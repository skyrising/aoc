package de.skyrising.aoc

import de.skyrising.aoc.visualization.Visualization
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.forEachLine
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

const val RUNS = 8
const val WARMUP = 2
const val BENCHMARK = false
var BENCHMARK_MODE: BenchMode? = if (BENCHMARK) BenchMode.Duration(100.milliseconds) else null
const val QUICK_PART2 = true
const val PRINT_RESULT = true

val RESULTS = mutableMapOf<PuzzleDay, List<String>>()

fun buildFilter(args: MutableList<String>): PuzzleFilter {
    var filter = PuzzleFilter.all()
    when(args.firstOrNull()) {
        null -> return filter.copy(latestOnly = true)
        "all" -> return filter
        "best" -> return filter.copy(bestVersionOnly = true)
    }
    val year = args.removeFirst().toInt()
    filter = PuzzleFilter.year(year)
    when(args.firstOrNull()) {
        null -> return filter.copy(latestOnly = true)
        "all" -> return filter
        "best" -> return filter.copy(bestVersionOnly = true)
    }
    val day = args.removeFirst().toInt()
    return PuzzleFilter(sortedSetOf(PuzzleDay(year, day)), solutionTypes = EnumSet.allOf(SolutionType::class.java))
}

fun main(args: Array<String>) {
    val resultsFile = Path("results.txt")
    if (resultsFile.exists()) {
        resultsFile.forEachLine {
            val line = it.trim()
            if (line.isEmpty()) return@forEachLine
            val values = line.split(' ')
            val (year, day) = values.take(2)
            RESULTS[PuzzleDay(year.toInt(), day.toInt())] = values.slice(2..values.lastIndex)
        }
    }
    var filter = buildFilter(args.toMutableList())
    filter = if (!BENCHMARK) {
        filter.copy(solutionTypes = filter.solutionTypes - SolutionType.C2)
    } else {
        filter.copy(solutionTypes = filter.solutionTypes - SolutionType.VISUALIZATION)
    }
    registerFiltered(filter)
    val puzzlesToRun = allPuzzles.filter(filter)
    var totalTime = 0.0
    var totalVariance = 0.0
    fun runOne(input: PuzzleInput, puzzle: Puzzle<*, *>, prepare: Boolean = false) {
        try {
            val benchmark = BENCHMARK_MODE
            val result = if (benchmark != null) {
                input.benchmark = true
                var warmup = WARMUP
                var measure = RUNS
                val mode = when (benchmark) {
                    is BenchMode.Iterations -> {
                        if (QUICK_PART2 && !prepare && puzzle.part == 2) {
                            warmup = 1
                            measure = 3
                        }
                        BenchMode.Iterations(if (QUICK_PART2 && !prepare && puzzle.part == 2) 3 else benchmark.iterations)
                    }
                    is BenchMode.Duration -> benchmark
                }
                val (result, avg, stddev) = benchmark(warmup, measure, mode) { a, b ->
                    input.use { if (prepare) puzzle.prepareInput(input) else puzzle.runPuzzle(input) }.also {
                        if (b == 0) {
                            print(if (a < 0) '-' else '+')
                        }
                    }
                }
                print("\r\u001b[K")
                totalTime += avg
                totalVariance += stddev * stddev
                println(formatResult(puzzle, result, prepare, avg, stddev))
                result
            } else {
                val start = System.nanoTime()
                val result = input.use { if (prepare) puzzle.prepareInput(input) else puzzle.runPuzzle(input) }
                val time = (System.nanoTime() - start) / 1000.0
                totalTime += time
                println(formatResult(puzzle, result, prepare, time))
                result
            }
            if (prepare) {
                input.prepared = lazyOf(result)
            }
        } catch (e: Exception) {
            println(formatResult(puzzle, e))
            e.printStackTrace()
        }
    }
    for (puzzle in puzzlesToRun) {
        val input = puzzle.getRealInput()
        if (puzzle.resultType == Visualization::class.java) {
            input.use(puzzle::runPuzzle)
            continue
        }
        if (puzzle is DefaultPuzzle<*, *> && puzzle.prepare != null && input.prepared == null) {
            runOne(input, puzzle, true)
        }
        runOne(input, puzzle)
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

const val NAME_LENGTH = 38
const val RESULT_LENGTH = 20
const val CORRECT = "\u001B[32m✓\u001B[0m"
const val INCORRECT = "\u001B[31m❌\u001B[0m"
fun formatResult(puzzle: Puzzle<*, *>, result: Any?, prepare: Boolean = false, us: Double = 0.0, stddev: Double? = null) = buildString {
    append(puzzle.day.year)
    append('/')
    append("%02d".format(puzzle.day.day))
    append('/')
    append(if (prepare) 0 else puzzle.part)
    append('.')
    append(puzzle.index)
    append(' ')
    val name = if (prepare) "Preparing Input" else puzzle.name
    append(name)
    repeat(NAME_LENGTH - name.length) { append(' ') }
    append(": ")
    if (result is Throwable) {
        append(result.javaClass.simpleName)
        append(": ")
        append(result.message)
        append(' ')
        append(INCORRECT)
    } else {
        append(formatTime(us))
        append(" ± ")
        if (stddev != null) {
            append(String.format(Locale.ROOT, "%4.1f%%", stddev * 100 / us))
        } else {
            append("?")
        }
        if (PRINT_RESULT && !prepare) {
            append(' ')
            val resultString = result.toString()
            if (resultString.length <= RESULT_LENGTH) {
                repeat(RESULT_LENGTH - resultString.length) { append(' ') }
                append(resultString)
            } else {
                append(resultString)
            }
            val correctResult = (RESULTS[puzzle.day] ?: listOf()).let { if (it.size >= puzzle.part) it[puzzle.part - 1] else null }
            if (correctResult != null) {
                append(' ')
                append(if (resultString == correctResult) CORRECT else INCORRECT)
            }
        }
    }
}
