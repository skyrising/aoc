package de.skyrising.aoc2020

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern
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
                println("Decoding ${System.identityHashCode(input)}")
            }
            return run(lastInput!!.second)
        }
    }
    register(p)
    return p
}

fun registerAll() {
    puzzleS(1, "Report Repair v1") {
        val numbers = IntOpenHashSet()
        for (line in it) {
            val num = line.toInt()
            val other = 2020 - num
            if (numbers.contains(other)) return@puzzleS num * other
            numbers.add(num)
        }
        0
    }
    fun parseInt4(s: String) = when (s.length) {
        1 -> s[0] - '0'
        2 -> (s[1] - '0') + 10 * (s[0] - '0')
        3 -> (s[2] - '0') + 10 * ((s[1] - '0') + 10 * (s[0] - '0'))
        4 -> (s[3] - '0') + 10 * ((s[2] - '0') + 10 * ((s[1] - '0') + 10 * (s[0] - '0')))
        else -> throw IllegalArgumentException()
    }
    fun isBitSet(longs: LongArray, i: Int): Boolean {
        return (longs[i shr 6] shr (i and 0x3f)) and 1 != 0L
    }
    fun setBit(longs: LongArray, i: Int) {
        val idx = i shr 6
        longs[idx] = longs[idx] or (1L shl (i and 0x3f))
    }
    puzzleS(1, "Report Repair v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in it) {
            val num = parseInt4(line)
            val other = 2020 - num
            if (isBitSet(numbers, other)) return@puzzleS num * other
            setBit(numbers, num)
        }
        0
    }
    puzzleS(1, "Part Two v1") {
        val numbers = IntOpenHashSet()
        for (line in it) {
            val a = line.toInt()
            for (b in numbers.iterator()) {
                val c = 2020 - a - b
                if (numbers.contains(c)) return@puzzleS a * b * c
            }
            numbers.add(a)
        }
        0
    }
    puzzleS(1, "Part Two v2") {
        val numbers = LongArray(2048 shr 6)
        for (line in it) {
            val a = parseInt4(line)
            for (b in 0 until 1010) {
                if (!isBitSet(numbers, b)) continue
                val c = 2020 - a - b
                if (c < 0) break
                if (isBitSet(numbers, c)) return@puzzleS a * b * c
            }
            setBit(numbers, a)
        }
        0
    }
    puzzleS(2, "Password Philosophy v1") {
        val pattern = Pattern.compile("^(?<min>\\d+)-(?<max>\\d+) (?<char>.): (?<password>.*)$")
        var valid = 0
        outer@ for (line in it) {
            val match = pattern.matcher(line)
            if (!match.find()) {
                println("Could not parse $line")
                continue
            }
            val min = match.group("min").toInt()
            val max = match.group("max").toInt()
            val c = match.group("char")[0]
            val password = match.group("password").toCharArray()
            var count = 0
            for (pc in password) {
                if (pc == c) {
                    count++
                    if (count > max) continue@outer
                }
            }
            if (count >= min) valid++
        }
        valid
    }
    puzzleB(2, "Password Philosophy v2") {
        var valid = 0
        for (line in it) {
            valid += day2(line) { min, max, c, start, end ->
                var count = 0
                for (i in start until end) {
                    if (line[i] == c) {
                        count++
                        if (count > max) return@day2 false
                    }
                }
                count >= min
            }
        }
        valid
    }
    puzzleS(2, "Part Two v1") {
        val pattern = Pattern.compile("^(?<first>\\d+)-(?<second>\\d+) (?<char>.): (?<password>.*)$")
        var valid = 0
        for (line in it) {
            val match = pattern.matcher(line)
            if (!match.find()) {
                println("Could not parse $line")
                continue
            }
            val first = match.group("first").toInt()
            val second = match.group("second").toInt()
            val c = match.group("char")[0]
            val password = match.group("password").toCharArray()
            if ((password[first - 1] == c) xor (password[second - 1] == c)) {
                valid++
            }
        }
        valid
    }
    puzzleB(2, "Part Two v2") {
        var valid = 0
        for (line in it) {
            valid += day2(line) { first, second, c, start, _ ->
                (line[start + first - 1] == c) xor (line[start + second - 1] == c)
            }
        }
        valid
    }
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

const val RUNS = 1000
const val WARMUP = 14
const val MEASURE_ITERS = 10
const val BENCHMARK = true

fun main() {
    registerAll()
    for ((day, puzzles) in dailyPuzzles) {
        // if (day != 2) continue
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