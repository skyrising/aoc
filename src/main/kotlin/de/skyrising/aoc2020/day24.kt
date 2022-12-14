package de.skyrising.aoc2020

import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet

class BenchmarkDay24 : BenchmarkDayV1(24)

private enum class HexNeighbor(val xOff: Int, val yOff: Int) {
    E(1, 0),
    SE(0, 1),
    SW(-1, 1),
    W(-1, 0),
    NW(0, -1),
    NE(1, -1);

    fun apply(tile: Long): Long {
        val x = (tile and 0xffffffffL).toInt() + xOff
        val y = (tile shr 32).toInt() + yOff
        // println("$this -> $x, $y")
        return (y.toLong() shl 32) or (x.toLong() and 0xffffffffL)
    }
}

private fun tileXY(tile: Long) = Pair((tile and 0xffffffffL).toInt(), (tile shr 32).toInt())

private fun tileFromPath(start: Long, path: String): Long {
    var tile = start
    var i = 0
    while (i < path.length) {
        val neighbor = when (path[i++]) {
            'e' -> HexNeighbor.E
            'w' -> HexNeighbor.W
            's' -> when (path[i++]) {
                'e' -> HexNeighbor.SE
                'w' -> HexNeighbor.SW
                else -> throw IllegalArgumentException()
            }
            'n' -> when (path[i++]) {
                'e' -> HexNeighbor.NE
                'w' -> HexNeighbor.NW
                else -> throw IllegalArgumentException()
            }
            else -> throw IllegalArgumentException()
        }
        tile = neighbor.apply(tile)
    }
    return tile
}

private fun stepDay(grid: LongSet): LongSet {
    val tiles = LongOpenHashSet(grid)
    for (n in HexNeighbor.values()) {
        for (tile in grid) {
            tiles.add(n.apply(tile))
        }
    }
    // println("Checking: ${tiles.size} (${grid.size})")
    val newGrid = LongOpenHashSet(grid)
    for (tile in tiles) {
        var count = 0
        for (n in HexNeighbor.values()) {
            if (n.apply(tile) in grid) count++
        }
        if (tile in grid) {
            if (count == 0 || count > 2) newGrid.remove(tile)
        } else if (count == 2) {
            newGrid.add(tile)
        }
    }
    return newGrid
}

fun registerDay24() {
    val test = TestInput("""
        sesenwnenenewseeswwswswwnenewsewsw
        neeenesenwnwwswnenewnwwsewnenwseswesw
        seswneswswsenwwnwse
        nwnwneseeswswnenewneswwnewseswneseene
        swweswneswnenwsewnwneneseenw
        eesenwseswswnenwswnwnwsewwnwsene
        sewnenenenesenwsewnenwwwse
        wenwwweseeeweswwwnwwe
        wsweesenenewnwwnwsenewsenwwsesesenwne
        neeswseenwwswnwswswnw
        nenwswwsewswnenenewsenwsenwnesesenew
        enewnwewneswsewnwswenweswnenwsenwsw
        sweneswneswneneenwnewenewwneswswnese
        swwesenesewenwneswnwwneseswwne
        enesenwswwswneneswsenwnewswseenwsese
        wnwnesenesenenwwnenwsewesewsesesew
        nenewswnwewswnenesenwnesewesw
        eneswnwswnwsenenwnwnwwseeswneewsenese
        neswnwewnwnwseenwseesewsenwsweewe
        wseweeenwnesenwwwswnew
    """)
    puzzle(24, "Lobby Layout v1") {
        val grid = Long2BooleanOpenHashMap()
        for (line in lines) {
            val tile = tileFromPath(0, line)
            grid[tile] = !grid[tile]
        }
        grid.values.sumOf { if (it) 1L else 0 }
    }
    puzzle(24, "Part 2 v1") {
        val tiles = LongArrayList()
        for (line in lines) tiles.add(tileFromPath(0, line))
        var grid: LongSet = LongOpenHashSet()
        for (tile in tiles) {
            if (tile in grid) {
                grid.remove(tile)
            } else {
                grid.add(tile)
            }
        }
        repeat(100) {
            grid = stepDay(grid)
            // println("Day ${it + 1}: ${grid.size}")
        }
        grid.size
    }
}
