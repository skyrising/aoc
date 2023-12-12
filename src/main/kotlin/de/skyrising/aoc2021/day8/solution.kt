package de.skyrising.aoc2021.day8

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2021, 8)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
        fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
        fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
        aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
        fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
        dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
        bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
        egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
        gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagc
    """)
    part1("Seven Segment Search") {
        var count = 0
        for (line in lines) {
            val (_, output) = line.split(" | ", limit=2)
            val outputs = output.split(' ')
            for (o in outputs) {
                when (o.length) {
                    2, 3, 4, 7 -> count++
                }
            }
        }
        count
    }
    part2 {
        var sum = 0
        for (line in lines) {
            val signalToNumber = mutableMapOf<String, Int>()
            val numberToSignal = Array<String?>(10) { null }
            val (signal, output) = line.split(" | ", limit=2)
            val signals = signal.split(' ').map(String::sort)
            val segmentCount = IntArray(7)
            for (s in signals) {
                when (s.length) { // 1, 4, 7, 8
                    2 -> {
                        signalToNumber[s] = 1
                        numberToSignal[1] = s
                    }
                    3 -> {
                        signalToNumber[s] = 7
                        numberToSignal[7] = s
                    }
                    4 -> {
                        signalToNumber[s] = 4
                        numberToSignal[4] = s
                    }
                    7 -> {
                        signalToNumber[s] = 8
                        numberToSignal[8] = s
                    }
                }
                for (seg in s) {
                    segmentCount[seg - 'a']++
                }
            }
            val f = 'a' + segmentCount.findIndex { _, n -> n == 9 }!!
            val e = 'a' + segmentCount.findIndex { _, n -> n == 4 }!!
            val d = 'a' + segmentCount.findIndex { idx, n -> n == 7 && ('a' + idx) in numberToSignal[4]!! }!!
            for (s in signals) {
                if (s in signalToNumber) continue
                when (s.length) {
                    5 -> { // 2, 3, 5
                        var contains7 = true
                        for (seg in numberToSignal[7]!!) {
                            if (seg !in s) {
                                contains7 = false
                                break
                            }
                        }
                        if (contains7) {
                            signalToNumber[s] = 3
                            numberToSignal[3] = s
                        } else if (f in s) {
                            signalToNumber[s] = 5
                            numberToSignal[5] = s
                        } else {
                            signalToNumber[s] = 2
                            numberToSignal[2] = s
                        }
                    }
                    6 -> { // 0, 6, 9
                        if (d !in s) {
                            signalToNumber[s] = 0
                            numberToSignal[0] = s
                        } else if (e in s) {
                            signalToNumber[s] = 6
                            numberToSignal[6] = s
                        } else {
                            signalToNumber[s] = 9
                            numberToSignal[9] = s
                        }
                    }
                }
            }
            val outputs = output.split(' ')
            sum += (signalToNumber[outputs[0].sort()] ?: 0) * 1000
            sum += (signalToNumber[outputs[1].sort()] ?: 0) * 100
            sum += (signalToNumber[outputs[2].sort()] ?: 0) * 10
            sum += (signalToNumber[outputs[3].sort()] ?: 0) * 1
        }
        sum
    }
}

fun String.sort(): String {
    val chars = this.toCharArray()
    chars.sort()
    return chars.concatToString()
}

fun IntArray.findIndex(predicate: (Int, Int) -> Boolean): Int? {
    for (i in this.indices) {
        if (predicate(i, this[i])) return i
    }
    return null
}