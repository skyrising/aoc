package de.skyrising.aoc2020

import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import java.util.*
import kotlin.math.sqrt

class BenchmarkDay20 : BenchmarkDayV1(20)

private class Tile(val id: Int, val width: Int, val height: Int, val flipped: Boolean = false, val rotated: Int = 0) {
    private val data = BooleanArray(width * height)
    operator fun get(x: Int, y: Int) = data[width * y + x]
    operator fun set(x: Int, y: Int, value: Boolean) {
        data[width * y + x] = value
    }

    fun flipY(): Tile {
        val tile = Tile(id, width, height, !flipped, rotated)
        for (y in 0 until height) for (x in 0 until width) {
            tile[x, y] = this[width - x - 1, y]
        }
        return tile
    }

    fun rotateClockwise90(): Tile {
        val tile = Tile(id, height, width, flipped, (rotated + 90) % 360)
        for (y in 0 until height) for (x in 0 until width) {
            tile[y, x] = this[x, height - y - 1]
        }
        return tile
    }

    fun matchRight(tile: Tile): Boolean {
        if (tile.height != height) return false
        for (i in 0 until height) {
            if (this[width - 1, i] != tile[0, i]) return false
        }
        return true
    }

    fun matchBottom(tile: Tile): Boolean {
        if (tile.width != width) return false
        for (i in 0 until width) {
            if (this[i, height - 1] != tile[i, 0]) return false
        }
        return true
    }

    fun allRotations(): Set<Tile> {
        val tile = this
        val flipped = tile.flipY()
        val tile90 = tile.rotateClockwise90()
        val flipped90 = flipped.rotateClockwise90()
        val tile180 = tile90.rotateClockwise90()
        val flipped180 = flipped90.rotateClockwise90()
        val tile270 = tile180.rotateClockwise90()
        val flipped270 = flipped180.rotateClockwise90()
        return setOf(
            tile, flipped,
            tile90, flipped90,
            tile180, flipped180,
            tile270, flipped270
        )
    }

    override fun equals(other: Any?) = other is Tile && other.id == id && other.data.contentEquals(data)

    override fun hashCode() = Objects.hash(id, data)

    override fun toString() = "Tile(id=$id,flip=$flipped,rot=$rotated"

