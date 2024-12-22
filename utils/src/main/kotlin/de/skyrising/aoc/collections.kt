package de.skyrising.aoc

import it.unimi.dsi.fastutil.ints.IntPriorityQueue

class IntArrayDeque(capacity: Int) : IntPriorityQueue {
    private var data = IntArray(capacity)
    private var start = 0
    private var end = 0
    var length = capacity

    constructor() : this(16)

    private fun expand() {
        val newLength = length * 2
        val newData = IntArray(newLength)
        if (start < end) {
            System.arraycopy(data, start, newData, 0, end - start)
        } else {
            System.arraycopy(data, start, newData, 0, length - start)
            System.arraycopy(data, 0, newData, length - start, end)
        }
        data = newData
        start = 0
        end = length
        length = newLength
    }

    override fun enqueue(x: Int) {
        data[end++] = x
        if (end == length) end = 0
        if (end == start) expand()
    }

    fun enqueueFirst(x: Int) {
        if (start == 0) start = length
        data[--start] = x
        if (end == start) expand()
    }

    override fun size(): Int {
        val l = end - start
        return if (l >= 0) l else l + length
    }

    override fun clear() {
        start = 0
        end = 0
    }

    override fun comparator() = null

    override fun dequeueInt(): Int {
        if (start == end) throw NoSuchElementException()
        return data[start++].also { if (start == length) start = 0 }
    }

    fun dequeueLastInt(): Int {
        if (start == end) throw NoSuchElementException()
        if (end == 0) end = length
        return data[--end]
    }

    override fun firstInt(): Int {
        if (start == end) throw NoSuchElementException()
        return data[start]
    }

    override fun lastInt(): Int {
        if (start == end) throw NoSuchElementException()
        return data[(if (end == 0) length else end) - 1]
    }

    operator fun get(index: Int): Int {
        if (index < 0 || index >= size()) throw IndexOutOfBoundsException()
        return data[(start + index) % length]
    }
}
