package de.skyrising.aoc2019

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay2 : BenchmarkDayV1(2)

@Suppress("unused")
fun registerDay2() {
    val test = TestInput("""
        1,9,10,3,2,3,11,0,99,30,40,50
    """)
    fun run(input: PuzzleInput, noun: Int, verb: Int) = runIntcode(input) {
        it[1] = noun
        it[2] = verb
    }.getInt(0)
    part1("Program Alarm") {
        run(this, 12, 2)
    }
    part2 {
        for (noun in 0..99) {
            for (verb in 0..99) {
                if (run(this, noun, verb) == 19690720) {
                    return@part2 100 * noun + verb
                }
            }
        }
    }
}