    fun contentToString(): String {
        val sb = StringBuilder((width + 1) * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                sb.append(if (this[x, y]) '#' else '.')
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    fun matches(pattern: Tile, x: Int, y: Int): Boolean {
        if (x < 0 || y < 0 || x + pattern.width > width || y + pattern.height > height) return false
        for (y1 in 0 until pattern.height) {
            for (x1 in 0 until pattern.width) {
                if (pattern[x1, y1] && !this[x + x1, y + y1]) return false
            }
        }
        return true
    }

    fun find(pattern: Tile, xStart: Int = 0, yStart: Int = 0): Pair<Int, Int>? {
        if (pattern.width == 0 || pattern.height == 0) return Pair(xStart, yStart)
        for (y in yStart .. height - pattern.height) {
            val start = if (y == yStart) xStart else 0
            for (x in start .. width - pattern.width) {
                if (matches(pattern, x, y)) return Pair(x, y)
            }
        }
        return null
    }

    fun findAll(pattern: Tile): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        var pos: Pair<Int, Int>? = Pair(-1, 0)
        while (pos != null) {
            pos = find(pattern, pos.first + 1, pos.second)
            if (pos != null) result.add(pos)
        }
        return result
    }

    fun count(value: Boolean = true) = data.count { it == value }

    companion object {
        fun parseTiles(lines: List<String>, maxSize: Int = 100): List<Tile> {
            val tiles = mutableListOf<Tile>()
            val temp = BooleanArray(maxSize)
            var id = -1
            var width = 0
            var height = 0
            fun add() {
                val tile = Tile(id, width, height)
                System.arraycopy(temp, 0, tile.data, 0, width * height)
                id = -1
                width = 0
                height = 0
                tiles.add(tile)
            }
            for (line in lines) {
                when {
                    line.isEmpty() -> {
                        add()
                    }
                    line.startsWith("Tile ") -> {
                        id = line.substring(5, line.length - 1).toInt()
                    }
                    else -> {
                        width = line.length
                        for (i in line.indices) {
                            temp[height * width + i] = line[i] == '#'
                        }
                        height++
                    }
                }
            }
            if (id >= 0) add()
            return tiles
        }

        fun getVariants(tiles: List<Tile>): Int2ObjectMap<Set<Tile>> {
            val variants = Int2ObjectOpenHashMap<Set<Tile>>()
            for (tile in tiles) {
                variants[tile.id] = tile.allRotations()
            }
            return variants
        }

        fun search(found: Array<Tile>, used: IntSet, variants: Int2ObjectMap<Set<Tile>>, size: Int, predicate: (Tile, Tile) -> Boolean): List<Array<Tile>> {
            if (size == 0) return listOf(found)
            val result = mutableListOf<Array<Tile>>()
            val last = found.lastOrNull()
            for ((id, v) in variants) {
                if (id in used) continue
                for (t in v) {
                    //if (last == null && (t.flipped || t.rotated != 0)) continue
                    if (last != null && !predicate(last, t)) continue
                    val newFound = found + t
                    val newUsed = IntOpenHashSet(used)
                    newUsed.add(t.id)
                    val subResults = search(newFound, newUsed, variants, size - 1, predicate)
                    result.addAll(subResults)
                }
            }
            return result
        }

        fun findBorders(variants: Int2ObjectMap<Set<Tile>>): List<Pair<Array<Tile?>, IntSet>> {
            val size = sqrt(variants.size.toDouble()).toInt()
            val top = search(emptyArray(), IntSet.of(), variants, size, Tile::matchRight)
            val borders = mutableListOf<Pair<Array<Tile?>, IntSet>>()
            for (sTop in top) {
                val usedTop = IntOpenHashSet()
                for (t in sTop) usedTop.add(t.id)
                val left = search(arrayOf(sTop[0]), usedTop, variants, size - 1, Tile::matchBottom)
                for (sLeft in left) {
                    val usedLeft = IntOpenHashSet(usedTop)
                    for (t in sLeft) usedLeft.add(t.id)
                    val right = search(arrayOf(sTop.last()), usedLeft, variants, size - 1, Tile::matchBottom)
                    for (sRight in right) {
                        val usedRight = IntOpenHashSet(usedLeft)
                        for (t in sRight) usedRight.add(t.id)
                        val bottom = search(arrayOf(sLeft.last()), usedRight, variants, size - 2, Tile::matchRight)
                        for (sBottom in bottom) {
                            if (!sBottom.last().matchRight(sRight.last())) continue
                            val used = IntOpenHashSet(usedRight)
                            for (t in sBottom) used.add(t.id)
                            borders.add(Pair(Array(variants.size) {
                                val x = it % size
                                val y = it / size
                                if (y == 0) return@Array sTop[x]
                                if (x == 0) return@Array sLeft[y]
                                if (x == size - 1) return@Array sRight[y]
                                if (y == size - 1) return@Array sBottom[x]
                                null
                            }, used))
                        }
                    }
                }
            }
            return borders
        }

        fun solveFull(variants: Int2ObjectMap<Set<Tile>>): List<Array<Tile>> {
            val solutions = mutableListOf<Array<Tile>>()
            val size = sqrt(variants.size.toDouble()).toInt()
            val borders = findBorders(variants)
            for ((border, used) in borders) {
                solutions.addAll(solveMiddle(border, used, variants, size))
            }
            return solutions
        }

        fun solveMiddle(known: Array<Tile?>, used: IntSet, variants: Int2ObjectMap<Set<Tile>>, size: Int = sqrt(known.size.toDouble()).toInt()): List<Array<Tile>> {
            val candidates = PriorityQueue<Pair<Int, MutableSet<Tile>>> { a, b -> a.second.size - b.second.size }
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val i = y * size + x
                    if (known[i] != null) continue
                    val currentCandidates = mutableSetOf<Tile>()
                    for ((id, variant) in variants) {
                        if (id in used) continue
                        for (tile in variant) {
                            val up = if (y == 0) null else known[i - size]
                            if (up != null && !up.matchBottom(tile)) continue
                            val left = if (x == 0) null else known[i - 1]
                            if (left != null && !left.matchRight(tile)) continue
                            val right = if (x == size - 1) null else known[i + 1]
                            if (right != null && !tile.matchRight(right)) continue
                            val down = if (y == size - 1) null else known[i + size]
                            if (down != null && !tile.matchBottom(down)) continue
                            currentCandidates.add(tile)
                        }
                    }
                    if (currentCandidates.isEmpty()) return emptyList()
                    candidates.add(Pair(i, currentCandidates))
                }
            }
            while (candidates.isNotEmpty()) {
                // println("${known.size - candidates.size}/${known.size}")
                val (i, currentCandidates) = candidates.poll()
                if (currentCandidates.size == 1) {
                    val tile = currentCandidates.single()
                    known[i] = tile
                    used.add(tile.id)
                    val allCandidates = ArrayList(candidates)
                    candidates.clear()
                    for ((j, other) in allCandidates) {
                        other.removeIf {
                            it.id == tile.id ||
                            (j == i + 1 && !tile.matchRight(it)) ||
                            (j == i - 1 && !it.matchRight(tile)) ||
                            (j == i + size && !tile.matchBottom(it)) ||
                            (j == i - size && !it.matchBottom(tile))
                        }
                        if (other.isEmpty()) return emptyList()
                        candidates.add(Pair(j, other))
                    }
                } else {
                    throw IllegalStateException("Multiple tries not implemented: ${currentCandidates.size}")
                }
            }
            return listOf(known as Array<Tile>)
        }

