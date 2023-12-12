package de.skyrising.aoc2021.day6

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.bytes.ByteArrayList

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2021, 6)

@Suppress("unused")
fun register() {
    val test = TestInput("3,4,3,1,2")
    part1("Lanternfish") {
        val fish = ByteArrayList(chars.trim().split(',').map(String::toByte))
        for (day in 1..80) {
            val newFish = ByteArrayList()
            for (i in fish.indices) {
                var f = fish.getByte(i)
                if (f == 0.toByte()) {
                    f = 6
                    newFish.add(8)
                } else {
                    f--
                }
                fish.set(i, f)
            }
            fish.addAll(newFish)
        }
        fish.size
    }
    part2 {
        val fish = chars.trim().split(',').map(String::toInt)
        val createCount = LongArray(280)
        for (f in fish) createCount[f + 1]++
        val count = LongArray(280)
        count[0] = fish.size.toLong()
        for (day in 1..256) {
            val c = createCount[day]
            createCount[day + 7] += c
            createCount[day + 9] += c
            count[day] += count[day - 1] + c
        }
        count[256]
    }
}