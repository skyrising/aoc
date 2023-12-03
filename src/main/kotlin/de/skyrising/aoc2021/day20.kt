package de.skyrising.aoc2021

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import java.util.*

@Suppress("unused")
class BenchmarkDay20 : BenchmarkDayV1(20)

@Suppress("unused")
fun registerDay20() {
    val test = TestInput("""
        ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

        #..#.
        #....
        ##..#
        ..#..
        ..###
    """)
    part1("Trench Map") {
        val (algorithm, image) = parseInput(this)
        var img = InfiniteBitImage(image, 0, 0, false)
        repeat(2) {
            img = processImageStep(img, algorithm)
        }
        img.count()
    }
    part2 {
        val (algorithm, image) = parseInput(this)
        var img = InfiniteBitImage(image, 0, 0, false)
        repeat(50) {
            img = processImageStep(img, algorithm)
        }
        img.count()
    }
}

private fun parseInput(input: PuzzleInput): Pair<BitSet, BitImage> {
    val algLine = input.lines[0]
    val alg = BitSet(algLine.length)
    for (i in algLine.indices) alg[i] = algLine[i] == '#'
    val img = BitImage(input.lines[2].length, input.lines.size - 2)
    for (y in 0 until img.height) {
        val line = input.lines[y + 2]
        for (x in 0 until img.width) {
            img[x, y] = line[x] == '#'
        }
    }
    return Pair(alg, img)
}

private fun processImageStep(image: InfiniteBitImage, algorithm: BitSet): InfiniteBitImage {
    val newImage = BitImage(image.width + 2, image.height + 2)
    for (y in 0 until newImage.height) {
        for (x in 0 until newImage.width) {
            newImage[x, y] = algorithm[image.getFilterIndex(x - 1 + image.offsetX, y - 1 + image.offsetY)]
        }
    }
    return InfiniteBitImage(newImage, image.offsetX - 1, image.offsetY - 1, algorithm[if (image.defaultValue) 0x1ff else 0])
}

data class BitImage(val data: BitSet, val width: Int, val height: Int) {
    constructor(width: Int, height: Int) : this(BitSet(width * height), width, height)

    operator fun get(x: Int, y: Int) = data[y * width + x]
    operator fun set(x: Int, y: Int, value: Boolean) {
        data[y * width + x] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0 until height) {
            if (y > 0) sb.append('\n')
            for (x in 0 until width) {
                sb.append(if (this[x, y]) '#' else '.')
            }
        }
        return sb.toString()
    }
}

data class InfiniteBitImage(val image: BitImage, val offsetX: Int, val offsetY: Int, val defaultValue: Boolean) {
    val width get() = image.width
    val height get() = image.height

    operator fun get(x: Int, y: Int): Boolean {
        if (x < offsetX || x >= offsetX + image.width || y < offsetY || y >= offsetY + image.height) {
            return defaultValue
        }
        return image[x - offsetX, y - offsetY]
    }

    fun getFilterIndex(x: Int, y: Int) = (if (this[x - 1, y - 1]) 0x100 else 0) or
            (if (this[x, y - 1]) 0x80 else 0) or
            (if (this[x + 1, y - 1]) 0x40 else 0) or
            (if (this[x - 1, y]) 0x20 else 0) or
            (if (this[x, y]) 0x10 else 0) or
            (if (this[x + 1, y]) 0x8 else 0) or
            (if (this[x - 1, y + 1]) 0x4 else 0) or
            (if (this[x, y + 1]) 0x2 else 0) or
            (if (this[x + 1, y + 1]) 0x1 else 0)

    fun count() = image.data.cardinality()

    override fun toString() = image.toString()
}