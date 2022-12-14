package de.skyrising.aoc2015

import de.skyrising.aoc.Puzzle
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.puzzle

inline fun <T> puzzle(day: Int, name: String, crossinline run: PuzzleInput.() -> T): Puzzle<T> = puzzle(2015, day, name, run)

fun register2015() {
    registerDay1()
    registerDay2()
    registerDay3()
    registerDay4()
    registerDay5()
    registerDay6()
    registerDay7()
    registerDay8()
    registerDay9()
}