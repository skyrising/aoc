package de.skyrising.aoc2020

import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
abstract class BenchmarkDay(day: Int) {
    init {
        registerAll()
    }
    private val input = getInput(day)
    private val p1v1 = dailyPuzzles[day]!![0]
    private val p1v2 = dailyPuzzles[day]!![1]
    private val p2v1 = dailyPuzzles[day]!![2]
    private val p2v2 = dailyPuzzles[day]!![3]

    @Benchmark
    fun part1v1() = p1v1.runPuzzle(input)

    @Benchmark
    fun part1v2() = p1v2.runPuzzle(input)

    @Benchmark
    fun part2v1() = p2v1.runPuzzle(input)

    @Benchmark
    fun part2v2() = p2v2.runPuzzle(input)
}

class BenchmarkDay1 : BenchmarkDay(1)
class BenchmarkDay2 : BenchmarkDay(2)
class BenchmarkDay3 : BenchmarkDay(3)