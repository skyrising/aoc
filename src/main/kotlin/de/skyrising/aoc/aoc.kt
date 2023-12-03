package de.skyrising.aoc

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.*
import kotlin.math.sqrt

interface Puzzle<T> : Comparable<Puzzle<T>> {
    val name: String
    val year: Int
    val day: Int
    val part: Int
    val index: Int
    fun getRealInput() = getInput(year, day)
    fun runPuzzle(input: PuzzleInput): T

    override fun compareTo(other: Puzzle<T>) =
        Comparator.comparing(Puzzle<*>::year)
            .thenComparing(Puzzle<*>::day)
            .thenComparing(Puzzle<*>::part)
            .thenComparing(Puzzle<*>::name)
            .thenComparing(Puzzle<*>::index)
            .compare(this, other)
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

typealias DayPuzzles = MutableList<Puzzle<*>>
typealias YearPuzzles = TreeMap<Int, DayPuzzles>

val allPuzzles = TreeMap<Int, YearPuzzles>()

fun register(puzzle: Puzzle<*>) {
    allPuzzles.computeIfAbsent(puzzle.year) { TreeMap() }.computeIfAbsent(puzzle.day) { mutableListOf() }.add(puzzle)
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

var currentYear = 0
var currentDay = 0
var lastPart = 0
var currentIndex = 0

inline fun <T> puzzle(name: String, part: Int = 0, crossinline run: PuzzleInput.() -> T): Puzzle<T> {
    if (part != lastPart) {
        currentIndex = 0
        lastPart = part
    }
    return object : Puzzle<T> {
        override val name = name
        override val year = currentYear
        override val day = currentDay
        override val part = part
        override val index = currentIndex++
        override fun runPuzzle(input: PuzzleInput): T = run(input)
    }.also(::register)
}

inline fun <T> part1(name: String, crossinline run: PuzzleInput.() -> T) = puzzle(name, 1, run)
inline fun <T> part2(name: String = "Part Two", crossinline run: PuzzleInput.() -> T) = puzzle(name, 2, run)

fun registerYear(year: Int) {
    val pkg = "de.skyrising.aoc$year"
    currentYear = year
    for (day in 1..25) {
        currentDay = day
        lastPart = 0
        try {
            val cls = Class.forName("$pkg.Day${day}Kt")
            cls.getMethod("registerDay$day").invoke(null)
        } catch (ignored: ClassNotFoundException) {}
    }
    currentYear = 0
    currentDay = 0
    currentIndex = 0
}

private var registeredAll = false
fun registerAll() {
    if (registeredAll) return
    registeredAll = true
    for (year in 2015..LocalDate.now().year) {
        registerYear(year)
    }
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
    for (puzzle in puzzlesToRun) {
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
            println(String.format(Locale.ROOT, "%d/%02d/%d.%d %-26s: %16s, %s ± %4.1f%%",
                puzzle.year,
                puzzle.day,
                puzzle.part,
                puzzle.index,
                puzzle.name,
                puzzle.runPuzzle(input),
                formatTime(avg),
                stddev * 100 / avg
            ))
        } else {
            val start = System.nanoTime()
            val result = puzzle.runPuzzle(input)
            val time = (System.nanoTime() - start) / 1000.0
            println(String.format(Locale.ROOT, "%d/%02d/%d.%d %-26s: %16s, %s ± ?",
                puzzle.year,
                puzzle.day,
                puzzle.part,
                puzzle.index,
                puzzle.name,
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