        fun combine(id: Int, tiles: Array<Tile>, width: Int): Tile {
            val borderlessTileWidth = tiles[0].width - 2
            val borderlessTileHeight = tiles[0].height - 2
            val height = tiles.size / width
            val pixelWidth = borderlessTileWidth * width
            val pixelHeight = borderlessTileHeight * height
            val tile = Tile(id, pixelWidth, pixelHeight)
            for (y in 0 until pixelHeight) {
                for (x in 0 until pixelWidth) {
                    val tileX = x / borderlessTileWidth
                    val tileY = y / borderlessTileHeight
                    tile[x, y] = tiles[tileX + width * tileY][1 + x % borderlessTileWidth, 1 + y % borderlessTileHeight]
                }
            }
            return tile
        }
    }
}

fun registerDay20() {
    val test = TestInput("""
        Tile 2311:
        ..##.#..#.
        ##..#.....
        #...##..#.
        ####.#...#
        ##.##.###.
        ##...#.###
        .#.#.#..##
        ..#....#..
        ###...#.#.
        ..###..###

        Tile 1951:
        #.##...##.
        #.####...#
        .....#..##
        #...######
        .##.#....#
        .###.#####
        ###.##.##.
        .###....#.
        ..#.#..#.#
        #...##.#..

        Tile 1171:
        ####...##.
        #..##.#..#
        ##.#..#.#.
        .###.####.
        ..###.####
        .##....##.
        .#...####.
        #.##.####.
        ####..#...
        .....##...

        Tile 1427:
        ###.##.#..
        .#..#.##..
        .#.##.#..#
        #.#.#.##.#
        ....#...##
        ...##..##.
        ...#.#####
        .#.####.#.
        ..#..###.#
        ..##.#..#.

        Tile 1489:
        ##.#.#....
        ..##...#..
        .##..##...
        ..#...#...
        #####...#.
        #..#.#.#.#
        ...#.#.#..
        ##.#...##.
        ..##.##.##
        ###.##.#..

        Tile 2473:
        #....####.
        #..#.##...
        #.##..#...
        ######.#.#
        .#...#.#.#
        .#########
        .###.#..#.
        ########.#
        ##...##.#.
        ..###.#.#.

        Tile 2971:
        ..#.#....#
        #...###...
        #.#.###...
        ##.##..#..
        .#####..##
        .#..####.#
        #..#.#..#.
        ..####.###
        ..#.#.###.
        ...#.#.#.#

        Tile 2729:
        ...#.#.#.#
        ####.#....
        ..#.#.....
        ....#..#.#
        .##..##.#.
        .#.####...
        ####.#.#..
        ##.####...
        ##..#.##..
        #.##...##.

        Tile 3079:
        #.#.#####.
        .#..######
        ..#.......
        ######....
        ####.#..#.
        .#...#.##.
        #.#####.##
        ..#.###...
        ..#.......
        ..#.###...
    """)
    puzzle(20, "Jurassic Jigsaw v1") {
        val tiles = Tile.parseTiles(lines)
        val variants = Tile.getVariants(tiles)
        val size = sqrt(tiles.size.toDouble()).toInt()
        val borders = Tile.findBorders(variants)
        val dedup = mutableSetOf<IntSet>()
        val bordersDedup = mutableListOf<Array<Tile?>>()
        for ((sBorder, _) in borders) {
            val corners = IntSet.of(sBorder[0]!!.id, sBorder[size - 1]!!.id, sBorder[(size - 1) * size]!!.id, sBorder[size * size - 1]!!.id)
            if (dedup.add(corners)) {
                bordersDedup.add(sBorder)
            }
        }
        if (dedup.size == 1) return@puzzle dedup.single().map(Int::toLong).reduceRight(Long::times)
        0
    }
    puzzle(20, "Part 2 v1") {
        val seaMonster = Tile.parseTiles("""
            |Tile 1:
            |                  # 
            |#    ##    ##    ###
            | #  #  #  #  #  #   
        """.trimMargin("|").split("\n"), 60).single()
        val tiles = Tile.parseTiles(lines)
        val variants = Tile.getVariants(tiles)
        val size = sqrt(tiles.size.toDouble()).toInt()
        val solutions = Tile.solveFull(variants)
        val allSolutions = mutableSetOf<Tile>()
        for (solution in solutions) {
            allSolutions.addAll(Tile.combine(0, solution, size).allRotations())
        }
        for (solution in allSolutions) {
            val monsters = solution.findAll(seaMonster)
            if (monsters.isEmpty()) continue
            // println(solution.contentToString())
            for ((xOff, yOff) in monsters) {
                for (y in 0 until seaMonster.height) {
                    for (x in 0 until seaMonster.width) {
                        if (seaMonster[x, y]) solution[x + xOff, y + yOff] = false
                    }
                }
            }
            return@puzzle solution.count()
        }
        0
    }
}