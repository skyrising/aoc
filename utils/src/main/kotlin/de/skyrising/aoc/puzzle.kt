package de.skyrising.aoc

import de.skyrising.aoc.visualization.Visualization
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType.methodType
import java.lang.reflect.AccessFlag
import java.lang.reflect.Method
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

annotation class PuzzleName(val name: String, val part: Int = 0)
enum class SolutionType {
    REGULAR,
    C2,
    VISUALIZATION,
}
annotation class Solution(val type: SolutionType = SolutionType.REGULAR)

interface Puzzle<T> : Comparable<Puzzle<T>> {
    val name: String
    val day: PuzzleDay
    val part: Int
    val index: Int
    val resultType: Class<T>
    val solutionType: SolutionType
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
        puzzles.computeIfAbsent(puzzle.day) { mutableListOf() }.run {
            add(puzzle)
            sortBy { it.part }
        }
    }

    fun filter(filter: PuzzleFilter): List<Puzzle<*>> {
        val days = if (filter.latestOnly) {
            puzzles.reversed().entries.find { it.key in filter.days && it.key.released }?.value ?: emptyList()
        } else {
            puzzles.filterKeys { it in filter.days }.values.flatten()
        }
        val withType = days.filter { it.solutionType in filter.solutionTypes }
        return if (filter.bestVersionOnly) {
            withType.groupBy { it.day to it.part }.values.map { it.maxBy(Puzzle<*>::index) }
        } else {
            withType
        }
    }
}

val allPuzzles = PuzzleCollection()

var currentDay = PuzzleDay(0, 0)
var lastPart = 0

data class DefaultPuzzle<T>(
    override val name: String,
    override val day: PuzzleDay,
    override val part: Int,
    override val index: Int,
    override val solutionType: SolutionType,
    override val resultType: Class<T>,
    val run: PuzzleInput.() -> T
) : Puzzle<T> {
    override fun runPuzzle(input: PuzzleInput): T = run(input)
}

inline fun <reified T> puzzle(name: String, part: Int = 0, solutionType: SolutionType, resultType: Class<T> = T::class.java, noinline run: PuzzleInput.() -> T): Puzzle<T> {
    if (part != lastPart) {
        lastPart = part
    }
    val index = allPuzzles[currentDay]?.count { it.part == part } ?: 0
    return DefaultPuzzle(name, currentDay, part, index, solutionType, resultType, run).also(allPuzzles::add)
}

private inline fun <reified T> convertMethod(lookup: MethodHandles.Lookup, method: Method): T {
    val itf = T::class.java
    val target = itf.methods.find { AccessFlag.ABSTRACT in it.accessFlags() } ?: error("No abstract methods in interface")
    val mh = lookup.unreflect(method)
    val targetType = lookup.unreflect(target).type().dropParameterTypes(0, 1)
    return LambdaMetafactory.metafactory(
        lookup,
        target.name,
        methodType(itf),
        targetType,
        mh,
        mh.type()
    ).target.invoke() as T
}

fun registerDay(day: PuzzleDay) {
    lastPart = 0
    currentDay = day
    try {
        val cls = Class.forName("de.skyrising.aoc${day.year}.day${day.day}.SolutionKt")
        val lookup = MethodHandles.lookup()
        for (method in cls.methods) {
            if (AccessFlag.STATIC !in method.accessFlags() || !method.name.startsWith("part")) continue
            if (method.parameterCount == 1 && method.parameterTypes[0] == PuzzleInput::class.java) {
                var part = 0
                if (method.name.startsWith("part")) part = method.name[4].digitToInt()
                var name = ""
                method.getAnnotation(PuzzleName::class.java)?.let {
                    if (it.part != 0) part = it.part
                    name = it.name
                }
                if (name.isEmpty() && part == 2) name = "Part Two"
                var solutionType = method.getAnnotation(Solution::class.java)?.type ?: SolutionType.REGULAR
                if (method.returnType == Visualization::class.java) solutionType = SolutionType.VISUALIZATION
                puzzle(name, part, solutionType, method.returnType as Class<Any?>, convertMethod<Function1<PuzzleInput, Any?>>(lookup, method))
            }
        }
    } catch (ignored: ClassNotFoundException) {}
}

data class PuzzleFilter(
    val days: SortedSet<PuzzleDay>,
    val latestOnly: Boolean = false,
    val bestVersionOnly: Boolean = false,
    val solutionTypes: EnumSet<SolutionType> = EnumSet.of(SolutionType.REGULAR, SolutionType.C2)
) {
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
