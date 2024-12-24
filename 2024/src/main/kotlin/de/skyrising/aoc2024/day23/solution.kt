package de.skyrising.aoc2024.day23

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.split2
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import java.util.function.IntFunction

val test = TestInput("""
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
""")

private const val SHIFT = 8

private fun makeGraph(lines: List<String>): Int2ObjectMap<IntSet> {
    val g = Int2ObjectOpenHashMap<IntSet>()
    for (line in lines) {
        val (a, b) = line.split2('-')!!
        val ai = (a[0].code shl SHIFT) or a[1].code
        val bi = (b[0].code shl SHIFT) or b[1].code
        g.computeIfAbsent(ai, IntFunction { IntOpenHashSet(13) }).add(bi)
        g.computeIfAbsent(bi, IntFunction { IntOpenHashSet(13) }).add(ai)
    }
    return g
}

private fun makeTriple(a: Int, b: Int, c: Int): Long {
    val n1 = minOf(a, b, c)
    val n3 = maxOf(a, b, c)
    val n2 = if (a != n1 && a != n3) a else if (b != n1 && b != n3) b else c
    return (n1.toLong() shl (SHIFT * 4)) or (n2.toLong() shl (SHIFT * 2)) or n3.toLong()
}

@PuzzleName("LAN Party")
fun PuzzleInput.part1(): Any {
    val g = makeGraph(lines)
    val triples = LongOpenHashSet()
    for ((v, vInc) in g) {
        if (v ushr SHIFT != 't'.code) continue
        for (a in vInc.intIterator()) {
            for (b in g[a]!!.intIterator()) {
                if (b != v && b in vInc) triples.add(makeTriple(v, a, b))
            }
        }
    }
    return triples.size
}

fun PuzzleInput.part2(): Any {
    val g = makeGraph(lines)
    var biggestLan = IntSet.of()
    for ((v, vInc) in g) {
        val lan = IntOpenHashSet()
        lan.add(v)
        for (a in vInc.intIterator()) {
            if (a !in lan && g[a]!!.containsAll(lan)) {
                lan.add(a)
            }
        }
        if (lan.size > biggestLan.size) biggestLan = lan
    }
    return biggestLan.sorted().joinToString(",") { "${(it ushr SHIFT).toChar()}${(it and ((1 shl SHIFT) - 1)).toChar()}" }
}
