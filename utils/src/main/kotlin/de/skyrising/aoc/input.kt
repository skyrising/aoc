package de.skyrising.aoc

import de.skyrising.aoc.visualization.DummyVisualization
import de.skyrising.aoc.visualization.RealVisualization
import de.skyrising.aoc.visualization.Visualization
import java.net.URL
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.LocalDate
import java.time.ZoneId
import kotlin.io.path.Path
import kotlin.io.path.exists

fun ByteBuffer.toLatin1String(): String {
    if (hasArray()) return String(array(), arrayOffset() + position(), remaining(), StandardCharsets.ISO_8859_1)
    val chars = ByteArray(remaining())
    get(position(), chars, 0, chars.size)
    return String(chars, StandardCharsets.ISO_8859_1)
}
fun calcInputS(input: ByteBuffer): CharBuffer = StandardCharsets.US_ASCII.decode(input.slice())
fun calcInputLS(input: ByteBuffer) = lineList(input).map { it.toLatin1String() }

interface PuzzleInput : AutoCloseable {
    var benchmark: Boolean
    val day: PuzzleDay
    val input: ByteBuffer
    val lines: List<String>
    val byteLines: List<ByteBuffer>
    val string: String
    val chars: CharBuffer
    val charGrid get() = CharGrid.parse(lines)
    val viz: Visualization
    val hasViz: Boolean
    var prepared: Lazy<Any?>?

    fun log(value: Any) {
        if (benchmark) return
        when (value) {
            is Array<*> -> println(value.contentToString())
            is ByteArray -> println(value.contentToString())
            is ShortArray -> println(value.contentToString())
            is IntArray -> println(value.contentToString())
            is LongArray -> println(value.contentToString())
            is FloatArray -> println(value.contentToString())
            is DoubleArray -> println(value.contentToString())
            is BooleanArray -> println(value.contentToString())
            is CharArray -> println(value.contentToString())
            is CharSequence -> println(value)
            else -> println(value.toString())
        }
    }

    fun visualization(block: Visualization.() -> Unit): Visualization? {
        if (benchmark) throw IllegalStateException()
        viz.block()
        return viz
    }
}

class RealInput(override val day: PuzzleDay, override val input: ByteBuffer, override var benchmark: Boolean = false) : PuzzleInput {
    override val lines by lazy { calcInputLS(input) }
    override val byteLines by lazy { lineList(input) }
    override val chars by lazy { calcInputS(input) }
    override val string by lazy { chars.toString() }
    private var lazyViz: Visualization? = null
    override val viz: Visualization
        get() {
            if (benchmark) return DummyVisualization
            if (lazyViz == null) lazyViz = RealVisualization(day)
            return lazyViz!!
        }
    override val hasViz: Boolean get() = lazyViz != null
    override var prepared: Lazy<Any?>? = null

    override fun close() {
        if (hasViz) viz.close()
        lazyViz = null
    }
}

class TestInput(str: String) : PuzzleInput {
    override var benchmark: Boolean = false
    override val string = str.trimIndent().trimEnd()
    override val lines by lazy { string.lines() }
    override val byteLines by lazy { lines.map { ByteBuffer.wrap(it.toByteArray()) } }
    override val chars: CharBuffer by lazy { CharBuffer.wrap(string) }
    override val input: ByteBuffer by lazy { ByteBuffer.wrap(string.toByteArray()) }
    override val day: PuzzleDay get() = throw UnsupportedOperationException()
    override val viz: Visualization get() = throw UnsupportedOperationException()
    override val hasViz: Boolean get() = false
    override var prepared: Lazy<Any?>? = null
    override fun close() {}
}

private val years = LocalDate.now(ZoneId.of("UTC")).year - 2015 + 1
private val inputs = arrayOfNulls<PuzzleInput>(years * 25)
private val cookie: String by lazy { Files.readString(Path("COOKIE.txt")).trim() }

fun getInput(year: Int, day: Int): PuzzleInput {
    val idx = (year - 2015) * 25 + day - 1
    return inputs[idx] ?: getInput0(year, day).also { inputs[idx] = it }
}

private fun getInput0(year: Int, day: Int): PuzzleInput {
    val cachePath = java.nio.file.Path.of("inputs", year.toString(), "$day.txt")
    if (cachePath.exists()) {
        return RealInput(PuzzleDay(year, day), ByteBuffer.wrap(Files.readAllBytes(cachePath)))
    }
    println("Downloading input for $year/$day")
    val connection = URL("https://adventofcode.com/${year}/day/$day/input").openConnection()
    connection.addRequestProperty("Cookie", cookie)
    connection.addRequestProperty("User-Agent", "github.com/skyrising/aoc simon@skyrising.xyz")
    val bytes = connection.getInputStream().readBytes()
    Files.createDirectories(cachePath.parent)
    Files.write(cachePath, bytes)
    return RealInput(PuzzleDay(year, day), ByteBuffer.wrap(bytes))
}
