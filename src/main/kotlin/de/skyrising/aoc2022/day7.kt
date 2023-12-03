package de.skyrising.aoc2022

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import java.nio.file.Path

@Suppress("unused")
class BenchmarkDay7 : BenchmarkDayV1(7)

private fun parseInput(input: PuzzleInput): Object2LongMap<Path> {
    val commands = mutableListOf<Pair<String, List<String>>>()
    var current: Pair<String, MutableList<String>>? = null
    for (line in input.lines) {
        if (line.startsWith("$")) {
            if (current != null) {
                commands.add(current)
            }
            current = Pair(line.substring(2), mutableListOf())
        } else {
            current!!.second.add(line)
        }
    }
    if (current != null) {
        commands.add(current)
    }
    val spaceUsed = Object2LongOpenHashMap<Path>()
    val root = Path.of("/")
    var pwd = root
    for ((command, output) in commands) {
        if (command.startsWith("cd ")) {
            pwd = pwd.resolve(command.substring(3)).normalize()
        } else if (command.startsWith("ls")) {
            for (line in output) {
                if (line.startsWith("dir")) continue
                val size = line.substringBefore(' ').toLong()
                val name = line.substringAfter(' ').trim()
                val path = root.resolve(pwd.resolve(name))
                for (i in 1 until path.nameCount) {
                    val parent = path.subpath(0, i)
                    spaceUsed[parent] = spaceUsed.getLong(parent) + size
                }
                spaceUsed[root] = spaceUsed.getLong(root) + size
            }
        }
    }
    return spaceUsed
}

@Suppress("unused")
fun registerDay7() {
    part1("No Space Left On Device") {
        val spaceUsed = parseInput(this)
        var sum = 0L
        for (v in spaceUsed.values) {
            if (v <= 100000) sum += v
        }
        sum
    }

    part2 {
        val spaceUsed = parseInput(this)
        var smallestMatching = Long.MAX_VALUE
        val used = spaceUsed.getLong(Path.of("/"))
        val available = 70000000 - used
        for (v in spaceUsed.values) {
            if (v + available >= 30000000) {
                if (v < smallestMatching) {
                    smallestMatching = v
                }
            }
        }
        smallestMatching
    }
}