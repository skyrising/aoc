package de.skyrising.aoc2023

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList

@Suppress("unused")
class BenchmarkDay5 : BenchmarkDayV1(5)

@Suppress("unused")
fun registerDay5() {
    val test = TestInput("""
        seeds: 79 14 55 13
        
        seed-to-soil map:
        50 98 2
        52 50 48
        
        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15
        
        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4
        
        water-to-light map:
        88 18 7
        18 25 70
        
        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13
        
        temperature-to-humidity map:
        0 69 1
        1 0 69
        
        humidity-to-location map:
        60 56 37
        56 93 4
    """)
    data class Range(val destStart: Long, val sourceStart: Long, val length: Long) {
        val source = sourceStart until sourceStart + length
        override fun toString() = "$source->${destStart..<destStart+length}"
    }
    class RangeMap(val ranges: List<Range>) {
        private var splitPoints = LongArrayList()
        private var sorted = false

        init {
            for (range in ranges) {
                splitPoints.add(range.sourceStart)
                splitPoints.add(range.sourceStart + range.length)
            }
        }

        operator fun get(key: Long): Long {
            return ranges.firstOrNull { key in it.source }?.let { it.destStart + (key - it.sourceStart) } ?: key
        }

        operator fun get(range: LongRange): List<LongRange> {
            return range.splitAt(splitPoints).map {
                get(it.first)..get(it.last)
            }.toList()
        }

        override fun toString() = "RangeMap(${ranges.joinToString(", ")})"
    }
    data class Almanac(val seeds: LongList, val maps: List<RangeMap>)
    fun parse(input: PuzzleInput): Almanac {
        val lists = input.lines.splitOnEmpty()
        return Almanac(
            lists[0][0].longs(),
            lists.map { list ->
                RangeMap(list.subList(1, list.size).map {
                    val entry = it.longs()
                    Range(entry.getLong(0), entry.getLong(1), entry.getLong(2))
                }.sortedBy { it.sourceStart })
            }
        )
    }
    part1("If You Give A Seed A Fertilizer") {
        val almanac = parse(this)
        almanac.seeds.minOf {
            almanac.maps.fold(it) { value, map -> map[value] }
        }
    }
    part2 {
        val almanac = parse(this)
        val seedRanges = almanac.seeds.chunked(2).map { it[0] until it[0] + it[1] }
        seedRanges.minOf {
            almanac.maps.fold(setOf(it)) { ranges, map ->
                joinRanges(ranges.flatMap(map::get))
            }.first().first
        }
    }
}