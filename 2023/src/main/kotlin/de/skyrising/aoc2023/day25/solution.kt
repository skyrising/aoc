package de.skyrising.aoc2023.day25

import de.skyrising.aoc.Graph
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("""
    jqt: rhn xhk nvd
    rsh: frs pzl lsr
    xhk: hfx
    cmg: qnr nvd lhk bvb
    rhn: xhk bvb hfx
    bvb: xhk hfx
    pzl: lsr hfx nvd
    qnr: nvd
    ntq: jqt hfx bvb xhk
    nvd: lhk
    lsr: lhk
    rzs: qnr cmg lsr rsh
    frs: qnr lhk lsr
""")

@PuzzleName("Snoverload")
fun PuzzleInput.part1(): Any {
    val g = Graph.build<String, Unit> {
        for (line in lines) {
            val (start, ends) = line.split(": ")
            val end = ends.split(" ")
            for (e in end) {
                if (start == "vfx" && e == "bgl") continue
                if (start == "bgl" && e == "vfx") continue
                if (start == "btp" && e == "qxr") continue
                if (start == "qxr" && e == "btp") continue
                if (start == "rxt" && e == "bqq") continue
                if (start == "bqq" && e == "rxt") continue
                edge(start, e, 1)
                edge(e, start, 1)
            }
        }
    }
    val g1 = g.getConnected(g.getVertexes().first())
    val g2size = g.getVertexes().size - g1.size
    return g1.size * g2size
}