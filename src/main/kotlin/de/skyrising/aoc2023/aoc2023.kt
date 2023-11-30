package de.skyrising.aoc2023

import de.skyrising.aoc.Puzzle
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.puzzle

inline fun <T> puzzle(day: Int, name: String, crossinline run: PuzzleInput.() -> T): Puzzle<T> = puzzle(2023, day, name, run)

fun register2023() {
}