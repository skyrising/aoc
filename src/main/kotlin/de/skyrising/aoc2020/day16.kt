package de.skyrising.aoc2020

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.HashSet

class BenchmarkDay16 : BenchmarkDay(16)

fun parseInput(input: List<String>, v2: Boolean = false): Triple<Map<String, Set<IntRange>>, IntList, MutableList<IntList>> {
    val classes = mutableMapOf<String, Set<IntRange>>()
    var state = 0
    val yourTicket = IntArrayList()
    val nearby = mutableListOf<IntList>()
    for (line in input) {
        if (line.isEmpty()) {
            state++
            continue
        }
        when (state) {
            0 -> {
                val colon = line.indexOf(": ")
                val className = line.substring(0, colon)
                val rangesStrings = line.substring(colon + 2).split(" or ")
                val ranges = if (v2) ObjectArraySet<IntRange>(2) else mutableSetOf()
                for (r in rangesStrings) {
                    val (f, t) = r.split('-')
                    ranges.add(f.toInt() .. t.toInt())
                }
                classes[className] = ranges
            }
            1 -> {
                if (line.startsWith("your ticket")) continue
                for (i in line.split(',')) yourTicket.add(i.toInt())
            }
            2 -> {
                if (line.startsWith("nearby tickets")) continue
                val thisTicket = IntArrayList()
                for (i in line.split(',')) thisTicket.add(i.toInt())
                nearby.add(thisTicket)
            }
        }
    }
    return Triple(classes, yourTicket, nearby)
}

fun parseInputV2(input: List<ByteBuffer>): Triple<Map<String, Set<IntRange>>, IntList, List<IntList>> {
    val classes = mutableMapOf<String, Set<IntRange>>()
    var state = 0
    val yourTicket = IntArrayList()
    val nearby = mutableListOf<IntList>()
    var skip = 0
    for (line1 in input) {
        if (skip > 0) {
            skip--
            continue
        }
        val line = Charsets.US_ASCII.decode(line1.slice())
        if (!line.hasRemaining()) {
            state++
            skip = 1
            continue
        }
        when (state) {
            0 -> {
                line.until(':')
                val className = line.toString()
                line.unflip().inc(2).until('-')
                val from1 = line.toString().toInt()
                line.unflip().inc().until(' ')
                val to1 = line.toString().toInt()
                line.unflip().inc(4).until('-')
                val from2 = line.toString().toInt()
                val to2 = line.unflip().inc().toString().toInt()
                classes[className] = setOf(from1 .. to1, from2 .. to2)
            }
            1 -> {
                splitToRanges(line, ',') { from, to ->
                    //println("$this, $from - $to, ${slice(from, to)}")
                    yourTicket.add(subSequence(from, to).toString().toInt())
                }
            }
            2 -> {
                val thisTicket = IntArrayList()
                splitToRanges(line, ',') { from, to ->
                    thisTicket.add(subSequence(from, to).toString().toInt())
                }
                nearby.add(thisTicket)
            }
        }
    }
    return Triple(classes, yourTicket, nearby)
}

fun mergeRanges(ranges: Collection<IntRange>): Set<IntRange> {
    val arr: Array<IntRange?> = ranges.toTypedArray()
    for (i in arr.indices) {
        val r1 = arr[i] ?: continue
        for (j in arr.indices) {
            if (i == j) continue
            val r2 = arr[j] ?: continue
            if (r2.first in r1) {
                arr[i] = r1.first .. maxOf(r1.last, r2.last)
                arr[j] = null
            } else if (r1.first in r2) {
                arr[i] = r2.first .. maxOf(r1.last, r2.last)
                arr[j] = null
            }
        }
    }
    val set = mutableSetOf<IntRange>()
    for (r in arr) if (r != null) set.add(r)
    return set
}

