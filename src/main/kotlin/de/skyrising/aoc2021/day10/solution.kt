package de.skyrising.aoc2021.day10

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.longs.LongArrayList

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2021, 10)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """)
    part1("Syntax Scoring") {
        var score = 0
        for (line in lines) {
            val stack = ArrayDeque<Char>()
            for (c in line) {
                when (c) {
                    '(' -> stack.add(')')
                    '[' -> stack.add(']')
                    '{' -> stack.add('}')
                    '<' -> stack.add('>')
                    else -> {
                        if (c != stack.removeLast()) {
                            score += when (c) {
                                ')' -> 3
                                ']' -> 57
                                '}' -> 1197
                                '>' -> 25137
                                else -> throw IllegalArgumentException(c.toString())
                            }
                            break
                        }
                    }
                }
            }
        }
        score
    }
    part2 {
        val scores = LongArrayList()
        outer@for (line in lines) {
            val stack = ArrayDeque<Char>()
            for (c in line) {
                when (c) {
                    '(' -> stack.add(')')
                    '[' -> stack.add(']')
                    '{' -> stack.add('}')
                    '<' -> stack.add('>')
                    else -> {
                        if (c != stack.removeLast()) {
                            continue@outer
                        }
                    }
                }
            }
            var score = 0L
            while (stack.isNotEmpty()) {
                score = score * 5 + when (stack.removeLast()) {
                    ')' -> 1
                    ']' -> 2
                    '}' -> 3
                    '>' -> 4
                    else -> throw IllegalArgumentException()
                }
            }
            scores.add(score)
        }
        scores.sort()
        scores.getLong(scores.size / 2)
    }
}