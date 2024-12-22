package de.skyrising.aoc2024.day22

import de.skyrising.aoc.IntArrayDeque
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

val test = TestInput("""
1
2
3
2024
""")

fun next(num: Long): Long {
    val a = (num xor (num shl 6)) and 0xffffff
    val b = (a xor (a ushr 5)) and 0xffffff
    return (b xor (b shl 11)) and 0xffffff
}

@PuzzleName("Monkey Market")
fun PuzzleInput.part1() = lines.sumOf {
    var num = it.toLong()
    repeat(2000) {
        num = next(num)
    }
    num
}

fun PuzzleInput.part2(): Any {
    val count = Int2IntOpenHashMap()
    fun pack(a: Int, b: Int, c: Int, d: Int) = (a shl 24) or ((b and 0xff) shl 16) or ((c and 0xff) shl 8) or (d and 0xff)
    fun unpack(n: Int) = listOf((n shr 24).toByte().toInt(), (n shr 16).toByte().toInt(), (n shr 8).toByte().toInt(), n.toByte().toInt())
    val history = IntArrayDeque()
    var maxCount = 0
    for (init in lines) {
        history.clear()
        var num = init.toLong()
        val seen = IntOpenHashSet(2000)
        repeat(2001) {
            val n = next(num)
            history.enqueue((n % 10 - num % 10).toInt())
            while (history.size() > 4) {
                val packed = pack(history[0], history[1], history[2], history[3])
                if (seen.add(packed)) {
                    val oldCount = count.addTo(packed, (num % 10).toInt())
                    val newCount = oldCount + (num % 10).toInt()
                    if (newCount > maxCount) {
                        maxCount = newCount
                    }
                }
                history.dequeueInt()
            }
            num = n
        }
    }
    return maxCount
}
