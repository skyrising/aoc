@file:PuzzleName("Crab Cups")

package de.skyrising.aoc2020.day23

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import kotlin.collections.contains
import kotlin.collections.last
import kotlin.collections.set

/*
private fun move(cups: IntArray, count: Int): IntArray {
    val size = cups.size
    //println("-- move ${count + 1} --")
    //println("cups: ${cups.contentToString()}")
    val currentIndex = count % size
    val pickup = Array(3) { n -> cups[(currentIndex + n + 1) % size] }
    //println("pick up: ${pickup.contentToString()}")
    var dest = cups[currentIndex] - 1
    while (dest in pickup || dest == 0) {
        dest--
        if (dest <= 0) dest = size
    }
    //println("destination: $dest")
    val newCups = IntArray(size)
    var i = 0
    var j = 0
    var currentDest = 0
    while (cups[i] in pickup) i++
    while (j < size) {
        if (i == currentIndex) currentDest = j
        val cup = cups[i]
        newCups[j++] = cup
        if (cup == dest) {
            newCups[j++] = pickup[0]
            newCups[j++] = pickup[1]
            newCups[j++] = pickup[2]
        }
        i = (i + if (i == currentIndex) 4 else 1) % size
    }
    val rotateAmount = currentDest - currentIndex
    if (rotateAmount != 0) {
        return IntArray(size) {
            newCups[(size + it + rotateAmount) % size]
        }
    }
    return newCups
}
*/

private fun move(cups: Int2IntMap, current: Int) {
    var tmpLabel = current
    val pickup = IntArray(3) {
        tmpLabel = cups[tmpLabel]
        tmpLabel
    }
    cups[current] = cups[tmpLabel]
    var dest = current - 1
    while (dest in pickup || dest < 1) {
        dest--
        if (dest < 1) dest = cups.size
    }
    //println("pickup: ${pickup.contentToString()}")
    //println("current: $current")
    //println("dest: $dest")
    tmpLabel = cups[dest]
    cups[dest] = pickup[0]
    cups[pickup[2]] = tmpLabel
}


val test = "389125467\n"

fun PuzzleInput.part1(): Any {
    val base = IntArray(chars.length - 1) { i -> chars[i] - '0' }
    val cups = Int2IntOpenHashMap(base.size)
    for (i in 0 until base.size - 1) cups[base[i]] = base[i + 1]
    cups[base.last()] = base[0]
    //println(cups)
    var current = base[0]
    repeat(100) {
        move(cups, current)
        //println(cups)
        current = cups[current]
    }
    var i = cups[1]
    val sb = StringBuilder(cups.size - 1)
    while (i != 1) {
        sb.append(i)
        i = cups[i]
    }
    return sb.toString()
}

fun PuzzleInput.part2(): Any {
    val base = IntArray(chars.length - 1) { i -> chars[i] - '0' }
    //println(base.contentToString())
    val cups = Int2IntOpenHashMap(1_000_000)
    for (i in 0 until base.size - 1) cups[base[i]] = base[i + 1]
    cups[base.last()] = base.size + 1
    for (i in base.size + 1 until 1_000_000) cups[i] = i + 1
    cups[1_000_000] = base[0]
    var current = base[0]
    //for ((k, v) in cups) if (v != k + 1 || k in base) println("$k, $v")
    repeat(10_000_000) {
        //if (it % 100_000 == 0) println("${it / 100_000}%")
        move(cups, current)
        current = cups[current]
    }
    val a = cups[1]
    val b = cups[a]
    //println("$a,$b")
    return a.toLong() * b.toLong()
}

