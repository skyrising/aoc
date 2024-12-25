@file:PuzzleName("Hot Springs")

package de.skyrising.aoc2023.day12

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import java.util.*

fun IntList.dec(index: Int) {
    set(index, getInt(index) - 1)
}

data class Springs(val op: BitSet, val dmg: BitSet, val length: Int) {
    constructor(value: String) : this(BitSet(value.length), BitSet(value.length), value.length) {
        for (i in value.indices) {
            when (value[i]) {
                '#' -> op.set(i)
                '.' -> dmg.set(i)
            }
        }
    }
    enum class Type {
        OPERATIONAL, DAMAGED, UNKNOWN
    }

    inline fun isOperational(index: Int) = op.get(index)
    inline fun isDamaged(index: Int) = dmg.get(index)

    inline operator fun get(index: Int) = when {
        isOperational(index) -> Type.OPERATIONAL
        isDamaged(index) -> Type.DAMAGED
        else -> Type.UNKNOWN
    }

    inline fun isEmpty() = length == 0

    fun subSequence(offset: Int) = Springs(op.get(offset, length), dmg.get(offset, length), length - offset)
    fun withFirst(type: Type): Springs {
        val op = op.clone() as BitSet
        val dmg = dmg.clone() as BitSet
        op.set(0, type == Type.OPERATIONAL)
        dmg.set(0, type == Type.DAMAGED)
        return Springs(op, dmg, length)
    }

    fun count(type: Type) = when (type) {
        Type.OPERATIONAL -> op.cardinality()
        Type.DAMAGED -> dmg.cardinality()
        Type.UNKNOWN -> length - op.cardinality() - dmg.cardinality()
    }
}

val test = TestInput("""
    ???.### 1,1,3
    .??..??...?##. 1,1,3
    ?#?#?#?#?#?#?#? 1,3,1,6
    ????.#...#... 4,1,1
    ????.######..#####. 1,6,5
    ?###???????? 3,2,1
""")

fun parse(input: PuzzleInput) = input.lines.map {
    val (springs, counts) = it.split(' ')
    springs to counts.ints()
}

fun adjustment(type: Springs.Type, a: Springs.Type, b: Springs.Type) = (type == a).toInt() - (type == b).toInt()

data class State(
    val springs: Springs,
    val counts: IntList,
    val operationalCount: Int = springs.count(Springs.Type.OPERATIONAL),
    val damagedCount: Int = springs.count(Springs.Type.DAMAGED)
) {
    private var countsHashCode: Int? = null
    private var countsHashCodeComputed = false

    fun withFirst(type: Springs.Type) = State(
        springs.withFirst(type),
        counts,
        operationalCount + adjustment(Springs.Type.OPERATIONAL, type, springs[0]),
        damagedCount + adjustment(Springs.Type.DAMAGED, type, springs[0])
    ).also { it.countsHashCode = countsHashCode; it.countsHashCodeComputed = true }
    fun next() = State(
        springs.subSequence(1),
        counts,
        operationalCount - springs.isOperational(0).toInt(),
        damagedCount - springs.isDamaged(0).toInt()
    ).also { it.countsHashCode = countsHashCode; it.countsHashCodeComputed = true }

    fun computePossibilities(cache: Object2LongMap<State> = Object2LongOpenHashMap()): Long {
        if (counts.isEmpty()) return (operationalCount == 0).toLong()
        if (springs.isEmpty()) return 0
        val cached = cache.getOrDefault(this as Any, -1L)
        if (cached >= 0) return cached
        if (springs.isOperational(0)) {
            val runLength = counts.getInt(0)
            if (springs.length - damagedCount < runLength) return 0
            var opCount = 1
            for (i in 1 until runLength) {
                if (springs.isDamaged(i)) return 0
                if (springs.isOperational(i)) opCount++
            }
            if (runLength == springs.length && damagedCount == 0) return (counts.size == 1).toLong()
            val next = springs[runLength]
            if (next == Springs.Type.OPERATIONAL) return 0
            return memoize(cache, State(springs.subSequence(runLength + 1), counts.subList(1, counts.size), operationalCount - opCount, damagedCount - (next == Springs.Type.DAMAGED).toInt()))
        }
        if (springs.isDamaged(0)) {
            return memoize(cache, next())
        }
        return memoize(cache,
            withFirst(Springs.Type.OPERATIONAL).computePossibilities(cache)
                    + next().computePossibilities(cache))
    }

    override fun hashCode(): Int {
        var hash = springs.hashCode()
        hash = hash * 31 + operationalCount
        hash = hash * 31 + damagedCount
        hash = hash * 31 + counts.size
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as State
        if (operationalCount != other.operationalCount) return false
        if (damagedCount != other.damagedCount) return false
        if (springs != other.springs) return false
        if (counts.size != other.counts.size) return false
        if (countsHashCodeComputed && other.countsHashCodeComputed && countsHashCode != other.countsHashCode) return false
        for (i in 0 until counts.size) {
            if (counts.getInt(i) != other.counts.getInt(i)) return false
        }
        return true
    }

    private inline fun memoize(cache: Object2LongMap<State>, value: Long) = value.also { cache[this] = it }
    private inline fun memoize(cache: Object2LongMap<State>, state: State) = state.computePossibilities(cache).also { cache[this] = it }
}

fun PuzzleInput.part1() = parse(this).sumOf { (springs, counts) ->
    State(Springs(springs), counts).computePossibilities()
}

fun PuzzleInput.part2() = parse(this).sumOf { (springs, counts) ->
    val repeatedCounts = IntArrayList(counts.size * 5)
    repeat(5) { repeatedCounts.addAll(counts) }
    State(Springs(("?$springs").repeat(5).substring(1)), repeatedCounts).computePossibilities()
}