fun registerDay16() {
    val test = """
        class: 1-3 or 5-7
        row: 6-11 or 33-44
        seat: 13-40 or 45-50

        your ticket:
        7,1,14

        nearby tickets:
        7,3,47
        40,4,50
        55,2,20
        38,6,12
    """.trimIndent().split("\n")
    val test2 = """
        class: 0-1 or 4-19
        row: 0-5 or 8-19
        seat: 0-13 or 16-19

        your ticket:
        11,12,13

        nearby tickets:
        3,9,18
        15,1,5
        5,14,9
    """.trimIndent().split("\n")
    puzzleLS(16, "Ticket Translation v1") {
        val (classes, _, nearby) = parseInput(it)
        var sum = 0
        for (ticket in nearby) {
            for (i in ticket) {
                var validClass = false
                outer@ for (c in classes.values) {
                    for (r in c) {
                        if (i in r) {
                            validClass = true
                            break@outer
                        }
                    }
                }
                if (!validClass) sum += i
            }
        }
        sum
    }
    puzzleLS(16, "Ticket Translation v2") {
        val (classes, _, nearby) = parseInput(it, true)
        val ranges = mutableSetOf<IntRange>()
        for ((_, r) in classes) ranges.addAll(r)
        val merged = mergeRanges(ranges)
        var sum = 0
        for (ticket in nearby) {
            val iter = ticket.iterator()
            while (iter.hasNext()) {
                val i = iter.nextInt()
                var validClass = false
                for (r in merged) {
                    if (i in r) {
                        validClass = true
                        break
                    }
                }
                if (!validClass) sum += i
            }
        }
        sum
    }
    puzzleLS(16, "Part 2 v1") {
        val (classes, yourTicket, nearby) = parseInput(it)
        val candidates = Array<MutableSet<String>>(yourTicket.size) { HashSet(classes.keys) }
        ticketLoop@ for (ticket in nearby) {
            for (i in ticket) {
                var validClass = false
                outer@ for (c in classes.values) {
                    for (r in c) {
                        if (i in r) {
                            validClass = true
                            break@outer
                        }
                    }
                }
                if (!validClass) continue@ticketLoop
            }
            ticket.forEachIndexed { index, i ->
                candidates[index].removeIf { className ->
                    val c = classes[className]!!
                    var validClass = false
                    for (r in c) {
                        if (i in r) {
                            validClass = true
                            break
                        }
                    }
                    !validClass
                }
            }
        }

        val certain = HashMap<String, Int>()
        while (certain.size < yourTicket.size) {
            var changed = false
            candidates.forEachIndexed { index, c ->
                if (c.size > 1) {
                    if (c.removeAll(certain.keys)) changed = true
                }
                if (c.size == 1) {
                    val name = c.elementAt(0)
                    certain[name] = index
                }
            }
            if (!changed) break
        }
        if (certain.size < yourTicket.size) throw IllegalStateException("Could not find a solution")
        var product = 1L
        for ((className, i) in certain.entries) {
            if (!className.startsWith("departure")) continue
            product *= yourTicket.getInt(i)
        }
        product
    }
    puzzleLS(16, "Part 2 v2") {
        val (classes, yourTicket, nearby) = parseInput(it, true)
        val ranges = mutableSetOf<IntRange>()
        for ((_, r) in classes) ranges.addAll(r)
        val merged = mergeRanges(ranges)
        nearby.removeIf { ticket ->
            val iter = ticket.iterator()
            while (iter.hasNext()) {
                val i = iter.nextInt()
                var validClass = false
                for (r in merged) {
                    if (i in r) {
                        validClass = true
                        break
                    }
                }
                if (!validClass) return@removeIf true
            }
            false
        }
        val candidates = Array<MutableCollection<Map.Entry<String, Set<IntRange>>>>(yourTicket.size) { LinkedList(classes.entries) }
        for (index in candidates.indices) {
            candidates[index].removeIf { e ->
                var allValid = true
                val c = e.value
                for (ticket in nearby) {
                    var ticketValid = false
                    for (r in c) {
                        if (ticket.getInt(index) in r) ticketValid = true
                    }
                    if (!ticketValid) {
                        allValid = false
                        break
                    }
                }
                !allValid
            }
        }
        /*
        ticketLoop@ for (ticket in nearby) {
            val iter = ticket.iterator()
            while (iter.hasNext()) {
                val i = iter.nextInt()
                var validClass = false
                for (r in merged) {
                    if (i in r) {
                        validClass = true
                        break
                    }
                }
                if (!validClass) continue@ticketLoop
            }
            ticket.forEachIndexed { index, i ->
                val classNames = candidates[index]
                classNames.removeIf { e ->
                    val c = e.value
                    var validClass = false
                    for (r in c) {
                        if (i in r) {
                            validClass = true
                            break
                        }
                    }
                    !validClass
                }
            }
        }*/

        val certain = HashMap<Map.Entry<String, Set<IntRange>>, Int>()
        while (certain.size < yourTicket.size) {
            var changed = false
            candidates.forEachIndexed { index, c ->
                if (c.size > 1) {
                    if (c.removeAll(certain.keys)) changed = true
                }
                if (c.size == 1) {
                    certain[c.elementAt(0)] = index
                }
            }
            if (!changed) break
        }
        if (certain.size < yourTicket.size) {
            println(certain)
            println(candidates.contentToString())
            println(nearby.size)
            throw IllegalStateException("Could not find a solution")
        }
        var product = 1L
        for ((e, i) in certain.entries) {
            if (!e.key.startsWith("departure")) continue
            product *= yourTicket.getInt(i)
        }
        product
    }
}