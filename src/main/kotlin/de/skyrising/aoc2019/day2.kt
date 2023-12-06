package de.skyrising.aoc2019

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.ints
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay2 : BenchmarkDayV1(2)

@Suppress("unused")
fun registerDay2() {
    val test = TestInput("""
        1,9,10,3,2,3,11,0,99,30,40,50
    """)
    part1("Program Alarm") {
        val code = string.ints()
        code[1] = 12
        code[2] = 2
        runIntcode(code).getInt(0)
    }
    part2 {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val code = string.ints()
                code[1] = noun
                code[2] = verb
                if (runIntcode(code).getInt(0) == 19690720) {
                    return@part2 100 * noun + verb
                }
            }
        }
    }
}