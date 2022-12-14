package de.skyrising.aoc

import de.skyrising.aoc2015.register2015
import de.skyrising.aoc2020.register2020
import de.skyrising.aoc2021.register2021
import de.skyrising.aoc2022.register2022
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.sqrt
import kotlin.random.Random

interface Puzzle<T> : Comparable<Puzzle<T>> {
    fun getName(): String
    fun getYear(): Int
    fun getDay(): Int
    fun getRealInput() = getInput(getYear(), getDay())
    fun generateInput(rand: Random): Pair<ByteBuffer, T>?
    fun runPuzzle(input: PuzzleInput): T

    override fun compareTo(other: Puzzle<T>): Int {
        val yearCmp = getYear().compareTo(other.getYear())
        if (yearCmp != 0) return yearCmp
        val dayCmp = getDay().compareTo(other.getDay())
        if (dayCmp != 0) return dayCmp
        val part = if (getName().startsWith("Part Two")) 2 else 1
        val otherPart = if (other.getName().startsWith("Part Two")) 2 else 1
        if (part != otherPart) return part - otherPart
        return getName().compareTo(other.getName())
    }
}

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
    override val string by lazy { chars.toString() }
}

class TestInput(str: String) : PuzzleInput {
    override var benchmark: Boolean = false
    override val string: String = str.trimIndent().trimEnd()
    override val lines by lazy { string.lines() }
    override val byteLines by lazy { lines.map { ByteBuffer.wrap(it.toByteArray()) } }
    override val chars: CharBuffer by lazy { CharBuffer.wrap(string) }
    override val input: ByteBuffer by lazy { ByteBuffer.wrap(string.toByteArray()) }
}

val allPuzzles = TreeMap<Int, TreeMap<Int, MutableList<Puzzle<*>>>>()

fun register(puzzle: Puzzle<*>) {
    allPuzzles.computeIfAbsent(puzzle.getYear()) { TreeMap() }.computeIfAbsent(puzzle.getDay()) { mutableListOf() }.add(puzzle)
}

@Suppress("LeakingThis")
abstract class AbstractPuzzle<T>(private val year: Int, private val day: Int, private val name: String) : Puzzle<T> {
    override fun getName() = name
    override fun getYear() = year
    override fun getDay() = day
    override fun generateInput(rand: Random): Pair<ByteBuffer, T>? = null
    override fun toString() = "Puzzle(year=$year,day=$day,name=$name})"
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

inline fun <T> puzzle(year: Int, day: Int, name: String, crossinline run: PuzzleInput.() -> T): Puzzle<T> {
    return object : AbstractPuzzle<T>(year, day, name) {
        override fun runPuzzle(input: PuzzleInput): T = run(input)
    }.also(::register)
}

private var registeredAll = false
fun registerAll() {
    if (registeredAll) return
    registeredAll = true
    register2015()
    register2020()
    register2021()
    register2022()
}

const val RUNS = 10
const val WARMUP = 5
const val MEASURE_ITERS = 10
const val BENCHMARK = false

fun main(args: Array<String>) {
    registerAll()
    val puzzlesToRun = TreeSet<Puzzle<*>>()
    if (args.isNotEmpty()) {
        val yearPuzzles = allPuzzles[args[0].toInt()] ?: throw IllegalArgumentException(args[0])
        if (args.size > 1) {
            if (args[1] == "all") {
                for (day in yearPuzzles.values) {
                    puzzlesToRun.addAll(day)
                }
            } else {
                puzzlesToRun.addAll(yearPuzzles[args[1].toInt()] ?: throw IllegalArgumentException(args[1]))
            }
        } else {
            puzzlesToRun.addAll(yearPuzzles.lastEntry().value)
        }
    } else {
        for (year in allPuzzles.values) {
            for (day in year.values) {
                puzzlesToRun.addAll(day)
            }
        }
    }
    var year: Int? = null
    var day: Int? = null
    for (puzzle in puzzlesToRun) {
        if (puzzle.getYear() != year) {
            year = puzzle.getYear()
            day = null
            println("$year:")
        }
        if (puzzle.getDay() != day) {
            day = puzzle.getDay()
            println("Day $day:")
        }
        val input = puzzle.getRealInput()
        if (BENCHMARK) {
            input.benchmark = true
            repeat(WARMUP) {
                measure(RUNS) { puzzle.runPuzzle(input) }
            }
            val times = DoubleArray(MEASURE_ITERS) {
                measure(RUNS) { puzzle.runPuzzle(input) }
            }
            input.benchmark = false
            val avg = times.average()
            val stddev = sqrt(times.map { (it - avg) * (it - avg) }.average())
            println(String.format(Locale.ROOT, "%-26s: %16s, %s ± %4.1f%%",
                puzzle.getName(),
                puzzle.runPuzzle(input),
                formatTime(avg),
                stddev * 100 / avg
            ))
        } else {
            val start = System.nanoTime()
            val result = puzzle.runPuzzle(input)
            val time = (System.nanoTime() - start) / 1000.0
            println(String.format(Locale.ROOT, "%-26s: %16s, %s ± ?",
                puzzle.getName(),
                result,
                formatTime(time)
            ))
        }
    }
}

private fun formatTime(us: Double): String {
    if (us < 1000) return "%7.3fµs".format(us)
    val ms = us / 1000
    if (ms < 1000) return "%7.3fms".format(ms)
    return "%7.3fs ".format(ms / 1000)
}
