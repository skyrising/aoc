package de.skyrising.aoc2020

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.sqrt
import kotlin.random.Random

interface Puzzle<T> {
    fun getName(): String
    fun getDay(): Int
    fun getRealInput() = getInput(getDay())
    fun generateInput(rand: Random): Pair<ByteBuffer, T>?
    fun runPuzzle(input: ByteBuffer): T
}

val dailyPuzzles = TreeMap<Int, MutableList<Puzzle<*>>>()

fun register(puzzle: Puzzle<*>) {
    dailyPuzzles.computeIfAbsent(puzzle.getDay()) { mutableListOf() }.add(puzzle)
}

@Suppress("LeakingThis")
abstract class AbstractPuzzle<T>(private val day: Int, private val name: String) : Puzzle<T> {
    override fun getName() = name
    override fun getDay() = day
    override fun generateInput(rand: Random): Pair<ByteBuffer, T>? = null
}

inline fun <T> puzzleB(day: Int, name: String, crossinline run: (ByteBuffer) -> T): Puzzle<T> {
    val p = object : AbstractPuzzle<T>(day, name) {
        override fun runPuzzle(input: ByteBuffer) = run(input)
    }
    register(p)
    return p
}

inline fun <T> puzzleLB(day: Int, name: String, crossinline run: (List<ByteBuffer>) -> T): Puzzle<T> {
    val p = object : AbstractPuzzle<T>(day, name) {
        override fun runPuzzle(input: ByteBuffer) = run(lineList(input))
    }
    register(p)
    return p
}

var lastInput: Pair<ByteBuffer, List<String>>? = null
inline fun <T> puzzleLS(day: Int, name: String, crossinline run: (List<String>) -> T): Puzzle<T> {
    val p = object : AbstractPuzzle<T>(day, name) {
        override fun runPuzzle(input: ByteBuffer): T {
            if (lastInput == null || lastInput!!.first !== input) {
                lastInput = Pair(input, lineList(input).map { StandardCharsets.US_ASCII.decode(it.slice()).toString() })
                // println("Decoding ${System.identityHashCode(input)}")
            }
            return run(lastInput!!.second)
        }
    }
    register(p)
    return p
}

private var registeredAll = false
fun registerAll() {
    if (registeredAll) return
    registeredAll = true
    registerDay1()
    registerDay2()
    registerDay3()
    registerDay4()
    registerDay5()
    registerDay6()
    registerDay7()
    registerDay8()
}

const val RUNS = 1000
const val WARMUP = 14
const val MEASURE_ITERS = 10
const val BENCHMARK = true

fun main() {
    registerAll()
    for ((day, puzzles) in dailyPuzzles) {
        if (day != 8) continue
        println("Day $day:")
        for (puzzle in puzzles) {
            // if (!puzzle.getName().endsWith("v2")) continue
            val input = puzzle.getRealInput()
            if (BENCHMARK) {
                repeat(WARMUP) {
                    measure(RUNS) { puzzle.runPuzzle(input) }
                }
                val times = DoubleArray(MEASURE_ITERS) {
                    measure(RUNS) { puzzle.runPuzzle(input) }
                }
                val avg = times.average()
                val stddev = sqrt(times.map { (it - avg) * (it - avg) }.average())
                println(String.format(Locale.ROOT, "%-22s: %-10s, %8.3f ± %6.3fµs",
                    puzzle.getName(),
                    puzzle.runPuzzle(input),
                    avg,
                    stddev
                ))
            } else {
                println(String.format(Locale.ROOT, "%-22s: %-10s",
                    puzzle.getName(),
                    puzzle.runPuzzle(input)
                ))
            }
        }
    }
}