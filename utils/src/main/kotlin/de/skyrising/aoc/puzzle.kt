package de.skyrising.aoc

import de.skyrising.aoc.visualization.Visualization
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType.methodType
import java.lang.reflect.AccessFlag
import java.lang.reflect.Method
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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

@Target(AnnotationTarget.FILE)
annotation class PuzzleName(val name: String)
enum class SolutionType {
    REGULAR,
    C2,
    VISUALIZATION,
}
annotation class Solution(val type: SolutionType = SolutionType.REGULAR)

interface Puzzle<T, P> : Comparable<Puzzle<T, P>> {
    val name: String
    val day: PuzzleDay
    val part: Int
    val index: Int
    val resultType: Class<T>
    val solutionType: SolutionType
    val hasPrepare: Boolean
    fun getRealInput() = getInput(day.year, day.day)
    fun prepareInput(input: PuzzleInput): P
    fun runPuzzle(input: PuzzleInput): T

    override fun compareTo(other: Puzzle<T, P>) =
        compareBy<Puzzle<*, *>>(
            { it.day },
            { it.part },
            { it.name },
            { it.index }
        ).compare(this, other)
}

@JvmInline
value class PuzzleCollection(private val puzzles: SortedMap<PuzzleDay, MutableList<Puzzle<*, *>>> = TreeMap()) : SortedMap<PuzzleDay, MutableList<Puzzle<*, *>>> by puzzles {
    operator fun get(year: Int, day: Int): List<Puzzle<*, *>> = this[PuzzleDay(year, day)] ?: throw NoSuchElementException()
    operator fun get(range: ClosedRange<PuzzleDay>) = PuzzleCollection(subMap(range.start, range.endInclusive + 1))
    operator fun get(year: Int) = this[PuzzleDay(year, 1)..PuzzleDay(year, 25)]

    fun add(puzzle: Puzzle<*, *>) {
        puzzles.computeIfAbsent(puzzle.day) { mutableListOf() }.run {
            add(puzzle)
            sortBy { it.part }
        }
    }

    fun filter(filter: PuzzleFilter): List<Puzzle<*, *>> {
        val days = if (filter.latestOnly) {
            puzzles.reversed().entries.find { it.key in filter.days && it.key.released }?.value ?: emptyList()
        } else {
            puzzles.filterKeys { it in filter.days }.values.flatten()
        }
        val withType = days.filter { it.solutionType in filter.solutionTypes }
        return if (filter.bestVersionOnly) {
            withType.groupBy { it.day to it.part }.values.map { it.maxBy(Puzzle<*, *>::index) }
        } else {
            withType
        }
    }
}

val allPuzzles = PuzzleCollection()

data class DefaultPuzzle<T, P>(
    override val name: String,
    override val day: PuzzleDay,
    override val part: Int,
    override val index: Int,
    override val solutionType: SolutionType,
    override val resultType: Class<T>,
    val run: P.() -> T,
    val prepare: (PuzzleInput.() -> P)? = null,
) : Puzzle<T, P> {
    override val hasPrepare get() = prepare != null
    override fun prepareInput(input: PuzzleInput): P {
        return if (prepare == null) this as P else prepare.invoke(input)
    }
    override fun runPuzzle(input: PuzzleInput): T {
        val prepared = if (prepare != null) {
            if (input.prepared == null) input.prepared = lazy { prepare.invoke(input) }
            input.prepared!!.value
        } else {
            input
        }
        return run(prepared as P)
    }
}

data class MHPuzzle<T, P>(
    override val name: String,
    override val day: PuzzleDay,
    override val part: Int,
    override val index: Int,
    override val solutionType: SolutionType,
    override val resultType: Class<T>,
    val run: MethodHandle,
    val prepare: MethodHandle?,
) : Puzzle<T, P> {
    override val hasPrepare get() = prepare != null
    override fun prepareInput(input: PuzzleInput) = (prepare ?: MethodHandles.identity(PuzzleInput::class.java)).invoke(input) as P
    override fun runPuzzle(input: PuzzleInput): T {
        if (input.prepared == null) input.prepared = lazy { prepareInput(input) }
        return run.invoke(input.prepared!!.value) as T
    }
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

const val COMPILE_MH = false

fun compilePuzzle(lookup: MethodHandles.Lookup, day: PuzzleDay, dayName: String, part: Int, index: Int, method: Method, prepare: Method?): Puzzle<*, *> {
    var name = dayName
    if (part == 2) name = "Part Two"
    var solutionType = method.getAnnotation(Solution::class.java)?.type ?: SolutionType.REGULAR
    if (method.returnType == Visualization::class.java) solutionType = SolutionType.VISUALIZATION
    if (COMPILE_MH) {
        val runMH = lookup.unreflect(method)
        val prepareMH = prepare?.let(lookup::unreflect)
        return MHPuzzle<Any?, Any?>(
            name,
            day,
            part,
            index,
            solutionType,
            method.returnType as Class<Any?>,
            runMH,
            prepareMH
        )
    } else {
        val runMethod: Any?.() -> Any? = convertMethod(lookup, method)
        val prepareMethod: (PuzzleInput.() -> Any?)? = prepare?.let { convertMethod(lookup, it) }
        return DefaultPuzzle(
            name,
            day,
            part,
            index,
            solutionType,
            method.returnType as Class<Any?>,
            runMethod,
            prepareMethod
        )
    }
}

fun registerDay(day: PuzzleDay) {
    try {
        val cls = Class.forName(buildString(37) {
            append("de.skyrising.aoc")
            append(day.year)
            append(".day")
            append(day.day)
            append(".SolutionKt")
        })
        val dayName = cls.getAnnotation(PuzzleName::class.java)?.name ?: "Day ${day.day}"
        val lookup = MethodHandles.lookup()
        for (method in cls.methods) {
            if (AccessFlag.STATIC !in method.accessFlags() || !method.name.startsWith("part")) continue
            if (method.parameterCount == 1) {
                var part = 0
                if (method.name.startsWith("part")) part = method.name[4].digitToInt()
                val index = allPuzzles[day]?.count { it.part == part } ?: 0
                val prepare = if (method.parameterTypes[0] == PuzzleInput::class.java) {
                    null
                } else {
                    cls.getMethod("prepare", PuzzleInput::class.java)
                }
                compilePuzzle(lookup, day, dayName, part, index, method, prepare).also(allPuzzles::add)
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
        fun all() = PuzzleFilter((2015..LocalDate.now(ZoneId.of("UTC")).year).flatMap { year -> (1..25).map { day -> PuzzleDay(year, day) } }.toSortedSet(), false)
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
