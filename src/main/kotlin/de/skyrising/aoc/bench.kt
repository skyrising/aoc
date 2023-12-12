package de.skyrising.aoc

import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
abstract class BenchmarkBase(year: Int, day: Int) {
    init {
        registerFiltered(PuzzleFilter(sortedSetOf(PuzzleDay(year, day))))
    }
    private val input = getInput(year, day)
    private val p1v1 = allPuzzles[year, day].find { it.part == 1 && it.index == 0 }!!
    private val p1v2 = allPuzzles[year, day].find { it.part == 1 && it.index == 1 }!!
    private val p2v1 = allPuzzles[year, day].find { it.part == 2 && it.index == 0 }!!
    private val p2v2 = allPuzzles[year, day].find { it.part == 2 && it.index == 1 }!!

    @Benchmark
    fun part1v1() = p1v1.runPuzzle(input)

    @Benchmark
    fun part1v2() = p1v2.runPuzzle(input)

    @Benchmark
    fun part2v1() = p2v1.runPuzzle(input)

    @Benchmark
    fun part2v2() = p2v2.runPuzzle(input)
}

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
abstract class BenchmarkBaseV1(year: Int, day: Int) {
    init {
        registerFiltered(PuzzleFilter(sortedSetOf(PuzzleDay(year, day))))
    }
    private val input = getInput(year, day)
    private val p1v1 = allPuzzles[year, day].find { it.part == 1 && it.index == 0 }!!
    private val p2v1 = allPuzzles[year, day].find { it.part == 2 && it.index == 0 }!!

    @Benchmark
    fun part1v1() = p1v1.runPuzzle(input)

    @Benchmark
    fun part2v1() = p2v1.runPuzzle(input)
}