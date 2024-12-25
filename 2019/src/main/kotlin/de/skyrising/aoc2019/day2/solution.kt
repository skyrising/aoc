@file:PuzzleName("Program Alarm")

package de.skyrising.aoc2019.day2

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc2019.runIntcode

val test = TestInput("""
    1,9,10,3,2,3,11,0,99,30,40,50
""")

fun run(input: PuzzleInput, noun: Int, verb: Int) = runIntcode(input) {
    it[1] = noun
    it[2] = verb
}.getInt(0)

fun PuzzleInput.part1() = run(this, 12, 2)

fun PuzzleInput.part2(): Any {
    for (noun in 0..99) {
        for (verb in 0..99) {
            if (run(this, noun, verb) == 19690720) {
                return 100 * noun + verb
            }
        }
    }
    return Unit
}
