package de.skyrising.aoc2024.day9

import de.skyrising.aoc.*

val test = TestInput("""
2333133121414131402
""")

@PuzzleName("Disk Fragmenter")
fun PuzzleInput.part1(): Any {
    var firstFree = 0
    var fileId = 0
    val digits = chars.digits()
    val fileMap = IntArray(digits.sum())
    var fileMapSize = 0
    for (i in digits.indices) {
        val id = if (i and 1 == 0) {
            fileId++
        } else {
            -1
        }
        for (j in 0 until digits.getInt(i)) {
            fileMap[fileMapSize++] = id
        }
    }
    var lastFile = fileMap.size - 1
    while (true) {
        while (fileMap[firstFree] != -1) firstFree++
        while (fileMap[lastFile] == -1) lastFile--
        if (firstFree >= lastFile) break
        fileMap[firstFree] = fileMap[lastFile]
        fileMap[lastFile] = -1
    }
    return fileMap.sumToLongWithIndex { i, f -> if (f < 0) 0 else i.toLong() * f.toLong() }
}

class PosSizeList(val capacity: Int) {
    val pos = IntArray(capacity)
    val size = ByteArray(capacity)

    fun moveStart(index: Int, amount: Int) {
        pos[index] += amount
        size[index] = (size[index] - amount).toByte()
    }

    fun set(index: Int, pos: Int, size: Int) {
        this.pos[index] = pos
        this.size[index] = size.toByte()
    }
}

fun PuzzleInput.part2(): Any {
    var nextPos = 0
    val digits = chars.digits()
    val fileList = PosSizeList((digits.size + 1) / 2)
    val freeList = PosSizeList(digits.size / 2)
    var freeListSize = 0
    for (i in digits.indices) {
        val size = digits.getInt(i)
        val index = i / 2
        if (i and 1 == 0) {
            fileList.set(index, nextPos, size)
        } else if (size > 0) {
            freeList.set(freeListSize++, nextPos, size)
        }
        nextPos += size
    }
    val firstFree = IntArray(10)
    outer@for (file in fileList.pos.indices.reversed()) {
        val pos = fileList.pos[file]
        val size = fileList.size[file].toInt()
        while (freeList.pos[freeListSize - 1] > pos) freeListSize--
        while (freeList.size[firstFree[size]] < size) {
            firstFree[size]++
            if (firstFree[size] >= freeListSize) break
        }
        val freePos = firstFree[size]
        if (freePos >= freeListSize) if (size == 1) break else continue
        val entryPos = freeList.pos[freePos]
        fileList.pos[file] = entryPos
        freeList.moveStart(freePos, size)
    }
    return fileList.pos.sumToLongWithIndex { i, p ->
        val end = p + fileList.size[i] - 1
        i.toLong() * (end - p + 1) * (p + end) / 2
    }
}
