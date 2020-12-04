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
    fun generateInput(rand: Random): Pair<List<ByteBuffer>, T>?
    fun runPuzzle(input: List<ByteBuffer>): T
}

val dailyPuzzles = TreeMap<Int, MutableList<Puzzle<*>>>()

fun register(puzzle: Puzzle<*>) {
    dailyPuzzles.computeIfAbsent(puzzle.getDay()) { mutableListOf() }.add(puzzle)
}

@Suppress("LeakingThis")
abstract class AbstractPuzzle<T>(private val day: Int, private val name: String) : Puzzle<T> {
    override fun getName() = name
    override fun getDay() = day
    override fun generateInput(rand: Random): Pair<List<ByteBuffer>, T>? = null
}

inline fun <T> puzzleB(day: Int, name: String, crossinline run: (List<ByteBuffer>) -> T): Puzzle<T> {
    val p = object : AbstractPuzzle<T>(day, name) {
        override fun runPuzzle(input: List<ByteBuffer>) = run(input)
    }
    register(p)
    return p
}

var lastInput: Pair<List<ByteBuffer>, List<String>>? = null
inline fun <T> puzzleS(day: Int, name: String, crossinline run: (List<String>) -> T): Puzzle<T> {
    val p = object : AbstractPuzzle<T>(day, name) {
        override fun runPuzzle(input: List<ByteBuffer>): T {
            if (lastInput == null || lastInput!!.first !== input) {
                lastInput = Pair(input, input.map { StandardCharsets.US_ASCII.decode(it.slice()).toString() })
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
}


inline fun day2(line: ByteBuffer, predicate: (n1: Int, n2: Int, c: Byte, start: Int, end: Int) -> Boolean): Int {
    val len = line.remaining()
    var num1 = 0
    var num2 = 0
    var i = 0
    while (i < len) {
        val c = line[i++]
        if (c == '-'.toByte()) break
        num1 *= 10
        num1 += c - '0'.toByte()
    }
    while (i < len) {
        val c = line[i++]
        if (c == ' '.toByte()) break
        num2 *= 10
        num2 += c - '0'.toByte()
    }
    val c = line[i]
    return if (predicate.invoke(num1, num2, c, i + 3, len)) 1 else 0
}

inline fun wrap(x: Int, len: Int) = x - if (x >= len) len else 0

const val RUNS = 1000
const val WARMUP = 14
const val MEASURE_ITERS = 10
const val BENCHMARK = true

fun main() {
    registerAll()
    for ((day, puzzles) in dailyPuzzles) {
        if (day != 4) continue
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