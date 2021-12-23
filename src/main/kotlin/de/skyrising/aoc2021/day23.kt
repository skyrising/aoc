package de.skyrising.aoc2021

import it.unimi.dsi.fastutil.ints.*
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

class BenchmarkDay23 : BenchmarkDayV1(23)

private const val DUMP_PATH = false

fun registerDay23() {
    val test = listOf(
        "#############",
        "#...........#",
        "###B#C#B#D###",
        "  #A#D#C#A#",
        "  #########"
    )
    puzzleLS(23, "Amphipod") {
        val layout = parseInput23(it)
        solve(layout, AmphipodLayout.SOLVED1)
    }
    puzzleLS(23, "Part Two") {
        val input = ArrayList(it)
        input.addAll(3, listOf("  #D#C#B#A#", "  #D#B#A#C#"))
        val layout = parseInput23(input)
        solve(layout, AmphipodLayout.SOLVED2)
    }
}

private fun solve(layout: AmphipodLayout, solved: AmphipodLayout): Int {
    val reached = Object2IntOpenHashMap<AmphipodLayout>()
    var minSolve = Int.MAX_VALUE
    reached.defaultReturnValue(-1)
    reached[layout] = 0
    val path = mutableMapOf<AmphipodLayout, AmphipodLayout>()
    val untraversed = ArrayDeque<AmphipodLayout>()
    untraversed.add(layout)
    while (untraversed.isNotEmpty()) {
        minSolve = solveStep(untraversed, reached, minSolve, path, solved)
    }
    if (DUMP_PATH) {
        val pathList = mutableListOf(solved)
        var l = path[solved]
        while (l != null) {
            pathList.add(0, l)
            l = path[l]
        }
        var cost = 0
        for (p in pathList) {
            val c = reached.getInt(p)
            println(c - cost)
            cost = c
            println(p)
        }
    }
    return reached.getInt(solved)
}

private fun solveStep(
    untraversed: ArrayDeque<AmphipodLayout>,
    reached: Object2IntOpenHashMap<AmphipodLayout>,
    minSolve: Int,
    path: MutableMap<AmphipodLayout, AmphipodLayout>,
    solved: AmphipodLayout
): Int {
    val current = untraversed.removeFirst()
    val cost = reached.getInt(current)
    val next = current.getPossibleMoves()
    var minSolve1 = minSolve
    for (n in next.object2IntEntrySet()) {
        val nextLayout = n.key
        val pathCost = n.intValue
        val newCost = cost + pathCost
        if (newCost >= minSolve1) continue
        val existing = reached.getInt(nextLayout)
        if (existing < 0 || existing > newCost) {
            if (nextLayout == solved) {
                minSolve1 = newCost
            } else {
                untraversed.add(nextLayout)
            }
            reached[nextLayout] = newCost
            if (DUMP_PATH) path[nextLayout] = current
        }
    }
    return minSolve1
}

private fun parseInput23(input: List<String>): AmphipodLayout {
    val data = CharArray(11 + (input.size - 3) * 4)
    for (i in 0 until 11) data[i] = input[1][1 + i]
    for (j in 0 until input.size - 3) {
        for (i in 0 until 4) data[11 + 4 * j + i] = input[2 + j][3 + 2 * i]
    }
    return AmphipodLayout(data)
}

data class AmphipodLayout(val data: CharArray) {
    private val hash = data.contentHashCode()
    override fun equals(other: Any?) = this === other || (other is AmphipodLayout && hash == other.hash && data.contentEquals(other.data))
    override fun hashCode() = hash

    override fun toString(): String {
        val sb = StringBuilder("#############\n#")
        for (i in 0 until 11) sb.append(data[i])
        sb.append("#\n###")
        for (i in 11 until 15) sb.append(data[i]).append('#')
        sb.append("##\n  #")
        for (i in 15 until data.size) {
            sb.append(data[i]).append('#')
            if ((i - 11) % 4 == 3) sb.append("\n  #")
        }
        sb.append("########")
        return sb.toString()
    }

    fun move(from: Int, to: Int): AmphipodLayout {
        val newData = data.copyOf()
        newData[to] = data[from]
        newData[from] = '.'
        return AmphipodLayout(newData)
    }

    fun getPossibleMoves(): Object2IntMap<AmphipodLayout> {
        val possible = Object2IntLinkedOpenHashMap<AmphipodLayout>()
        outer@for (i in data.indices) {
            val c = data[i]
            if (c == '.') continue
            val targetRoom = c - 'A'
            // move to the bottom of the room if there is no foreign pod
            for (j in data.size - 4 + targetRoom downTo 11 step 4) {
                val present = data[j]
                if (present != '.' && present != c) break
                val cost = getCost(i, j)
                if (cost == 0) continue
                possible[move(i, j)] = cost
                continue@outer
            }
            // move along the hallway
            if (i >= 11) {
                for (j in VALID_HALLWAY) {
                    val cost = getCost(i, j)
                    if (cost == 0) continue
                    possible[move(i, j)] = cost
                }
            }
        }
        return possible
    }

    private fun getCost(from: Int, to: Int): Int {
        //if (data[from] == '.' || data[to] != '.') return 0
        //if (to in 2..8 && to % 2 == 0) return 0
        //if (to >= 15 && data[to - 4] != '.') return 0
        //if (!isCorrectRoom(to, data[from])) return 0
        val path = getPath(from, to)
        path.forEach { if (data[it] != '.') return 0 }
        return path.size * COST_MULTIPLIER[data[from] - 'A']
    }

    companion object {
        val SOLVED1 = AmphipodLayout("...........ABCDABCD".toCharArray())
        val SOLVED2 = AmphipodLayout("...........ABCDABCDABCDABCD".toCharArray())
        private val COST_MULTIPLIER = arrayOf(1, 10, 100, 1000)
        private val VALID_HALLWAY = arrayOf(0, 1, 3, 5, 7, 9, 10)
        private val PATHS = Int2ObjectOpenHashMap<Int2ObjectMap<IntList>>()

        private fun isCorrectRoom(room: Int, pod: Char): Boolean {
            if (room < 11) return true
            return (room - 11) % 4 == pod - 'A'
        }

        private fun getPositionInFront(pos: Int) = if (pos >= 11) 2 + 2 * ((pos - 11) % 4) else pos

        private fun getPath(from: Int, to: Int) = PATHS.computeIfAbsent(from, Int2ObjectFunction{ Int2ObjectOpenHashMap() }).computeIfAbsent(to, Int2ObjectFunction{ computePath(from, to) })

        private fun computePath(from: Int, to: Int): IntList {
            val path = IntArrayList()
            if (from == to) return path
            var p = from
            val via = getPositionInFront(to)
            while (p >= 15) {
                p -= 4
                path.add(p)
            }
            if (p >= 11) {
                p = getPositionInFront(p)
                path.add(p)
            }
            while (p > via) path.add(--p)
            while (p < via) path.add(++p)
            if (p == to) return path
            p = 11 + (to - 11) % 4
            path.add(p)
            while (p < to) {
                p += 4
                path.add(p)
            }
            return path
        }
    }
}