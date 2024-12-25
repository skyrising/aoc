@file:PuzzleName("Monster Messages")

package de.skyrising.aoc2020.day19

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import kotlin.collections.List
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.set

private interface PreRule {
    operator fun invoke(rules: (Int) -> String): String
}

private fun readPreRules(lines: List<String>): Triple<Int, Int2ObjectMap<String>, (Int) -> String> {
    val rules = Int2ObjectOpenHashMap<PreRule>()
    for (line in lines) {
        if (line.isEmpty()) break
        val colon = line.indexOf(':')
        val id = line.substring(0, colon).toInt()
        val line1 = line.substring(colon + 2)
        if (line1[0] == '"') {
            val str = line1.substring(1, line1.length - 1)
            rules[id] = object : PreRule {
                override fun invoke(rules: (Int) -> String) = Regex.fromLiteral(str).pattern
            }
        } else {
            val groups = line1.split(" | ").map { it.split(" ").map(String::toInt) }
            rules[id] = object : PreRule {
                override fun invoke(rules: (Int) -> String): String {
                    return groups.joinToString("|") {
                        it.joinToString("", "(?:", ")") { id ->
                            val rule = rules(id)
                            if ('|' !in rule) rule else "(?:$rule)"
                        }
                    }
                }
            }
        }
    }

    val cache = Int2ObjectOpenHashMap<String>()
    fun buildRule(id: Int): String {
        var regex = cache[id]
        if (regex != null) return regex
        regex = rules[id](::buildRule)
        cache[id] = regex
        return regex
    }
    return Triple(rules.size, cache, ::buildRule)
}

val test = TestInput("""
    0: 4 1 5
    1: 2 3 | 3 2
    2: 4 4 | 5 5
    3: 4 5 | 5 4
    4: "a"
    5: "b"

    ababbb
    bababa
    abbbab
    aaabbb
    aaaabbb
""")

val test2 = TestInput("""
    42: 9 14 | 10 1
    9: 14 27 | 1 26
    10: 23 14 | 28 1
    1: "a"
    11: 42 31
    5: 1 14 | 15 1
    19: 14 1 | 14 14
    12: 24 14 | 19 1
    16: 15 1 | 14 14
    31: 14 17 | 1 13
    6: 14 14 | 1 14
    2: 1 24 | 14 4
    0: 8 11
    13: 14 3 | 1 12
    15: 1 | 14
    17: 14 2 | 1 7
    23: 25 1 | 22 14
    28: 16 1
    4: 1 1
    20: 14 14 | 1 15
    3: 5 14 | 16 1
    27: 1 6 | 14 18
    14: "b"
    21: 14 1 | 1 14
    25: 1 1 | 1 14
    22: 14 14
    8: 42
    26: 14 22 | 1 20
    18: 15 15
    7: 14 5 | 1 21
    24: 14 1

    abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
    bbabbbbaabaabba
    babbbbaabbbbbabbbbbbaabaaabaaa
    aaabbbbbbaaaabaababaabababbabaaabbababababaaa
    bbbbbbbaaaabbbbaaabbabaaa
    bbbababbbbaaaaaaaabbababaaababaabab
    ababaaaaaabaaab
    ababaaaaabbbaba
    baabbaaaabbaaaababbaababb
    abbbbabbbbaaaababbbbbbaaaababb
    aaaaabbaabaaaaababaa
    aaaabbaaaabbaaa
    aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
    babaaabbbaaabaababbaabababaaab
    aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
""")

fun PuzzleInput.part1(): Any {
    val (rulesCount, _, buildRule) = readPreRules(lines)
    val rule0 = Regex(buildRule(0))
    var count = 0
    for (i in rulesCount + 1 until lines.size) {
        val message = lines[i]
        if (rule0.matches(message)) count++
    }
    return count
}

fun PuzzleInput.part2(): Any {
    // 0: 8 11
    // 8: 42 | 42 8 -> 42+
    // 11: 42 31 | 42 11 31 -> (42(?-1)?31)
    // https://regex101.com/r/9CrEQm/1
    val (_, cache, buildRule) = readPreRules(lines)
    val rule31 = buildRule(31)
    val rule42 = buildRule(42)
    cache[8] = "(?:$rule42)+"
    cache[11] = "((?:$rule42)(?-1)?(?:$rule31))"
    return "^${buildRule(0)}$"
}
