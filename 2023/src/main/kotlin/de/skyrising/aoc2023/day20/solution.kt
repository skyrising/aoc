package de.skyrising.aoc2023.day20

import de.skyrising.aoc.*
import de.skyrising.aoc.visualization.SVGGroup
import de.skyrising.aoc.visualization.SVGPath
import de.skyrising.aoc.visualization.SVGText
import de.skyrising.aoc.visualization.graphviz.renderGraphToSVG
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.Rectangle2D
import kotlin.math.ceil

enum class Pulse {
    HIGH, LOW, NONE
}
sealed interface Node {
    val outputs: List<String>
    fun onPulse(input: String, pulse: Pulse): Pulse
}
data class Broadcaster(override val outputs: List<String>) : Node {
    override fun onPulse(input: String, pulse: Pulse) = pulse
}
data class FlipFlop(override val outputs: List<String>, var on: Boolean = false) : Node {
    override fun onPulse(input: String, pulse: Pulse): Pulse {
        if (pulse == Pulse.HIGH) return Pulse.NONE
        on = !on
        return if (on) Pulse.HIGH else Pulse.LOW
    }
}
data class Conjunction(override val outputs: List<String>, val inputs: MutableSet<String> = mutableSetOf(), val lastHigh: MutableSet<String> = mutableSetOf()) : Node {
    val allHigh get() = lastHigh.size == inputs.size
    override fun onPulse(input: String, pulse: Pulse): Pulse {
        inputs.add(input)
        if (pulse == Pulse.LOW) {
            lastHigh -= input
        } else if (pulse == Pulse.HIGH) {
            lastHigh += input
        }
        return if (allHigh) Pulse.LOW else Pulse.HIGH
    }
}
data object Untyped : Node {
    override val outputs = emptyList<String>()
    override fun onPulse(input: String, pulse: Pulse) = Pulse.NONE
}

data class State(val nodes: Map<String, Node>) {
    inline fun pressButton(onPulse: (String,Pulse,String)->Unit) {
        val queue = ArrayDeque<Triple<String, String, Pulse>>()
        queue.add(Triple("button", "broadcaster", Pulse.LOW))
        while (queue.isNotEmpty()) {
            val (from, name, pulse) = queue.removeFirst()
            onPulse(from, pulse, name)
            val node = nodes[name] ?: Untyped
            val newPulse = node.onPulse(from, pulse)
            if (newPulse == Pulse.NONE) continue
            for (output in node.outputs) {
                queue.add(Triple(name, output, newPulse))
            }
        }
    }

    fun dot(): String {
        fun subgraph(start: String): MutableSet<String> {
            val queue = ArrayDeque<String>()
            val seen = mutableSetOf<String>()
            queue.add(start)
            while (queue.isNotEmpty()) {
                val name = queue.removeLast()
                if (!seen.add(name)) continue
                val node = nodes[name] ?: continue
                queue += node.outputs
            }
            return seen
        }
        val rxInputNode = nodes.entries.single {
            "rx" in it.value.outputs
        }.value as Conjunction
        val rxInputs = rxInputNode.inputs
        val sb = StringBuilder()
        sb.append("digraph g {\n")
        sb.append("node[style=filled]\n")
        sb.append("{ rank=source; broadcaster }\n")
        for (branch in nodes["broadcaster"]!!.outputs) {
            val subgraph = subgraph(branch)
            sb.append("{ cluster=true; ")
            for (node in subgraph) {
                if (node == "rx" || nodes[node]!!.outputs.contains("rx")) continue
                if (node in rxInputs) {
                    sb.append("{cluster=false; rank=sink; $node}; ")
                } else {
                    sb.append("$node; ")
                }
            }
            sb.append("}\n")
        }
        sb.append("{ rank=sink; rx[shape=hexagon,fillcolor=\"#79AC78\"] }\n")
        for ((name, node) in nodes) {
            when (node) {
                is FlipFlop -> sb.append("$name [shape=box,fillcolor=\"#7ED7C1\"]\n")
                is Conjunction -> sb.append("$name [shape=diamond,fillcolor=\"#DC8686\"]\n")
                is Broadcaster -> sb.append("$name [fillcolor=\"#B0D9B1\"]\n")
                else -> {}
            }
            for (output in node.outputs) {
                sb.append("$name -> $output\n")
            }
        }
        sb.append("}")
        return sb.toString()
    }
}

fun parse(input: PuzzleInput): State {
    val nodes = mutableMapOf<String, Node>()
    for (line in input.lines) {
        val (name, outputsS) = line.split(" -> ")
        val outputs = outputsS.split(", ")
        if (name == "broadcaster") {
            nodes[name] = Broadcaster(outputs)
            continue
        }
        nodes[name.substring(1)] = when (name[0]) {
            '&' -> Conjunction(outputs)
            '%' -> FlipFlop(outputs)
            else -> error("Unknown node type: $name")
        }
    }
    for ((name, node) in nodes) {
        for (output in node.outputs) {
            val target = nodes[output] ?: continue
            if (target is Conjunction) target.inputs += name
        }
    }
    return State(nodes)
}

val test = TestInput("""
    broadcaster -> a, b, c
    %a -> b
    %b -> c
    %c -> inv
    &inv -> a
""")

val test2 = TestInput("""
    broadcaster -> a
    %a -> inv, con
    &inv -> b
    %b -> con
    &con -> output
""")

