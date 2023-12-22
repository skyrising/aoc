package de.skyrising.aoc

import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
abstract class BenchmarkBase(year: Int, day: Int) {
    protected val input = getInput(year, day).also { it.benchmark = true }
}