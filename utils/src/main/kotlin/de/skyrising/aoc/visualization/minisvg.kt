package de.skyrising.aoc.visualization

import de.skyrising.aoc.Vec2d
import org.w3c.dom.Element
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Ellipse2D
import java.awt.geom.Path2D
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.ceil
import kotlin.math.tan

fun parseSimpleSVG(input: InputStream): SVG {
    val document = DocumentBuilderFactory.newInstance().apply {
        isIgnoringComments = true
        isValidating = false
    }.newDocumentBuilder().parse(input)
    val svg = document.documentElement
    val (x, y, width, height) = svg.getAttribute("viewBox").split(" ").map { it.toDouble() }
    val children = mutableListOf<SVGBox>()
    for (i in 0 until svg.childNodes.length) {
        val child = svg.childNodes.item(i)
        if (child is Element) {
            val box = parseElement(child)
            if (box != null) children.add(box)
        }
    }
    return SVG(x, y, width, height, children)
}

interface SVGBox {
    fun draw(g: Graphics2D)
    fun forEach(f: (SVGBox) -> Unit) {
        f(this)
    }
}
class SVG(var x: Double, var y: Double, var width: Double, var height: Double, var children: MutableList<SVGBox>) :
    SVGBox {
    fun drawToImage(scale: Double = 1.0): BufferedImage {
        val image = BufferedImage(ceil(width * scale).toInt(), ceil(height * scale).toInt(), BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.scale(scale, scale)
        draw(g)
        g.dispose()
        return image
    }

    override fun draw(g: Graphics2D) {
        g.translate(-x, -y)
        for (child in children) child.draw(g)
        g.translate(x, y)
    }

    override fun forEach(f: (SVGBox) -> Unit) {
        f(this)
        for (child in children) child.forEach(f)
    }

    override fun toString() = "SVG(x=$x, y=$y, width=$width, height=$height)"
}
class SVGGroup(var id: String, var classes: MutableList<String>, var children: MutableList<SVGBox>, val transform: AffineTransform = AffineTransform(), var title: String? = null) :
    SVGBox {
    override fun draw(g: Graphics2D) {
        val t = g.transform
        g.transform(transform)
        for (child in children) child.draw(g)
        g.transform = t
    }

    override fun forEach(f: (SVGBox) -> Unit) {
        f(this)
        for (child in children) child.forEach(f)
    }

    override fun toString() = "Group(id=$id, title=$title, classes=$classes, transform=$transform)"
}
class SVGPath(var fill: Color?, var stroke: Color?, var path: Path2D) : SVGBox {
    constructor(fill: Color?, stroke: Color?, shape: Shape) : this(fill, stroke, Path2D.Double().apply { append(shape, false) })
    override fun draw(g: Graphics2D) {
        if (fill != null) {
            g.color = fill
            g.fill(path)
        }
        if (stroke != null) {
            g.color = stroke
            g.draw(path)
        }
    }

    override fun toString() = "Path(fill=$fill, stroke=$stroke, path=$path)"
}
class SVGText(var x: Double, var y: Double, var text: String, var fill: Color, var anchor: Anchor, var fontSize: Double) :
    SVGBox {
    enum class Anchor {
        START, MIDDLE, END
    }
    override fun draw(g: Graphics2D) {
        g.color = fill
        g.font = g.font.deriveFont(fontSize.toFloat())
        val x = when (anchor) {
            Anchor.START -> x
            Anchor.MIDDLE -> x - g.fontMetrics.stringWidth(text) / 2.0
            Anchor.END -> x - g.fontMetrics.stringWidth(text)
        }
        g.drawString(text, x.toFloat(), y.toFloat())
    }
}

private fun parseElement(el: Element): SVGBox? {
    return when (el.tagName) {
        "g" -> parseGroup(el)
        "polygon" -> parsePolygon(el)
        "path" -> parsePath(el)
        "text" -> parseText(el)
        "ellipse" -> parseEllipse(el)
        else -> null
    }
}

private fun Element.getAttributeOrNull(name: String): String? {
    if (!hasAttribute(name)) return null
    return getAttribute(name)
}

private fun parseTransform(s: String): AffineTransform {
    val t = AffineTransform()
    for (part in s.split(")")) {
        if (!part.contains('(')) continue
        val (name, args) = part.split("(")
        val values = args.split(',', ' ').map { it.toDouble() }
        when (name.trim()) {
            "matrix" -> t.concatenate(AffineTransform(values[0], values[1], values[2], values[3], values[4], values[5]))
            "translate" -> t.translate(values[0], values[1])
            "scale" -> t.scale(values[0], values[1])
            "rotate" -> t.rotate(Math.toRadians(values[0]), 0.0, 0.0)
            "skewX" -> t.shear(tan(Math.toRadians(values[0])), 0.0)
            "skewY" -> t.shear(0.0, tan(Math.toRadians(values[0])))
        }
    }
    return t
}

private fun parseGroup(group: Element): SVGGroup {
    val id = group.getAttribute("id")
    val classes = group.getAttribute("class").split(" ").toMutableList()
    val transform = group.getAttributeOrNull("transform") ?.let(::parseTransform) ?: AffineTransform()
    val children = group.childNodes
    val g = SVGGroup(id, classes, ArrayList(children.length), transform)
    for (i in 0 until children.length) {
        val child = children.item(i)
        if (child is Element) {
            if (child.tagName == "title") {
                g.title = child.textContent
                continue
            }
            val box = parseElement(child)
            if (box != null) g.children.add(box)
        }
    }
    return g
}

private fun parseColor(color: String): Color? {
    if (color.isBlank() || color == "none") return null
    if (color.startsWith("#")) return Color(color.substring(1).toInt(16))
    return when (color) {
        "black" -> Color.black
        "white" -> Color.white
        "red" -> Color.red
        "green" -> Color.green
        "blue" -> Color.blue
        "cyan" -> Color.cyan
        "magenta" -> Color.magenta
        "yellow" -> Color.yellow
        "gray" -> Color.gray
        "grey" -> Color.gray
        "lightgray" -> Color.lightGray
        "lightgrey" -> Color.lightGray
        "darkgray" -> Color.darkGray
        "darkgrey" -> Color.darkGray
        "orange" -> Color.orange
        "pink" -> Color.pink
        else -> throw IllegalArgumentException("Unknown color $color")//Color.black
    }
}

private fun parsePolygon(polygon: Element): SVGPath {
    val fill = polygon.getAttributeOrNull("fill")?.let(::parseColor)
    val stroke = polygon.getAttributeOrNull("stroke")?.let(::parseColor)
    val points = polygon.getAttribute("points").split(" ").map {
        val (x, y) = it.split(",").map(String::toDouble)
        Vec2d(x, y)
    }
    return SVGPath(fill, stroke, Path2D.Double().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            lineTo(points[i].x, points[i].y)
        }
        closePath()
    })
}

