package de.skyrising.aoc2023.day19

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 19)

enum class Operator {
    LT, GT;
    companion object {
        inline operator fun get(c: Char) = entries[(c.code - '<'.code) / 2]
    }
}
enum class Category {
    X, M, A, S;

    companion object {
        val HASHED = arrayOf(X, A, S, M)
        inline operator fun get(c: Char) = HASHED[(c.code xor (c.code shr 1)) and 3]
    }
}
@JvmInline
value class Categories<T>(private val value: Array<T>) {
    val x get() = value[0]
    val m get() = value[1]
    val a get() = value[2]
    val s get() = value[3]
    operator fun get(category: Category) = value[category.ordinal]
    fun copy(category: Category, value: T): Categories<T> {
        val newValue = this.value.copyOf()
        newValue[category.ordinal] = value
        return Categories(newValue)
    }
    companion object {
        inline fun <reified T> of(x: T, m: T, a: T, s: T) = Categories(arrayOf(x, m, a, s))
    }
}
fun Categories<IntRange>.combinations() = x.count().toLong() * m.count() * a.count() * s.count()
data class Rule(val key: Category?, val predicateOp: Operator, val predicateValue: Int, val next: String) {
    fun matches(ratings: Categories<Int>): Boolean {
        if (key == null) return true
        return when (predicateOp) {
            Operator.LT -> ratings[key] < predicateValue
            Operator.GT -> ratings[key] > predicateValue
        }
    }

    override fun toString(): String {
        if (key == null) return next
        return "$key${"<>"[predicateOp.ordinal]}$predicateValue:$next"
    }
}
data class Workflow(val rules: List<Rule>) {
    override fun toString() = rules.joinToString(", ", "Workflow{", "}")
}

fun parse(input: PuzzleInput, parseRatings: Boolean = true): Pair<Map<String, Workflow>, List<Categories<Int>>> {
    val (workflowLines, ratingLines) = input.lines.splitOnEmpty(2)
    val workflows = HashMap.newHashMap<String, Workflow>(workflowLines.size)
    for (line in workflowLines) {
        val brace = line.indexOf('{')
        val name = line.substring(0, brace)
        val rulesSubstr = line.substring(brace + 1, line.length - 1)
        val rules = mutableListOf<Rule>()
        rulesSubstr.splitToRanges(',') { from, to ->
            rules.add(if (to != rulesSubstr.length) {
                val key = Category[rulesSubstr[from]]
                val op = Operator[rulesSubstr[from + 1]]
                val colon = rulesSubstr.indexOf(':', from)
                Rule(key, op, rulesSubstr.toInt(from + 2, colon), rulesSubstr.substring(colon + 1, to))
            } else {
                Rule(null, Operator.GT, 0, rulesSubstr.substring(from))
            })
        }
        workflows[name] = Workflow(rules)
    }
    val ratings = if (parseRatings) ratingLines.map {
        Categories(it.ints().toTypedArray())
    } else emptyList()
    return workflows to ratings
}

data class QueueEntry(val state: String, val ruleIndex: Int, val ranges: Categories<IntRange>)
inline fun allAccepted(workflows: Map<String, Workflow>): MutableList<Categories<IntRange>> {
    val unprocessed = ArrayDeque<QueueEntry>()
    unprocessed.add(QueueEntry("in", 0, Categories.of(1..4000, 1..4000, 1..4000, 1..4000)))
    val accepted = mutableListOf<Categories<IntRange>>()
    while (unprocessed.isNotEmpty()) {
        val (state, ruleIndex, ranges) = unprocessed.removeLast()
        val workflow = workflows[state]!!
        if (ruleIndex >= workflow.rules.size) continue
        val rule = workflow.rules[ruleIndex]
        val next = rule.next
        val key = rule.key
        if (key == null) {
            if (next == "A") accepted.add(ranges)
            else if (next != "R") unprocessed.add(QueueEntry(next, 0, ranges))
            continue
        }
        val range = ranges[key]
        val matching = if (rule.predicateOp == Operator.LT) range.first until rule.predicateValue else rule.predicateValue + 1..range.last
        val nextRanges = if (!matching.isEmpty()) ranges.copy(key, matching) else null
        if (nextRanges != null) {
            if (next == "A") accepted.add(nextRanges)
            else if (next != "R") unprocessed.add(QueueEntry(next, 0, nextRanges))
        }
        val nonMatching = if (rule.predicateOp == Operator.LT) rule.predicateValue..range.last else range.first..rule.predicateValue
        val otherRanges = if (!nonMatching.isEmpty()) ranges.copy(key, nonMatching) else null
        if (otherRanges != null) unprocessed.add(QueueEntry(state, ruleIndex + 1, otherRanges))
    }
    return accepted
}

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
    part1("Aplenty") {
        val (workflows, ratings) = parse(this)
        var result = 0
        for (rating in ratings) {
            var state = "in"
            while (state != "R" && state != "A") {
                val workflow = workflows[state]!!
                state = workflow.rules.first { it.matches(rating) }.next
            }
            if (state == "A") {
                result += rating.x + rating.m + rating.a + rating.s
            }
        }
        result
    }
    part2 {
        val (workflows, _) = parse(this, false)
        allAccepted(workflows).sumOf(Categories<IntRange>::combinations)
    }
}