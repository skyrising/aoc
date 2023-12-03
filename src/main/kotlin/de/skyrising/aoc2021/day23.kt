package de.skyrising.aoc2021

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

@Suppress("unused")
class BenchmarkDay23 : BenchmarkDayV1(23)

private const val DUMP_PATH = false

@Suppress("unused")
fun registerDay23() {
    val test = TestInput("""
        #############
        #...........#
        ###B#C#B#D###
          #A#D#C#A#
          #########
    """)
    part1("Amphipod") {
        val layout = parseInput(lines)
        solve(layout, AmphipodLayout.SOLVED1)
    }
    part2 {
        val input = ArrayList(lines)
        input.addAll(3, listOf("  #D#C#B#A#", "  #D#B#A#C#"))
        val layout = parseInput(input)
        solve(layout, AmphipodLayout.SOLVED2)
    }
}

private fun solve(layout: AmphipodLayout, solved: AmphipodLayout): Int {
    val reached = Object2IntOpenHashMap<AmphipodLayout>()
    var minSolve = Int.MAX_VALUE
    reached.defaultReturnValue(Int.MAX_VALUE)
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
    var minSolve1 = minSolve
    current.forPossibleMoves { nextLayout, pathCost ->
        val newCost = cost + pathCost
        if (newCost >= minSolve1 || newCost >= reached.getInt(nextLayout)) return@forPossibleMoves
        if (nextLayout == solved) {
            minSolve1 = newCost
        } else {
            untraversed.add(nextLayout)
        }
        reached[nextLayout] = newCost
        if (DUMP_PATH) path[nextLayout] = current
    }
    return minSolve1
}

private fun parseInput(input: List<String>): AmphipodLayout {
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

    // move to the bottom of the room if there is no foreign pod
    inline fun collectMovesToRoom(from: Int, c: Char, targetRoom: Int, callback: (AmphipodLayout, Int) -> Unit): Boolean {
        val data = this.data
        var to = data.size - 4 + targetRoom
        while (to >= 11) {
            val present = data[to]
            if (present != '.' && present != c) break
            val cost = getCost(from, to)
            if (cost != 0) {
                callback(move(from, to), cost)
                return true
            }
            to -= 4
        }
        return false
    }

    inline fun collectMovesToHallway(from: Int, callback: (AmphipodLayout, Int) -> Unit) {
        if (from < 11) return
        for (to in VALID_HALLWAY) {
            val cost = getCost(from, to)
            if (cost == 0) continue
            callback(move(from, to), cost)
        }
    }

    inline fun forPossibleMoves(callback: (AmphipodLayout, Int) -> Unit) {
        val size = data.size
        for (from in 0 until size) {
            val c = data[from]
            if (c == '.') continue
            if (collectMovesToRoom(from, c, c - 'A', callback)) continue
            collectMovesToHallway(from, callback)
        }
    }

    fun getCost(from: Int, to: Int): Int {
        val data = this.data
        //if (data[from] == '.' || data[to] != '.') return 0
        //if (to in 2..8 && to % 2 == 0) return 0
        //if (to >= 15 && data[to - 4] != '.') return 0
        //if (!isCorrectRoom(to, data[from])) return 0
        val path = getPath(from, to)
        for (i in path.indices) {
            if (data[path.getInt(i)] != '.') return 0
        }
        return path.size * COST_MULTIPLIER[data[from] - 'A']
    }

    companion object {
        val SOLVED1 = AmphipodLayout("...........ABCDABCD".toCharArray())
        val SOLVED2 = AmphipodLayout("...........ABCDABCDABCDABCD".toCharArray())
        private val COST_MULTIPLIER = intArrayOf(1, 10, 100, 1000)
        val VALID_HALLWAY = intArrayOf(0, 1, 3, 5, 7, 9, 10)
        private val PATHS = Array<IntList?>(27 * 27) { null }

        private fun isCorrectRoom(room: Int, pod: Char): Boolean {
            if (room < 11) return true
            return (room - 11) % 4 == pod - 'A'
        }

        private fun getPositionInFront(pos: Int) = if (pos >= 11) 2 + 2 * ((pos - 11) % 4) else pos

        private fun getPath(from: Int, to: Int): IntList {
            val key = from * 27 + to
            var path = PATHS[key]
            if (path == null) {
                path = computePath(from, to)
                PATHS[key] = path
            }
            return path
        }

        private fun computePath(from: Int, to: Int): IntList {
            val path = IntArrayList(12)
            if (from == to) return path
            var p = from
            val via = getPositionInFront(to)
            while (p >= 15) {
                p -= 4
                path.add(p)
            }
            if (p >= 11) {
                p = 2 + 2 * ((p - 11) % 4)
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