package de.skyrising.aoc2021

import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.longs.LongArrayList

class BenchmarkDay10 : BenchmarkDayV1(10)

fun registerDay10() {
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
    puzzle(10, "Syntax Scoring") {
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
    puzzle(10, "Part Two") {
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