private fun parseEllipse(ellipse: Element): SVGPath {
    val fill = ellipse.getAttributeOrNull("fill")?.let(::parseColor)
    val stroke = ellipse.getAttributeOrNull("stroke")?.let(::parseColor)
    val cx = ellipse.getAttribute("cx").toDouble()
    val cy = ellipse.getAttribute("cy").toDouble()
    val rx = ellipse.getAttribute("rx").toDouble()
    val ry = ellipse.getAttribute("ry").toDouble()
    return SVGPath(fill, stroke, Ellipse2D.Double(cx - rx, cy - ry, rx * 2, ry * 2))
}

private fun parseText(text: Element): SVGText {
    val x = text.getAttribute("x").toDouble()
    val y = text.getAttribute("y").toDouble()
    val fill = parseColor(text.getAttribute("fill")) ?: Color.black
    val anchor = when (text.getAttribute("text-anchor")) {
        "start" -> SVGText.Anchor.START
        "middle" -> SVGText.Anchor.MIDDLE
        "end" -> SVGText.Anchor.END
        else -> SVGText.Anchor.START
    }
    val fontSize = text.getAttributeOrNull("font-size")?.toDouble() ?: 12.0
    return SVGText(x, y, text.textContent, fill, anchor, fontSize)
}

private fun parsePathData(d: String) = Path2D.Double().apply {
    var i = 0
    fun consumeWhitespace() {
        while (i < d.length && d[i].isWhitespace()) i++
    }
    fun consumeWhitespaceWithComma(): Boolean {
        consumeWhitespace()
        if (i < d.length && d[i] == ',') {
            i++
            consumeWhitespace()
            return true
        }
        return false
    }
    fun parseNumber(): Double? {
        var j = i
        var dots = 0
        if (d[j] == '-') j++
        while (j < d.length) {
            if (d[j] == '.') {
                if (dots++ > 0) break
            } else if (!d[j].isDigit()) {
                break
            }
            j++
        }
        return try {
            d.substring(i, j).toDouble()
        } catch (_: NumberFormatException) {
            null
        } finally {
            i = j
        }
    }
    fun parseCoordinatePair(): Vec2d? {
        val x = parseNumber() ?: return null
        consumeWhitespaceWithComma()
        val y = parseNumber() ?: return null
        return Vec2d(x, y)
    }
    while (i < d.length) {
        consumeWhitespace()
        val c = d[i]
        i++
        val l = c.lowercaseChar()
        val base = if (c == l) currentPoint.run { Vec2d(x, y) } else Vec2d(0.0, 0.0)
        when (l) {
            'm' -> {
                consumeWhitespace()
                val move = base + (parseCoordinatePair() ?: throw IllegalArgumentException("Invalid coordinate pair ${d.substring(i)}"))
                moveTo(move.x, move.y)
            }
            'l' -> {
                consumeWhitespace()
                val line = base + (parseCoordinatePair() ?: throw IllegalArgumentException("Invalid coordinate pair ${d.substring(i)}"))
                lineTo(line.x, line.y)
            }
            'c' -> {
                do {
                    consumeWhitespace()
                    val c1 = base + (parseCoordinatePair()
                        ?: throw IllegalArgumentException("Invalid coordinate pair ${d.substring(i)}"))
                    consumeWhitespaceWithComma()
                    val c2 = base + (parseCoordinatePair()
                        ?: throw IllegalArgumentException("Invalid coordinate pair ${d.substring(i)}"))
                    consumeWhitespaceWithComma()
                    val c3 = base + (parseCoordinatePair()
                        ?: throw IllegalArgumentException("Invalid coordinate pair ${d.substring(i)}"))
                    curveTo(c1.x, c1.y, c2.x, c2.y, c3.x, c3.y)
                    consumeWhitespace()
                } while (i < d.length && !d[i].isLetter())
            }
            'z' -> {
                closePath()
            }
            else -> throw IllegalArgumentException("Unknown path command: $c")
        }
    }
}


private fun parsePath(path: Element): SVGPath {
    val fill = path.getAttributeOrNull("fill")?.let(::parseColor)
    val stroke = path.getAttributeOrNull("stroke")?.let(::parseColor)
    val path2d = parsePathData(path.getAttribute("d"))
    return SVGPath(fill, stroke, path2d)
}