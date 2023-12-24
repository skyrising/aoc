package de.skyrising.aoc2023.day24

import com.microsoft.z3.*
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
        operator fun <T: ArithSort> ArithExpr<T>.plus(other: ArithExpr<T>) = mkAdd(this, other)
        operator fun <T: ArithSort> ArithExpr<T>.times(other: ArithExpr<T>) = mkMul(this, other)
        infix fun <T: ArithSort> ArithExpr<T>.eq(other: ArithExpr<T>) = mkEq(this, other)
        operator fun Solver.plusAssign(constraint: Expr<BoolSort>) = add(constraint)
        val solver = mkSolver()
        val (x, y, z, vx, vy, vz) = listOf("x", "y", "z", "vx", "vy", "vz").map { mkRealConst(it) }
        for (i in 0..2) {
            val (xi, yi, zi) = hailstones[i].first.toList().map { mkReal(it) }
            val (vxi, vyi, vzi) = hailstones[i].second.toList().map { mkReal(it) }
            val ti = mkRealConst("t$i")
            solver += (xi + vxi * ti) eq (x + vx * ti)
            solver += (yi + vyi * ti) eq (y + vy * ti)
            solver += (zi + vzi * ti) eq (z + vz * ti)
        }
        if (solver.check() != Status.SATISFIABLE) error("Not satisfiable")
        val m = solver.model
        // log(m)
        fun RatNum.toLong() = if (denominator.int64 != 1L) throw IllegalArgumentException() else numerator.int64
        fun get(expr: Expr<*>) = (m.getConstInterp(expr) as RatNum).toLong()
        Vec3l(get(x), get(y), get(z)) to Vec3l(get(vx), get(vy), get(vz))
    }
    // val durations = hailstones.mapNotNull { (p, v) ->
    //     intersectRays(rp.xy(), rv.xy(), p.xy(), v.xy())?.x?.toDuration(DurationUnit.NANOSECONDS)
    // }.sorted()
    // log(durations)
    return rp.x + rp.y + rp.z
}
