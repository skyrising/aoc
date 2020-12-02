package de.skyrising.aoc2020

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets

fun getInput(day: Int): List<String> {
    val connection = URL("https://adventofcode.com/2020/day/$day/input").openConnection()
    connection.addRequestProperty("Cookie", System.getenv("AOC_COOKIE"))
    return BufferedReader(InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).useLines {
        it.toList()
    }
}

// Poor man's JMH

private var blackhole: Unit? = Unit
fun blackhole(o: Any?) {
    blackhole = if (o == null || o != blackhole) Unit else blackhole
}

fun <T> measure(runs: Int, fn: () -> T?): Double {
    val start = System.nanoTime()
    repeat(runs) {
        blackhole(fn())
    }
    return (System.nanoTime() - start) / (1000.0 * runs)
}