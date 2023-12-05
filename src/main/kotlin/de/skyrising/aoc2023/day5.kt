package de.skyrising.aoc2023

import de.skyrising.aoc.*
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
    data class Range(val destStart: Long, val sourceStart: Long, val length: Long) : Comparable<Range> {
        val source: LongRange get() = sourceStart until sourceStart + length

        operator fun get(source: Long): Long {
            if (source !in this.source) throw IllegalArgumentException("Source $source not in $this")
            return destStart + (source - sourceStart)
        }

        override fun compareTo(other: Range): Int {
            return sourceStart.compareTo(other.sourceStart)
        }

        override fun toString() = "$source->${destStart..<destStart+length}"
    }
    class RangeMap {
        private val ranges = mutableListOf<Range>()
        private var sorted = false
        fun add(destStart: Long, sourceStart: Long, length: Long) {
            ranges.add(Range(destStart, sourceStart, length))
        }

        fun get(key: Long): Long {
            if (!sorted) {
                ranges.sort()
                sorted = true
            }
            for (range in ranges) {
                if (key !in range.source) continue
                return range[key]
            }
            return key
        }

        fun get(range: LongRange): List<LongRange> {
            if (!sorted) {
                ranges.sort()
                sorted = true
            }
            val found = mutableListOf<LongRange>()
            var firstUnmapped = range.first
            for (mapRange in ranges) {
                if (mapRange.sourceStart > firstUnmapped) {
                    val num = minOf(range.last - firstUnmapped + 1, mapRange.sourceStart - firstUnmapped)
                    found.add(firstUnmapped..<firstUnmapped+num)
                    firstUnmapped += num
                }
                if (firstUnmapped in mapRange.source) {
                    val start = firstUnmapped
                    val num = minOf(range.last - firstUnmapped, mapRange.source.last - firstUnmapped)
                    val end = start + num
                    found.add(mapRange[start]..mapRange[end])
                    firstUnmapped = end + 1
                }
                if (firstUnmapped > range.last) break
            }
            if (firstUnmapped <= range.last) {
                found.add(firstUnmapped..range.last)
            }
            return found
        }

        override fun toString() = "RangeMap(${ranges.joinToString(", ")})"
    }
    data class Almanac(val seeds: LongList, val seedToSoil: RangeMap, val soilToFertilizer: RangeMap, val fertilizerToWater: RangeMap, val waterToLight: RangeMap, val lightToTemperature: RangeMap, val temperatureToHumidity: RangeMap, val humidityToLocation: RangeMap)
    fun parse(input: PuzzleInput): Almanac {
        var seeds: LongList? = null
        var currentMap: RangeMap? = null
        var currentMapName: String? = null
        val maps = mutableMapOf<String, RangeMap>()
        fun finish() {
            if (currentMap == null) return
            maps[currentMapName!!] = currentMap!!
            currentMap = null
            currentMapName = null
        }
        for (line in input.lines) {
            if (line.isEmpty()) {
                finish()
                continue
            }
            if (line.startsWith("seeds:"))  {
                seeds = line.substringAfter(": ").longs()
                continue
            }
            if (line.endsWith(" map:")) {
                currentMapName = line.substringBefore(" map:")
                currentMap = RangeMap()
                continue
            }
            val entry = line.longs()
            currentMap!!.add(entry.getLong(0), entry.getLong(1), entry.getLong(2))
        }
        finish()
        return Almanac(
            seeds!!,
            maps["seed-to-soil"]!!,
            maps["soil-to-fertilizer"]!!,
            maps["fertilizer-to-water"]!!,
            maps["water-to-light"]!!,
            maps["light-to-temperature"]!!,
            maps["temperature-to-humidity"]!!,
            maps["humidity-to-location"]!!
        )
    }
    part1("If You Give A Seed A Fertilizer") {
        val almanac = parse(this)
        almanac.seeds.minOf {
            val soil = almanac.seedToSoil.get(it)
            val fertilizer = almanac.soilToFertilizer.get(soil)
            val water = almanac.fertilizerToWater.get(fertilizer)
            val light = almanac.waterToLight.get(water)
            val temperature = almanac.lightToTemperature.get(light)
            val humidity = almanac.temperatureToHumidity.get(temperature)
            val location = almanac.humidityToLocation.get(humidity)
            location
        }
    }
    part2 {
        val almanac = parse(this)
        val seedRanges = almanac.seeds.chunked(2).map { it[0] until it[0] + it[1] }
        seedRanges.minOf {
            val soil = joinRanges(almanac.seedToSoil.get(it))
            val fertilizer = joinRanges(soil.flatMap { almanac.soilToFertilizer.get(it) })
            val water = joinRanges(fertilizer.flatMap { almanac.fertilizerToWater.get(it) })
            val light = joinRanges(water.flatMap { almanac.waterToLight.get(it) })
            val temperature = joinRanges(light.flatMap { almanac.lightToTemperature.get(it) })
            val humidity = joinRanges(temperature.flatMap { almanac.temperatureToHumidity.get(it) })
            val location = joinRanges(humidity.flatMap { almanac.humidityToLocation.get(it) })
            location.first().first
        }
    }
}