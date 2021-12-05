package de.skyrising.aoc2021

class BenchmarkDay5 : BenchmarkDayV1(5)

fun coord(s: String): Pair<Int, Int> {
    val (x, y) = s.split(',')
    return x.toInt() to y.toInt()
}

fun registerDay5() {
    val test = listOf("0,9 -> 5,9", "8,0 -> 0,8", "9,4 -> 3,4", "2,2 -> 2,1", "7,0 -> 7,4", "6,4 -> 2,0", "0,9 -> 2,9", "3,4 -> 1,4", "0,0 -> 8,8", "5,5 -> 8,2")
    puzzleLS(5, "Hydrothermal Venture") {
        val width = 1000
        val height = 1000
        val vents = ShortArray(width * height)
        for (line in it) {
            val parts = line.split(' ')
            val from = coord(parts[0])
            val to = coord(parts[2])
            if (from.first == to.first) {
                for (y in minOf(from.second, to.second) .. maxOf(from.second, to.second)) {
                    vents[y * width + from.first]++
                }
            } else if (from.second == to.second) {
                for (x in minOf(from.first, to.first) .. maxOf(from.first, to.first)) {
                    vents[from.second * width + x]++
                }
            }
        }
        vents.count { n -> n > 1 }
    }
    puzzleLS(5, "Part Two") {
        val width = 1000
        val height = 1000
        val vents = ShortArray(width * height)
        for (line in it) {
            val parts = line.split(' ')
            val from = coord(parts[0])
            val to = coord(parts[2])
            val min = minOf(from.first, to.first) to minOf(from.second, to.second)
            val max = maxOf(from.first, to.first) to maxOf(from.second, to.second)
            if (min.first == max.first) {
                for (y in min.second .. max.second) {
                    vents[y * width + min.first]++
                }
            } else if (min.second == max.second) {
                for (x in min.first .. max.first) {
                    vents[min.second * width + x]++
                }
            } else if (min.second - min.first == max.second - max.first) {
                val dirX = if (to.first >= from.first) 1 else -1
                val dirY = if (to.second >= from.second) 1 else -1
                for (i in 0 .. (max.first - min.first)) {
                    val x = from.first + i * dirX
                    val y = from.second + i * dirY
                    vents[y * width + x]++
                }
            }
        }
        vents.count { n -> n > 1 }
    }
}