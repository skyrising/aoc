package de.skyrising.aoc

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

data class PuzzleDay(val year: Int, val day: Int) : Comparable<PuzzleDay> {
    val releaseTime: Instant get() = LocalDate.of(year, 12, day).atStartOfDay().toInstant(ZoneOffset.of("-05:00"))
    val released get() = releaseTime.isBefore(Instant.now())

    override fun compareTo(other: PuzzleDay) =
        compareBy<PuzzleDay>(
            { it.year },
            { it.day }
        ).compare(this, other)

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
        compareBy<Puzzle<*>>(
            { it.day },
            { it.part },
            { it.name },
            { it.index }
        ).compare(this, other)
}

@JvmInline
value class PuzzleCollection(private val puzzles: SortedMap<PuzzleDay, MutableList<Puzzle<*>>> = TreeMap()) : SortedMap<PuzzleDay, MutableList<Puzzle<*>>> by puzzles {
    operator fun get(year: Int, day: Int): List<Puzzle<*>> = this[PuzzleDay(year, day)] ?: throw NoSuchElementException()
    operator fun get(range: ClosedRange<PuzzleDay>) = PuzzleCollection(subMap(range.start, range.endInclusive + 1))
    operator fun get(year: Int) = this[PuzzleDay(year, 1)..PuzzleDay(year, 25)]

    fun add(puzzle: Puzzle<*>) {
        puzzles.computeIfAbsent(puzzle.day) { mutableListOf() }.add(puzzle)
    }

    fun filter(filter: PuzzleFilter) = if (filter.latestOnly) {
        puzzles.reversed().entries.find { it.key in filter.days && it.key.released }?.value ?: emptyList()
    } else {
        puzzles.filterKeys { it in filter.days }.values.flatten()
    }
}

val allPuzzles = PuzzleCollection()

var currentDay = PuzzleDay(0, 0)
var lastPart = 0
var currentIndex = 0

data class DefaultPuzzle<T>(
    override val name: String,
    override val day: PuzzleDay,
    override val part: Int,
    override val index: Int,
    val run: PuzzleInput.() -> T
) : Puzzle<T> {
    override fun runPuzzle(input: PuzzleInput): T = run(input)
}

inline fun <T> puzzle(name: String, part: Int = 0, noinline run: PuzzleInput.() -> T): Puzzle<T> {
    if (part != lastPart) {
        currentIndex = 0
        lastPart = part
    }
    return DefaultPuzzle(name, currentDay, part, currentIndex++, run).also(allPuzzles::add)
}

inline fun <T> part1(name: String, noinline run: PuzzleInput.() -> T) = puzzle(name, 1, run)
inline fun <T> part2(name: String = "Part Two", noinline run: PuzzleInput.() -> T) = puzzle(name, 2, run)

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