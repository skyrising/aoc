package de.skyrising.aoc2015

import de.skyrising.aoc.Puzzle
import java.nio.ByteBuffer
import java.nio.CharBuffer

inline fun <T> puzzleB(day: Int, name: String, crossinline run: (ByteBuffer) -> T): Puzzle<T> = de.skyrising.aoc.puzzleB(2015, day, name, run)
inline fun <T> puzzleLB(day: Int, name: String, crossinline run: (List<ByteBuffer>) -> T): Puzzle<T> = de.skyrising.aoc.puzzleLB(2015, day, name, run)
inline fun <T> puzzleS(day: Int, name: String, crossinline run: (CharBuffer) -> T): Puzzle<T> = de.skyrising.aoc.puzzleS(2015, day, name, run)
inline fun <T> puzzleLS(day: Int, name: String, crossinline run: (List<String>) -> T): Puzzle<T> = de.skyrising.aoc.puzzleLS(2015, day, name, run)

fun register2015() {
    registerDay1()
    registerDay2()
    registerDay3()
    registerDay4()
    registerDay5()
    registerDay6()
    registerDay7()
}