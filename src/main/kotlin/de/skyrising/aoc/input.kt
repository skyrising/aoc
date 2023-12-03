package de.skyrising.aoc

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.net.URL
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.exists

var lastInputLB = MutableBox<Pair<ByteBuffer, List<ByteBuffer>>?>(null)
var lastInputS = MutableBox<Pair<ByteBuffer, CharBuffer>?>(null)
var lastInputLS = MutableBox<Pair<ByteBuffer, List<String>>?>(null)

fun calcInputS(input: ByteBuffer): CharBuffer = StandardCharsets.US_ASCII.decode(input.slice())
fun calcInputLS(input: ByteBuffer) = lineList(input).map { StandardCharsets.US_ASCII.decode(it.slice()).toString() }

interface PuzzleInput {
    var benchmark: Boolean
    val input: ByteBuffer
    val lines: List<String>
    val byteLines: List<ByteBuffer>
    val string: String
    val chars: CharBuffer
    val charGrid: CharGrid

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
}

class RealInput(override val input: ByteBuffer, override var benchmark: Boolean = false) : PuzzleInput {
    override val lines by lazy { getInput(input, lastInputLS, ::calcInputLS) }
    override val byteLines by lazy { getInput(input, lastInputLB, ::lineList) }
    override val chars by lazy { getInput(input, lastInputS, ::calcInputS) }
    override val charGrid: CharGrid by lazy { CharGrid.parse(lines) }
    override val string by lazy { chars.toString() }
}

class TestInput(str: String) : PuzzleInput {
    override var benchmark: Boolean = false
    override val string: String = str.trimIndent().trimEnd()
    override val lines by lazy { string.lines() }
    override val byteLines by lazy { lines.map { ByteBuffer.wrap(it.toByteArray()) } }
    override val chars: CharBuffer by lazy { CharBuffer.wrap(string) }
    override val charGrid: CharGrid by lazy { CharGrid.parse(lines) }
    override val input: ByteBuffer by lazy { ByteBuffer.wrap(string.toByteArray()) }
}

inline fun <T> getInput(input: ByteBuffer, lastInput: MutableBox<Pair<ByteBuffer, T>?>, noinline fn: (ByteBuffer) -> T): T {
    val value = lastInput.value
    if (value == null || value.first !== input) {
        val result = fn(input)
        lastInput.value = Pair(input, result)
        return result
    }
    return value.second
}

private val inputs: Int2ObjectMap<Int2ObjectMap<PuzzleInput>> = Int2ObjectOpenHashMap()
private val cookie: String by lazy { Files.readString(java.nio.file.Path.of("COOKIE.txt")) }

fun getInput(year: Int, day: Int): PuzzleInput = inputs.computeIfAbsent(year, Int2ObjectFunction {
    Int2ObjectOpenHashMap()
}).computeIfAbsent(day, Int2ObjectFunction {
    getInput0(year, it)
})

private fun getInput0(year: Int, day: Int): PuzzleInput {
    val cachePath = java.nio.file.Path.of("inputs", year.toString(), "$day.txt")
    if (cachePath.exists()) {
        return RealInput(ByteBuffer.wrap(Files.readAllBytes(cachePath)).asReadOnlyBuffer())
    }
    println("Downloading input for $year/$day")
    val connection = URL("https://adventofcode.com/${year}/day/$day/input").openConnection()
    connection.addRequestProperty("Cookie", cookie)
    connection.addRequestProperty("User-Agent", "github.com/skyrising/aoc simon@skyrising.xyz")
    val bytes = connection.getInputStream().readBytes()
    Files.createDirectories(cachePath.parent)
    Files.write(cachePath, bytes)
    return RealInput(ByteBuffer.wrap(bytes).asReadOnlyBuffer())
}