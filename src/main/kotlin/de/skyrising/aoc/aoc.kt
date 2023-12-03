package de.skyrising.aoc

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.math.sqrt

data class PuzzleDay(val year: Int, val day: Int) : Comparable<PuzzleDay> {
    val releaseTime: Instant get() = LocalDate.of(year, 12, day).atStartOfDay().toInstant(ZoneOffset.of("-05:00"))
    val released get() = releaseTime.isBefore(Instant.now())

    override fun compareTo(other: PuzzleDay) =
        Comparator.comparing(PuzzleDay::year)
            .thenComparing(PuzzleDay::day)
            .compare(this, other)

    operator fun plus(days: Int) = PuzzleDay(year, day + days)
}

interface Puzzle<T> : Comparable<Puzzle<T>> {
    val name: String
    val day: PuzzleDay
    val part: Int
    val index: Int
    fun getRealInput() = getInput(day.year, day.day)
    fun runPuzzle(input: PuzzleInput): T

    override fun compareTo(other: Puzzle<T>) =
        Comparator.comparing(Puzzle<*>::day)
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

@JvmInline
value class PuzzleCollection(private val puzzles: SortedMap<PuzzleDay, MutableList<Puzzle<*>>> = TreeMap()) : SortedMap<PuzzleDay, MutableList<Puzzle<*>>> by puzzles {
    operator fun get(year: Int, day: Int): List<Puzzle<*>> = this[PuzzleDay(year, day)] ?: throw NoSuchElementException()
    operator fun get(range: ClosedRange<PuzzleDay>) = PuzzleCollection(subMap(range.start, range.endInclusive + 1))
    operator fun get(year: Int) = this[PuzzleDay(year, 1)..PuzzleDay(year, 25)]

    fun add(puzzle: Puzzle<*>) {
        computeIfAbsent(puzzle.day) { mutableListOf() }.add(puzzle)
    }

    fun filter(filter: PuzzleFilter) = if (filter.latestOnly) {
        reversed().entries.find { it.key in filter.days && it.key.released }?.value ?: emptyList()
    } else {
        puzzles.filterKeys { it in filter.days }.values.flatten()
    }
}

val allPuzzles = PuzzleCollection()

inline fun <T> getInput(input: ByteBuffer, lastInput: MutableBox<Pair<ByteBuffer, T>?>, noinline fn: (ByteBuffer) -> T): T {
    val value = lastInput.value
    if (value == null || value.first !== input) {
        val result = fn(input)
        lastInput.value = Pair(input, result)
        return result
    }
    return value.second
}

var currentDay = PuzzleDay(0, 0)
var lastPart = 0
var currentIndex = 0

inline fun <T> puzzle(name: String, part: Int = 0, crossinline run: PuzzleInput.() -> T): Puzzle<T> {
    if (part != lastPart) {
        currentIndex = 0
        lastPart = part
    }
    return object : Puzzle<T> {
        override val name = name
        override val day = currentDay
        override val part = part
        override val index = currentIndex++
        override fun runPuzzle(input: PuzzleInput): T = run(input)
    }.also(allPuzzles::add)
}

inline fun <T> part1(name: String, crossinline run: PuzzleInput.() -> T) = puzzle(name, 1, run)
inline fun <T> part2(name: String = "Part Two", crossinline run: PuzzleInput.() -> T) = puzzle(name, 2, run)

fun registerDay(day: PuzzleDay) {
    lastPart = 0
    currentDay = day
    try {
        val cls = Class.forName("de.skyrising.aoc${day.year}.Day${day.day}Kt")
        cls.getMethod("registerDay${day.day}").invoke(null)
    } catch (ignored: ClassNotFoundException) {}
}

data class PuzzleFilter(val days: SortedSet<PuzzleDay>, val latestOnly: Boolean = false) {
    companion object {
        fun all() = PuzzleFilter((2015..LocalDate.now().year).flatMap { year -> (1..25).map { day -> PuzzleDay(year, day) } }.toSortedSet(), false)
        fun year(year: Int) = PuzzleFilter((1..25).map { PuzzleDay(year, it) }.toSortedSet(), false)
    }
}

fun registerFiltered(filter: PuzzleFilter) {
    for (day in filter.days.reversed()) {
        if (!day.released) continue
        registerDay(day)
        if (filter.latestOnly && day in allPuzzles) break
    }
}

const val RUNS = 10
const val WARMUP = 5
const val MEASURE_ITERS = 10
const val BENCHMARK = false

fun buildFilter(args: Array<String>): PuzzleFilter {
    if (args.isEmpty()) return PuzzleFilter.all().copy(latestOnly = true)
    if (args[0] == "all") return PuzzleFilter.all()
    val year = args[0].toInt()
    val filter = PuzzleFilter.year(year)
    if (args.size <= 1) return filter.copy(latestOnly = true)
    if (args[1] == "all") return filter
    return PuzzleFilter(sortedSetOf(PuzzleDay(year, args[1].toInt())))
}

fun main(args: Array<String>) {
    val filter = buildFilter(args)
    registerFiltered(filter)
    val puzzlesToRun = allPuzzles.filter(filter)
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
                puzzle.day.year,
                puzzle.day.day,
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
                puzzle.day.year,
                puzzle.day.day,
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
