@file:PuzzleName("Crossed Wires")

package de.skyrising.aoc2024.day24

import de.skyrising.aoc.*

val test = TestInput("""
x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj
""")

sealed interface Expression {
    fun get(wireSet: WireSet): Boolean
    fun isEquivalent(wireSet: WireSet, expr: Expression): Boolean
}
class Constant(val value: Boolean) : Expression {
    override fun get(wireSet: WireSet) = value
    override fun isEquivalent(wireSet: WireSet, expr: Expression) = this === expr
    override fun toString() = if (value) "1" else "0"
}
enum class Operator(val op: (Boolean, Boolean) -> Boolean) {
    AND(Boolean::and),
    OR(Boolean::or),
    XOR(Boolean::xor),
}
class Operation(val a: String, val b: String, val op: Operator) : Expression {
    override fun get(wireSet: WireSet): Boolean {
        val aValue = wireSet[a].get(wireSet)
        val bValue = wireSet[b].get(wireSet)
        return op.op(aValue, bValue)
    }
    override fun isEquivalent(wireSet: WireSet, expr: Expression): Boolean {
        if (expr !is Operation) return false
        if (op != expr.op) return false
        if (this === expr) return true
        val a1 = wireSet[a]
        val b1 = wireSet[b]
        val a2 = wireSet[expr.a]
        val b2 = wireSet[expr.b]
        return (a1.isEquivalent(wireSet, a1) && b1.isEquivalent(wireSet, b2)) || (a1.isEquivalent(wireSet, b2) && b1.isEquivalent(wireSet, a2))
    }

    override fun toString() = "$a $op $b"
}

class WireSet(val expressions: MutableMap<String, Expression>) {
    val x = expressions.entries.filter { it.key[0] == 'x' }.sortedBy { it.key.substring(1).toInt() }
    val y = expressions.entries.filter { it.key[0] == 'y' }.sortedBy { it.key.substring(1).toInt() }
    val z = expressions.entries.filter { it.key[0] == 'z' }.sortedBy { it.key.substring(1).toInt() }
    val swapped = sortedSetOf<String>()

    operator fun get(name: String) = expressions[name]!!
    operator fun get(expr: Expression) = expressions.entries.firstOrNull { it.value.isEquivalent(this, expr) }?.key
        //?.also { println("$expr -> $it") }
        ?: throw IllegalStateException("$expr not found")

    fun swap(a: String, b: String) {
        if (!swapped.add(a)) throw IllegalArgumentException("Already swapped $a")
        if (!swapped.add(b)) throw IllegalArgumentException("Already swapped $b")
        // println("swap($a, $b)")
        val aExpr = expressions[a]!!
        val bExpr = expressions[b]!!
        expressions[b] = aExpr
        expressions[a] = bExpr
    }
}

private fun parse(lines: List<String>): WireSet {
    val (start, ops) = lines.splitOnEmpty(2)
    val expressions = mutableMapOf<String, Expression>()
    for (line in start) {
        val (name, value) = line.split(": ")
        expressions[name] = Constant(value == "1")
    }
    for (line in ops) {
        val (a, opStr, b, _, out) = line.split(" ")
        val op = Operator.valueOf(opStr)
        expressions[out] = Operation(a, b, op)
    }
    return WireSet(expressions)
}

fun PuzzleInput.part1(): Any {
    val wireSet = parse(lines)
    return wireSet.z.mapIndexed { idx, (_, expr) -> expr.get(wireSet).toInt().toLong() shl idx }.sum()
}

fun PuzzleInput.part2(): Any {
    val wireSet = parse(lines)
    // TODO: detect this automatically
    wireSet.swap("wkr", "nvr")
    var carryOut: String? = null
    for ((i, z) in wireSet.z.withIndex()) {
        do {
            var swaps = 0
            if (i == wireSet.z.size - 1) {
                if (z.key != carryOut) {
                    wireSet.swap(z.key, carryOut!!)
                    swaps++
                }
                continue
            }
            val x = wireSet.x[i]
            val y = wireSet.y[i]
            val xor1 = wireSet[Operation(x.key, y.key, Operator.XOR)]
            val and1 = wireSet[Operation(x.key, y.key, Operator.AND)]
            if (i == 0) {
                if (z.key != xor1) {
                    wireSet.swap(z.key, xor1)
                    swaps++
                    continue
                }
                carryOut = and1
                continue
            }
            val carryIn = carryOut!!
            val xor2 = wireSet[Operation(carryIn, xor1, Operator.XOR)]
            if (z.key != xor2) {
                wireSet.swap(z.key, xor2)
                swaps++
                continue
            }
            val and2 = wireSet[Operation(carryIn, xor1, Operator.AND)]
            val or1 = wireSet[Operation(and1, and2, Operator.OR)]
            carryOut = or1
        } while (swaps != 0)
    }
    return wireSet.swapped.joinToString(",")
}
