package de.skyrising.aoc

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Vec2i(val x: Int, val y: Int): HasBoundingBox2i {
    override val boundingBox get() = BoundingBox2i(this, this)

    override fun toString() = "[$x, $y]"
    operator fun plus(other: Vec2i) = Vec2i(x + other.x, y + other.y)
    operator fun minus(other: Vec2i) = Vec2i(x - other.x, y - other.y)
    operator fun times(other: Vec2i) = Vec2i(x * other.x, y * other.y)
    operator fun times(other: Int) = Vec2i(x * other, y * other)
    operator fun div(other: Vec2i) = Vec2i(x / other.x, y / other.y)
    operator fun div(other: Int) = Vec2i(x / other, y / other)
    operator fun unaryMinus() = Vec2i(-x, -y)
    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException()
    }

    infix fun dot(other: Vec2i) = x * other.x + y * other.y

    fun manhattanDistance(v: Vec2i) = abs(x - v.x) + abs(y - v.y)

    val north get() = Vec2i(x, y - 1)
    val northEast get() = Vec2i(x + 1, y - 1)
    val east get() = Vec2i(x + 1, y)
    val southEast get() = Vec2i(x + 1, y + 1)
    val south get() = Vec2i(x, y + 1)
    val southWest get() = Vec2i(x - 1, y + 1)
    val west get() = Vec2i(x - 1, y)
    val northWest get() = Vec2i(x - 1, y - 1)

    fun fourNeighbors() = arrayOf(north, east, south, west)
    fun eightNeighbors() = arrayOf(north, northEast, east, southEast, south, southWest, west, northWest)

    fun withZ(z: Int) = Vec3i(x, y, z)

    infix fun lineTo(other: Vec2i) = Line2i(this, other)

    companion object {
        val ZERO = Vec2i(0, 0)
        val KNOWN = mapOf(
            "U" to Vec2i(0, -1),
            "R" to Vec2i(1, 0),
            "D" to Vec2i(0, 1),
            "L" to Vec2i(-1, 0),
            "N" to Vec2i(0, -1),
            "E" to Vec2i(1, 0),
            "S" to Vec2i(0, 1),
            "W" to Vec2i(-1, 0)
        )

        fun parse(input: String): Vec2i {
            val (x, y) = input.ints()
            return Vec2i(x, y)
        }
    }
}

fun min(a: Vec2i, b: Vec2i) = Vec2i(min(a.x, b.x), min(a.y, b.y))
fun max(a: Vec2i, b: Vec2i) = Vec2i(max(a.x, b.x), max(a.y, b.y))

data class BoundingBox2i(val min: Vec2i, val max: Vec2i): HasBoundingBox2i {
    init {
        if (min.x > max.x || min.y > max.y) throw IllegalArgumentException()
    }

    override val boundingBox get() = this
    val size get() = max - min

    inline fun charGrid(init: (Int) -> Char): CharGrid {
        val width = max.x - min.x + 1
        val height = max.y - min.y + 1
        return CharGrid(width, height, CharArray(width * height) { init(it) }, min)
    }

    fun expand(other: HasBoundingBox2i): BoundingBox2i {
        val box = other.boundingBox
        return BoundingBox2i(min(min, box.min), max(max, box.max))
    }
}

interface HasBoundingBox2i {
    val boundingBox: BoundingBox2i
}

fun Collection<HasBoundingBox2i>.boundingBox(): BoundingBox2i {
    var min: Vec2i? = null
    var max: Vec2i? = null
    for (v in this) {
        val box = v.boundingBox
        if (min == null || max == null) {
            min = box.min
            max = box.max
        } else {
            min = min(min, box.min)
            max = max(max, box.max)
        }
    }
    if (min == null || max == null) return BoundingBox2i(Vec2i.ZERO, Vec2i.ZERO)
    return BoundingBox2i(min, max)
}

data class Line2i(val from: Vec2i, val to: Vec2i) : Iterable<Vec2i>, HasBoundingBox2i {
    override val boundingBox get() = BoundingBox2i(min(from, to), max(from, to))