@PuzzleName("Pulse Propagation")
fun PuzzleInput.part1(): Any {
    val state = parse(this)
    var low = 0L
    var high = 0L
    repeat(1000) {
        state.pressButton { _, pulse, _ ->
            if (pulse == Pulse.HIGH) high++
            else if (pulse == Pulse.LOW) low++
        }
    }
    return low * high
}

fun PuzzleInput.part2(): Any {
    val state = parse(this)
    val rxInputNode = state.nodes.entries.single {
        "rx" in it.value.outputs
    }.value as Conjunction
    val rxInputs = rxInputNode.inputs
    var i = 0L
    val lastSeen = Object2LongOpenHashMap<String>()
    while(true) {
        i++
        state.pressButton { _, pulse, to ->
            if (pulse == Pulse.LOW && to in rxInputs && to !in lastSeen) {
                lastSeen[to] = i
                if (lastSeen.size == rxInputs.size) {
                    return@part2 lastSeen.values.reduce(Long::lcm)
                }
            }
        }
    }
}

fun PuzzleInput.part2viz() = visualization {
    val state = parse(this@part2viz)
    val svg = renderGraphToSVG(state.dot())
    //video = true
    size = Vec2i(ceil(svg.width * 1.5).toInt(), ceil(svg.height * 1.5).toInt())
    val g = viz.g
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.scale(1.5, 1.5)
    val edges = mutableMapOf<Pair<String, String>, SVGGroup>()
    val nodes = mutableMapOf<String, SVGGroup>()
    svg.forEach {
        if (it is SVGGroup) {
            if (it.id.startsWith("edge")) {
                val (from, to) = it.title!!.split("->")
                edges[from to to] = it
            } else if (it.id.startsWith("node")){
                nodes[it.title!!] = it
            }
        }
    }
    val rxInputNode = state.nodes.entries.single {
        "rx" in it.value.outputs
    }
    val rxInputs = (rxInputNode.value as Conjunction).inputs
    var i = 0L
    val lastSeen = Object2LongOpenHashMap<String>()
    fun color(g: SVGGroup, color: Color) {
        g.forEach {
            if (it is SVGPath) {
                if (it.stroke != null) it.stroke = color
                if (it.fill != null) it.fill = color
            }
        }
    }
    fun fillColor(g: SVGGroup, color: Color) {
        g.forEach {
            if (it is SVGPath) it.fill = color
        }
    }
    fun draw() {
        svg.draw(g)
        g.font = g.font.deriveFont(20f)
        g.color = Color.BLACK
        val w = g.fontMetrics.stringWidth(i.toString())
        val counterPos = Vec2d(svg.width - w - 10.0, 30.0)
        g.drawString(i.toString(), counterPos.x.toFloat(), counterPos.y.toFloat())
    }
    fun textBox(textPos: Vec2d, s: String, size: Double, fill: Color?, stroke: Color?, color: Color = Color.BLACK, pad: Double = size / 4): Pair<SVGPath, SVGText> {
        val width = g.getFontMetrics(g.font.deriveFont(size.toFloat())).stringWidth(s) + pad
        val height = size + pad
        val box = SVGPath(Color(0xfec686), Color(0xdc6666), Rectangle2D.Double(textPos.x - width / 2, textPos.y - height + pad, width, height))
        val text = SVGText(textPos.x, textPos.y, s, Color.BLACK, SVGText.Anchor.MIDDLE, size)
        return box to text
    }
    while(true) {
        i++
        for (edge in edges.values) color(edge, Color.BLACK)
        state.pressButton { from, pulse, to ->
            val edge = edges[from to to]
            if (edge != null) color(edge, if (pulse == Pulse.HIGH) Color.RED else Color.BLUE)
            val node = state.nodes[to] ?: Untyped
            when (node) {
                is FlipFlop -> fillColor(nodes[to]!!, if (node.on) Color(0x7ed7c1) else Color(0x3eb7a1))
                is Conjunction -> fillColor(nodes[to]!!, if (node.allHigh) Color(0xdc8686) else Color(0xfec686))
                else -> {}
            }
            if (pulse == Pulse.LOW && to in rxInputs && to !in lastSeen) {
                lastSeen[to] = i
                val posA = (nodes[to]!!.children.first() as SVGPath).path.bounds2D.run { Vec2d(x + width / 2, y + height / 2) }
                val posB = (nodes[rxInputNode.key]!!.children.first() as SVGPath).path.bounds2D.run { Vec2d(x + width / 2, y + height / 2) }
                val t = 0.3
                val textPos = posA * (1 - t) + posB * t + Vec2d(0.0, 25.0)
                val (box, text) = textBox(textPos, i.toString(), 20.0, Color(0xfec686), Color(0xdc6666))
                (svg.children.first() as SVGGroup).children.addAll(listOf(box, text))
                draw()
                repeat(60) {
                    viz.present()
                }
                box.stroke = null
                if (lastSeen.size == rxInputs.size) {
                    val result = lastSeen.values.reduce(Long::lcm)
                    val bigBox = textBox(Vec2d(svg.width / 2.0, -svg.height / 2.0), result.toString(), 70.0, Color(0xfec686), Color(0xdc6666))
                    (svg.children.first() as SVGGroup).children.addAll(bigBox.toList())
                    draw()
                    repeat(120) {
                        viz.present()
                    }
                    return@visualization
                }
            }
        }
        if (i >= 3650) {
            draw()
            viz.present()
        }
    }
}
