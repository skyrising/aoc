package de.skyrising.aoc2020

import de.skyrising.aoc.Puzzle
import java.nio.ByteBuffer
import java.nio.CharBuffer

inline fun <T> puzzleB(day: Int, name: String, crossinline run: (ByteBuffer) -> T): Puzzle<T> = de.skyrising.aoc.puzzleB(2020, day, name, run)
inline fun <T> puzzleLB(day: Int, name: String, crossinline run: (List<ByteBuffer>) -> T): Puzzle<T> = de.skyrising.aoc.puzzleLB(2020, day, name, run)
inline fun <T> puzzleS(day: Int, name: String, crossinline run: (CharBuffer) -> T): Puzzle<T> = de.skyrising.aoc.puzzleS(2020, day, name, run)
inline fun <T> puzzleLS(day: Int, name: String, crossinline run: (List<String>) -> T): Puzzle<T> = de.skyrising.aoc.puzzleLS(2020, day, name, run)

fun register2020() {
    registerDay1()
    registerDay2()
    registerDay3()
    registerDay4()
    registerDay5()
    registerDay6()
    registerDay7()
    registerDay8()
    registerDay9()
    registerDay10()
    registerDay11()
    registerDay12()
    registerDay13()
    registerDay14()
    registerDay15()
    registerDay16()
    registerDay17()
    registerDay18()
    registerDay19()
    registerDay20()
    registerDay21()
    registerDay22()
    registerDay23()
    registerDay24()
    registerDay25()
}