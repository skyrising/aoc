package de.skyrising.aoc

import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
abstract class BenchmarkBase(year: Int, day: Int) {
    protected val input = getInput(year, day).also { it.benchmark = true }
}

// Poor man's JMH

data class BenchmarkResult(val result: Any?, val meanUs: Double, val stddevUs: Double)

private inline fun benchmarkCommon(warmup: Int, measure: Int, run: (Int) -> Double): BenchmarkResult {
    val allTimes = DoubleArray(warmup + measure) { run(it - warmup) }
    val times = allTimes.copyOfRange(warmup, allTimes.size)
    val avg = times.average()
    val stddev = sqrt(times.map { (it - avg) * (it - avg) }.average())
    return BenchmarkResult(null, avg, stddev)
}

fun benchmark(warmup: Int, measure: Int, iterations: Int, block: (Int, Int) -> Any?): BenchmarkResult {
    var result: Any? = null
    return benchmarkCommon(warmup, measure) { a ->
        measure(iterations) { b ->
            block(a, b).also { result = it }
        }
    }.copy(result = result)
}

fun benchmark(warmup: Int, measure: Int, duration: Duration, block: (Int, Int) -> Any?): BenchmarkResult {
    var result: Any? = null
    return benchmarkCommon(warmup, measure) { a ->
        var b = 0
        measureForDuration(duration) {
            block(a, b++).also { result = it }
        }
    }.copy(result = result)
}

sealed interface BenchMode {
    data class Iterations(val iterations: Int) : BenchMode
    data class Duration(val duration: kotlin.time.Duration) : BenchMode
}

fun benchmark(warmup: Int, measure: Int, mode: BenchMode, block: (Int, Int) -> Any?) = when (mode) {
    is BenchMode.Iterations -> benchmark(warmup, measure, mode.iterations, block)
    is BenchMode.Duration -> benchmark(warmup, measure, mode.duration, block)
}

class Timeout(val duration: Duration) {
    @Volatile var isDone = false

    fun start() {
        val deadline = System.nanoTime() + duration.inWholeNanoseconds
        Thread.startVirtualThread {
            while (System.nanoTime() < deadline) {
                LockSupport.parkNanos(deadline - System.nanoTime())
            }
            isDone = true
        }
    }
}

inline fun <T> measure(runs: Int, fn: (Int) -> T?): Double {
    val start = System.nanoTime()
    repeat(runs) {
        blackhole(fn(it))
    }
    return (System.nanoTime() - start) / (1000.0 * runs)
}

inline fun <T> runForDuration(duration: Duration, block: () -> T?): Pair<Long, Duration> {
    val timeout = Timeout(duration)
    timeout.start()
    val start = System.nanoTime()
    var iterations = 0L
    do {
        iterations++
        blackhole(block())
    } while (!timeout.isDone)
    return iterations to (System.nanoTime() - start).nanoseconds
}

inline fun <T> measureForDuration(duration: Duration, block: () -> T?): Double {
    val (iterations, time) = runForDuration(duration) { block() }
    return time.inWholeNanoseconds / (1000.0 * iterations)
}

private var blackhole: Unit? = Unit
fun blackhole(o: Any?) {
    blackhole = if (o == null || o != blackhole) Unit else blackhole
}
