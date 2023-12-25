package de.skyrising.aoc2023.day24

import com.microsoft.z3.Context
import com.microsoft.z3.Status
import de.skyrising.aoc.*

val test = TestInput("""
    19, 13, 30 @ -2,  1, -2
    18, 19, 22 @ -1, -1, -2
    20, 25, 34 @ -2, -2, -4
    12, 31, 28 @ -1, -2, -1
    20, 19, 15 @  1, -5, -3
""")

fun intersectRays(p1: Vec2l, v1: Vec2l, p2: Vec2l, v2: Vec2l): Vec2d? {
    val dx = p2.x - p1.x
    val dy = p2.y - p1.y
    val det = v2.x * v1.y - v2.y * v1.x
    if (det == 0L) return null
    val t1 = (dy * v2.x - dx * v2.y) / det.toDouble()
    val t2 = (dy * v1.x - dx * v1.y) / det.toDouble()
    if (t1 < 0 || t2 < 0) return null
    return Vec2d(t1, t2)
}

fun parse(input: PuzzleInput) = input.lines.map { val (x, y, z, vx, vy, vz) = it.longs(); Vec3l(x, y, z) to Vec3l(vx, vy, vz) }

@PuzzleName("Never Tell Me The Odds")
fun PuzzleInput.part1(): Any {
    val hailstones = parse(this)
    val area = 200000000000000.0..400000000000000.0 //7.0..27.0
    return hailstones.unorderedPairs().count { (a, b) ->
        val (p1, v1) = a
        val (p2, v2) = b
        val t = intersectRays(p1.xy(), v1.xy(), p2.xy(), v2.xy()) ?: return@count false
        val p = p1.xy().toDouble() + v1.xy().toDouble() * t.x
        //log("${a.first} @ ${a.second} ${b.first} @ ${b.second} t=$t $p")
        (p.x in area && p.y in area)
    }
}

fun PuzzleInput.part2(): Any {
    val hailstones = parse(this)
    val (rp, rv) = Context().run {
        val solver = mkSolver()
        val (x, y, z, vx, vy, vz) = mkRealConst("x", "y", "z", "vx", "vy", "vz")
        for (i in 0..2) {
            val (xi, yi, zi) = mkReal(hailstones[i].first)
            val (vxi, vyi, vzi) = mkReal(hailstones[i].second)
            val ti = mkRealConst("t$i")
            solver += ti gt 0
            solver += (xi + vxi * ti) eq (x + vx * ti)
            solver += (yi + vyi * ti) eq (y + vy * ti)
            solver += (zi + vzi * ti) eq (z + vz * ti)
        }
        if (solver.check() != Status.SATISFIABLE) error("Not satisfiable")
        val m = solver.model
        Vec3l(m[x].toLong(), m[y].toLong(), m[z].toLong()) to Vec3l(m[vx].toLong(), m[vy].toLong(), m[vz].toLong())
    }
    // val durations = hailstones.mapNotNull { (p, v) ->
    //     intersectRays(rp.xy(), rv.xy(), p.xy(), v.xy())?.x?.toDuration(DurationUnit.NANOSECONDS)
    // }.sorted()
    // log(durations)
    return rp.x + rp.y + rp.z
}
