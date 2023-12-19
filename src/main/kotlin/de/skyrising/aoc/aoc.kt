package de.skyrising.aoc

import java.util.*
import kotlin.math.sqrt

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
        try {
            if (BENCHMARK) {
                input.benchmark = true
                var result: Any? = null
                val allTimes = DoubleArray(WARMUP + MEASURE_ITERS) { a ->
                    measure(RUNS) { b ->
                        if (a == WARMUP + MEASURE_ITERS - 1 && b == RUNS - 1) input.benchmark = false
                        puzzle.runPuzzle(input).also { result = it }
                    }
                }
                val times = allTimes.copyOfRange(WARMUP, allTimes.size)
                val avg = times.average()
                val stddev = sqrt(times.map { (it - avg) * (it - avg) }.average())
                println(
                    String.format(
                        Locale.ROOT, "%d/%02d/%d.%d %-32s: %16s, %s ± %4.1f%%",
                        puzzle.day.year,
                        puzzle.day.day,
                        puzzle.part,
                        puzzle.index,
                        puzzle.name,
                        result,
                        formatTime(avg),
                        stddev * 100 / avg
                    )
                )
            } else {
                val start = System.nanoTime()
                val result = puzzle.runPuzzle(input)
                val time = (System.nanoTime() - start) / 1000.0
                println(
                    String.format(
                        Locale.ROOT, "%d/%02d/%d.%d %-32s: %16s, %s ± ?",
                        puzzle.day.year,
                        puzzle.day.day,
                        puzzle.part,
                        puzzle.index,
                        puzzle.name,
                        result,
                        formatTime(time)
                    )
                )
            }
        } catch (e: Exception) {
            println(
                String.format(
                    Locale.ROOT, "%d/%02d/%d.%d %-32s: %16s, %s",
                    puzzle.day.year,
                    puzzle.day.day,
                    puzzle.part,
                    puzzle.index,
                    puzzle.name,
                    e.javaClass.simpleName,
                    e.message
                )
            )
            e.printStackTrace()
        }
    }
    println("Done")
}

private fun formatTime(us: Double): String {
    if (us < 1000) return "%7.3fµs".format(us)
    val ms = us / 1000
    if (ms < 1000) return "%7.3fms".format(ms)
    return "%7.3fs ".format(ms / 1000)
}