    override fun iterator(): Iterator<Vec2i> {
        val dx = abs(to.x - from.x)
        val dy = -abs(to.y - from.y)
        val sx = if (from.x < to.x) 1 else -1
        val sy = if (from.y < to.y) 1 else -1
        var err = dx + dy
        return object : Iterator<Vec2i> {
            var x = from.x
            var y = from.y
            var done = false
            override fun hasNext() = !done
            override fun next(): Vec2i {
                val cur = Vec2i(x, y)
                if (cur == to) {
                    done = true
                    return cur
                }
                val e2 = 2 * err
                if (e2 >= dy) {
                    err += dy
                    x += sx
                }
                if (e2 <= dx) {
                    err += dx
                    y += sy
                }
                return cur
            }
        }
    }

    override fun toString() = "$from -> $to"
}

data class Vec3i(val x: Int, val y: Int, val z: Int) : HasBoundingBox3i {
    override val boundingBox get() = BoundingBox3i(this, this)

    fun distanceSq(other: Vec3i) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z)
    override fun toString() = "[$x,$y,$z]"
    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException()
    }

    operator fun plus(v: Vec3i) = Vec3i(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vec3i) = Vec3i(x - v.x, y - v.y, z - v.z)
    operator fun times(v: Vec3i) = Vec3i(x * v.x, y * v.y, z * v.z)
    operator fun times(v: Int) = Vec3i(x * v, y * v, z * v)
    operator fun div(v: Vec3i) = Vec3i(x / v.x, y / v.y, z / v.z)
    operator fun div(v: Int) = Vec3i(x / v, y / v, z / v)
    operator fun unaryMinus() = Vec3i(-x, -y, -z)

    infix fun dot(v: Vec3i) = x * v.x + y * v.y + z * v.z

    fun manhattanDistance(v: Vec3i) = abs(x - v.x) + abs(y - v.y) + abs(z - v.z)

    fun xy() = Vec2i(x, y)
    fun xz() = Vec2i(x, z)
    fun yz() = Vec2i(y, z)
    fun sixNeighbors() = arrayOf(
        Vec3i(x - 1, y, z),
        Vec3i(x + 1, y, z),
        Vec3i(x, y - 1, z),
        Vec3i(x, y + 1, z),
        Vec3i(x, y, z - 1),
        Vec3i(x, y, z + 1)
    )

    companion object {
        val ZERO = Vec3i(0, 0, 0)

        fun parse(input: String): Vec3i {
            val (x, y, z) = input.ints()
            return Vec3i(x, y, z)
        }
    }
}

fun min(a: Vec3i, b: Vec3i) = Vec3i(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))
fun max(a: Vec3i, b: Vec3i) = Vec3i(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))

interface HasBoundingBox3i {
    val boundingBox: BoundingBox3i
}

fun Collection<HasBoundingBox3i>.boundingBox(): BoundingBox3i {
    var min: Vec3i? = null
    var max: Vec3i? = null
    for (v in this) {
        val box = v.boundingBox
        if (min == null || max == null) {
            min = box.min
            max = box.max
        } else {
            min = min(min, box.min)
            max = max(max, box.max)
        }
    }
    if (min == null || max == null) return BoundingBox3i(Vec3i.ZERO, Vec3i.ZERO)
    return BoundingBox3i(min, max)
}

data class BoundingBox3i(val min: Vec3i, val max: Vec3i): HasBoundingBox3i {
    init {
        if (min.x > max.x || min.y > max.y || min.z > max.z) throw IllegalArgumentException()
    }

    override val boundingBox get() = this
    val size get() = max - min

    fun expand(other: HasBoundingBox3i): BoundingBox3i {
        val box = other.boundingBox
        return BoundingBox3i(min(min, box.min), max(max, box.max))
    }

    fun expand(amount: Int) = BoundingBox3i(min - Vec3i(amount, amount, amount), max + Vec3i(amount, amount, amount))
    operator fun contains(point: Vec3i) = point.x in min.x..max.x && point.y in min.y..max.y && point.z in min.z..max.z
}

operator fun Int.times(other: Vec2i) = other * this
operator fun Int.times(other: Vec3i) = other * this