package de.skyrising.aoc

import de.skyrising.aoc.visualization.Visualization
import java.util.*
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

const val RUNS = 8
const val WARMUP = 2
const val BENCHMARK = false
var BENCHMARK_MODE: BenchMode? = if (BENCHMARK) BenchMode.Duration(100.milliseconds) else null
const val QUICK_PART2 = true

fun buildFilter(args: Array<String>): PuzzleFilter {
    if (args.isEmpty()) return PuzzleFilter.all().copy(latestOnly = true)
    if (args[0] == "all") return PuzzleFilter.all()
    if (args[0] == "best") return PuzzleFilter.all().copy(bestVersionOnly = true)
    val year = args[0].toInt()
    val filter = PuzzleFilter.year(year)
    if (args.size <= 1) return filter.copy(latestOnly = true)
    if (args[1] == "all") return filter
    if (args[1] == "best") return filter.copy(bestVersionOnly = true)
    return PuzzleFilter(sortedSetOf(PuzzleDay(year, args[1].toInt())))
}

fun main(args: Array<String>) {
    val filter = buildFilter(args)
    registerFiltered(filter)
    val puzzlesToRun = allPuzzles.filter(filter)
    var totalTime = 0.0
    var totalVariance = 0.0
    for (puzzle in puzzlesToRun) {
        val input = puzzle.getRealInput()
        if (puzzle.resultType == Visualization::class.java) {
            input.use(puzzle::runPuzzle)
            continue
        }
        try {
            val benchmark = BENCHMARK_MODE
            if (benchmark != null) {
                input.benchmark = true
                var warmup = WARMUP
                var measure = RUNS
                val mode = when (benchmark) {
                    is BenchMode.Iterations -> {
                        if (QUICK_PART2 && puzzle.part == 2) {
                            warmup = 1
                            measure = 3
                        }
                        BenchMode.Iterations(if (QUICK_PART2 && puzzle.part == 2) 3 else benchmark.iterations)
                    }
                    is BenchMode.Duration -> benchmark
                }
                val (result, avg, stddev) = benchmark(warmup, measure, mode) { a, b ->
                    input.use { puzzle.runPuzzle(input) }.also {
                        input.benchmark = true
                        if (b == 0) {
                            print(if (a < 0) '-' else '+')
                        }
                    }
                }
                print("\r\u001b[K")
                totalTime += avg
                totalVariance += stddev * stddev
                println(formatResult(puzzle, result, avg, stddev))
            } else {
                val start = System.nanoTime()
                val result = input.use { puzzle.runPuzzle(input) }
                val time = (System.nanoTime() - start) / 1000.0
                totalTime += time
                println(formatResult(puzzle, result, time))
            }
        } catch (e: Exception) {
            println(formatResult(puzzle, e))
            e.printStackTrace()
        }
    }
    println(buildString {
        append("Done: ")
        append(formatTime(totalTime).trim())
        if (BENCHMARK) {
            append(String.format(Locale.ROOT, " ± %.1f%%", sqrt(totalVariance) * 100 / totalTime))
        }
    })
}

private fun formatTime(us: Double): String {
    if (us < 1000) return "%7.3fµs".format(us)
    val ms = us / 1000
    if (ms < 1000) return "%7.3fms".format(ms)
    return "%7.3fs ".format(ms / 1000)
}

const val NAME_LENGTH = 32
const val RESULT_LENGTH = 19
fun formatResult(puzzle: Puzzle<*>, result: Any?, us: Double = 0.0, stddev: Double? = null) = buildString {
    append(puzzle.day.year)
    append('/')
    append("%02d".format(puzzle.day.day))
    append('/')
    append(puzzle.part)
    append('.')
    append(puzzle.index)
    append(' ')
    append(puzzle.name)
    repeat(NAME_LENGTH - puzzle.name.length) { append(' ') }
    append(": ")
    val resultString = if (result is Throwable) result.javaClass.simpleName else result.toString()
    if (resultString.length <= RESULT_LENGTH) {
        repeat(RESULT_LENGTH - resultString.length) { append(' ') }
        append(resultString)
    } else {
        repeat(RESULT_LENGTH) { append(' ') }
    }
    append(", ")
    if (result is Throwable) {
        append(result.message)
    } else {
        append(formatTime(us))
        append(" ± ")
        if (stddev != null) {
            append(String.format(Locale.ROOT, "%4.1f%%", stddev * 100 / us))
        } else {
            append("?")
        }
    }
    if (resultString.length > RESULT_LENGTH) {
        append("\n")
        append(resultString)
    }
}
