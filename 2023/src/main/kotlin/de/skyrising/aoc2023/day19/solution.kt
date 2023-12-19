package de.skyrising.aoc2023.day19

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 19)

enum class Operator {
    LT, GT
}
data class Rule(val key: String?, val predicateOp: Operator, val predicateValue: Int, val next: String) {
    fun matches(ratings: Object2IntMap<String>): Boolean {
        if (key == null) return true
        val value = ratings.getInt(key)
        return when (predicateOp) {
            Operator.LT -> value < predicateValue
            Operator.GT -> value > predicateValue
        }
    }
    fun split(ranges: Map<String, IntRange>): Triple<String, List<Map<String, IntRange>>, List<Map<String, IntRange>>> {
        if (key == null) return Triple(next, listOf(ranges), emptyList())
        val range = ranges[key] ?: return Triple(next, emptyList(), listOf(ranges))
        val (matching, nonMatching) = when (predicateOp) {
            Operator.LT -> (range.first until predicateValue) to (predicateValue .. range.last)
            Operator.GT -> (predicateValue + 1 .. range.last) to (range.first .. predicateValue)
        }
        val matchingRanges = mutableListOf<Map<String, IntRange>>()
        val nonMatchingRanges = mutableListOf<Map<String, IntRange>>()
        if (!matching.isEmpty()) {
            val newRanges = ranges.toMutableMap()
            newRanges[key] = matching
            matchingRanges.add(newRanges)
        }
        if (!nonMatching.isEmpty()) {
            val newRanges = ranges.toMutableMap()
            newRanges[key] = nonMatching
            nonMatchingRanges.add(newRanges)
        }
        return Triple(next, matchingRanges, nonMatchingRanges)
    }

    override fun toString(): String {
        if (key == null) return next
        return "$key${"<>"[predicateOp.ordinal]}$predicateValue:$next"
    }
}
data class Workflow(val rules: List<Rule>)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}
        
        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """)
    fun parse(input: PuzzleInput): Pair<Map<String, Workflow>, List<Object2IntMap<String>>> {
        val (workflowLines, ratingLines) = input.lines.splitOnEmpty()
        val workflows = mutableMapOf<String, Workflow>()
        for (line in workflowLines) {
            val (name, rules) = line.split('{')
            val workflow = Workflow(rules.substringBefore('}').split(',').map { rule ->
                val parts = rule.split(':')
                if (parts.size == 1) {
                    Rule(null, Operator.GT, 0, parts[0])
                } else {
                    val (pred, next) = parts
                    val (_, key, opS, value) = Regex("(\\w+)([<>])(-?\\d+)").matchEntire(pred)!!.groups.map { it!!.value }
                    val op = when (opS) {
                        "<" -> Operator.LT
                        ">" -> Operator.GT
                        else -> throw IllegalArgumentException(opS)
                    }
                    Rule(key, op, value.toInt(), next)
                }
            })
            workflows[name] = workflow
        }
        val ratings = ratingLines.map { line ->
            val map = Object2IntLinkedOpenHashMap<String>()
            for (part in line.substring(1, line.length - 1).split(',')) {
                val (name, value) = part.split('=')
                map[name] = value.toInt()
            }
            map
        }
        return workflows to ratings
    }
    part1("Aplenty") {
        val (workflows, ratings) = parse(this)
        var result = 0
        for (rating in ratings) {
            var state = "in"
            while (state != "R" && state != "A") {
                val workflow = workflows[state]!!
                state = workflow.rules.first { it.matches(rating) }.next
            }
            log("$rating, $state")
            if (state == "A") {
                result += rating.getInt("x") + rating.getInt("m") + rating.getInt("a") + rating.getInt("s")
            }
        }
        result
    }
    part2 {
        val (workflows, _) = parse(this)
        val unprocessed = ArrayDeque<Triple<String, Int, Map<String, IntRange>>>()
        unprocessed.add(Triple("in", 0, mapOf("x" to 1..4000, "m" to 1..4000, "a" to 1..4000, "s" to 1..4000)))
        val accepted = mutableListOf<Map<String, IntRange>>()
        while (unprocessed.isNotEmpty()) {
            val (state, ruleIndex, ranges) = unprocessed.removeFirst()
            val workflow = workflows[state]!!
            if (ruleIndex >= workflow.rules.size) continue
            val rule = workflow.rules[ruleIndex]
            val (next, nextRanges, otherRanges) = rule.split(ranges)
            //log("$state:$ruleIndex $rule, $nextRanges, $otherRanges")
            for (r in nextRanges) {
                if (next == "A") accepted.add(r)
                else if (next != "R") unprocessed.add(Triple(next, 0, r))
            }
            for (r in otherRanges) unprocessed.add(Triple(state, ruleIndex + 1, r))
        }
        accepted.sumOf { r ->
            val x = r["x"]!!
            val m = r["m"]!!
            val a = r["a"]!!
            val s = r["s"]!!
            val score = (x.last - x.first + 1L) * (m.last - m.first + 1) * (a.last - a.first + 1) * (s.last - s.first + 1)
            //log("$r, $score")
            score
        }
    }
}