package de.skyrising.aoc

import it.unimi.dsi.fastutil.longs.LongList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@JvmInline
value class Direction(val ordinal: Int) {
    val x inline get() = if (ordinal and 1 != 0) 2 - ordinal else 0
    val y inline get() = if (ordinal and 1 == 0) ordinal - 1 else 0
    val vec inline get() = Vec2i(x, y)
    val vecL inline get() = Vec2l(x.toLong(), y.toLong())

    fun rotateCW() = Direction((ordinal + 1) and 3)
    fun rotateCCW() = Direction((ordinal + 3) and 3)
    operator fun unaryMinus() = Direction((ordinal + 2) and 3)
    operator fun inc() = rotateCW()
    operator fun dec() = rotateCCW()
    operator fun times(distance: Int) = Vec2i(x * distance, y * distance)
    operator fun times(distance: Long) = Vec2l(x * distance, y * distance)

    override fun toString() = "NESW"[ordinal].toString()

    companion object {
        val N = Direction(0)
        val E = Direction(1)
        val S = Direction(2)
        val W = Direction(3)
        val values = arrayOf(N, E, S, W)
    }
}

@JvmInline
value class Vec2i(val longValue: Long): HasBoundingBox2i {
    constructor(x: Int, y: Int): this(packToLong(x, y))
    val x inline get() = unpackFirstInt(longValue)
    val y inline get() = unpackSecondInt(longValue)
    override val boundingBox get() = BoundingBox2i(this, this)

    inline operator fun component1() = x
    inline operator fun component2() = y

    override fun toString() = "[$x, $y]"
    inline operator fun plus(other: Vec2i) = Vec2i(x + other.x, y + other.y)
    inline operator fun plus(offset: Int) = Vec2i(x + offset, y + offset)
    inline operator fun plus(direction: Direction) = Vec2i(x + direction.x, y + direction.y)
    inline operator fun minus(other: Vec2i) = Vec2i(x - other.x, y - other.y)
    inline operator fun minus(offset: Int) = Vec2i(x - offset, y - offset)
    inline operator fun minus(direction: Direction) = Vec2i(x - direction.x, y - direction.y)
    inline operator fun times(other: Vec2i) = Vec2i(x * other.x, y * other.y)
    inline operator fun times(other: Int) = Vec2i(x * other, y * other)
    inline operator fun div(other: Vec2i) = Vec2i(x / other.x, y / other.y)
    inline operator fun div(other: Int) = Vec2i(x / other, y / other)
    inline operator fun unaryMinus() = Vec2i(-x, -y)
    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException()
    }
    operator fun rem(other: Vec2i) = Vec2i(x % other.x, y % other.y)
    operator fun rem(other: Int) = Vec2i(x % other, y % other)
    infix fun mod(other: Vec2i) = Vec2i(x.mod(other.x), y.mod(other.y))
    infix fun mod(other: Int) = Vec2i(x.mod(other), y.mod(other))

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
    val dir inline get() = if (x == 0) if (y < 0) Direction.N else Direction.S else if (x < 0) Direction.W else Direction.E

    fun fourNeighbors() = arrayOf(north, east, south, west)
    fun fiveNeighbors() = arrayOf(north, east, south, west, this)
    fun eightNeighbors() = arrayOf(north, northEast, east, southEast, south, southWest, west, northWest)

    fun withZ(z: Int) = Vec3i(x, y, z)

    infix fun lineTo(other: Vec2i) = Line2i(this, other)

    fun toDouble() = Vec2d(x.toDouble(), y.toDouble())

    fun ray(direction: Vec2i, length: Int) = (0..<length).map { this + direction * it }

    companion object {
        val ZERO = Vec2i(0, 0)
        val N = Vec2i(0, -1)
        val E = Vec2i(1, 0)
        val S = Vec2i(0, 1)
        val W = Vec2i(-1, 0)
        val NE = Vec2i(1, -1)
        val SE = Vec2i(1, 1)
        val SW = Vec2i(-1, 1)
        val NW = Vec2i(-1, -1)
        val KNOWN = mapOf(
            "U" to N,
            "R" to E,
            "D" to S,
            "L" to W,
            "N" to N,
            "E" to E,
            "S" to S,
            "W" to W
        )

        fun parse(input: String): Vec2i {
            val (x, y) = input.ints()
            return Vec2i(x, y)
        }
    }
}

operator fun Int.times(other: Vec2i) = other * this

data class Vec2l(val x: Long, val y: Long) {
    override fun toString() = "[$x, $y]"
    inline operator fun plus(other: Vec2l) = Vec2l(x + other.x, y + other.y)
    inline operator fun plus(offset: Int) = Vec2l(x + offset, y + offset)
    inline operator fun plus(offset: Long) = Vec2l(x + offset, y + offset)
    inline operator fun plus(direction: Direction) = Vec2l(x + direction.x, y + direction.y)
    inline operator fun minus(other: Vec2l) = Vec2l(x - other.x, y - other.y)
    inline operator fun minus(offset: Int) = Vec2l(x - offset, y - offset)
    inline operator fun minus(offset: Long) = Vec2l(x - offset, y - offset)
    inline operator fun minus(direction: Direction) = Vec2l(x - direction.x, y - direction.y)
    inline operator fun times(other: Vec2l) = Vec2l(x * other.x, y * other.y)
    inline operator fun times(other: Int) = Vec2l(x * other, y * other)
    inline operator fun times(other: Long) = Vec2l(x * other, y * other)
    inline operator fun div(other: Vec2l) = Vec2l(x / other.x, y / other.y)
    inline operator fun div(other: Int) = Vec2l(x / other, y / other)
    inline operator fun div(other: Long) = Vec2l(x / other, y / other)
    inline operator fun unaryMinus() = Vec2l(-x, -y)
    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException()
    }
    operator fun rem(other: Vec2l) = Vec2l(x % other.x, y % other.y)
    operator fun rem(other: Int) = Vec2l(x % other, y % other)

    infix fun dot(other: Vec2l) = x * other.x + y * other.y

    fun manhattanDistance(v: Vec2l) = abs(x - v.x) + abs(y - v.y)

    val north get() = Vec2l(x, y - 1)
    val northEast get() = Vec2l(x + 1, y - 1)
    val east get() = Vec2l(x + 1, y)
    val southEast get() = Vec2l(x + 1, y + 1)
    val south get() = Vec2l(x, y + 1)
    val southWest get() = Vec2l(x - 1, y + 1)
    val west get() = Vec2l(x - 1, y)
    val northWest get() = Vec2l(x - 1, y - 1)

    fun fourNeighbors() = arrayOf(north, east, south, west)
    fun fiveNeighbors() = arrayOf(north, east, south, west, this)
    fun eightNeighbors() = arrayOf(north, northEast, east, southEast, south, southWest, west, northWest)

    fun toDouble() = Vec2d(x.toDouble(), y.toDouble())

    companion object {
        val ZERO = Vec2l(0, 0)
        val N = Vec2l(0, -1)
        val E = Vec2l(1, 0)
        val S = Vec2l(0, 1)
        val W = Vec2l(-1, 0)
        val KNOWN = mapOf(
            "U" to N,
            "R" to E,
            "D" to S,
            "L" to W,
            "N" to N,
            "E" to E,
            "S" to S,
            "W" to W
        )

        fun parse(input: String): Vec2l {
            val (x, y) = input.longs()
            return Vec2l(x, y)
        }
    }
}

operator fun Long.times(other: Vec2l) = other * this

data class Vec2d(val x: Double, val y: Double) {
    override fun toString() = "[$x, $y]"
    inline operator fun plus(other: Vec2d) = Vec2d(x + other.x, y + other.y)
    inline operator fun plus(offset: Double) = Vec2d(x + offset, y + offset)
    inline operator fun minus(other: Vec2d) = Vec2d(x - other.x, y - other.y)
    inline operator fun minus(offset: Double) = Vec2d(x - offset, y - offset)
    inline operator fun times(other: Vec2d) = Vec2d(x * other.x, y * other.y)
    inline operator fun times(other: Double) = Vec2d(x * other, y * other)
    inline operator fun div(other: Vec2d) = Vec2d(x / other.x, y / other.y)
    inline operator fun div(other: Double) = Vec2d(x / other, y / other)
    inline operator fun unaryMinus() = Vec2d(-x, -y)
    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException()
    }
    operator fun rem(other: Vec2d) = Vec2d(x % other.x, y % other.y)
    operator fun rem(other: Double) = Vec2d(x % other, y % other)

    infix fun dot(other: Vec2d) = x * other.x + y * other.y

    fun manhattanDistance(v: Vec2d) = abs(x - v.x) + abs(y - v.y)
}

fun min(a: Vec2i, b: Vec2i) = Vec2i(min(a.x, b.x), min(a.y, b.y))
fun max(a: Vec2i, b: Vec2i) = Vec2i(max(a.x, b.x), max(a.y, b.y))

data class BoundingBox2i(val min: Vec2i, val max: Vec2i): HasBoundingBox2i {
    init {
        if (min.x > max.x || min.y > max.y) throw IllegalArgumentException()
    }

    override val boundingBox get() = this
    val size get() = max - min
    val area get() = (max.x - min.x + 1) * (max.y - min.y + 1)

    inline fun charGrid(init: (Int) -> Char): CharGrid {
        val width = max.x - min.x + 1
        val height = max.y - min.y + 1
        return CharGrid(width, height, CharArray(width * height) { init(it) }, min.x, min.y)
    }

    fun expand(other: HasBoundingBox2i): BoundingBox2i {
        val box = other.boundingBox
        return BoundingBox2i(min(min, box.min), max(max, box.max))
    }

    fun expand(amount: Int) = BoundingBox2i(min - Vec2i(amount, amount), max + Vec2i(amount, amount))
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
    fun reverse() = Line2i(to, from)
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
    operator fun plus(offset: Int) = Vec3i(x + offset, y + offset, z + offset)
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

operator fun Int.times(other: Vec3i) = other * this
fun min(a: Vec3i, b: Vec3i) = Vec3i(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))
fun max(a: Vec3i, b: Vec3i) = Vec3i(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))

data class Vec3l(val x: Long, val y: Long, val z: Long) {

    fun distanceSq(other: Vec3l) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z)
    override fun toString() = "[$x,$y,$z]"
    operator fun get(i: Int) = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException()
    }

    fun toList(): LongList = LongList.of(x, y, z)

    operator fun plus(v: Vec3l) = Vec3l(x + v.x, y + v.y, z + v.z)
    operator fun plus(offset: Long) = Vec3l(x + offset, y + offset, z + offset)
    operator fun minus(v: Vec3l) = Vec3l(x - v.x, y - v.y, z - v.z)
    operator fun times(v: Vec3l) = Vec3l(x * v.x, y * v.y, z * v.z)
    operator fun times(v: Long) = Vec3l(x * v, y * v, z * v)
    operator fun div(v: Vec3l) = Vec3l(x / v.x, y / v.y, z / v.z)
    operator fun div(v: Long) = Vec3l(x / v, y / v, z / v)
    operator fun unaryMinus() = Vec3l(-x, -y, -z)

    infix fun dot(v: Vec3l) = x * v.x + y * v.y + z * v.z

    fun manhattanDistance(v: Vec3l) = abs(x - v.x) + abs(y - v.y) + abs(z - v.z)

    fun xy() = Vec2l(x, y)
    fun xz() = Vec2l(x, z)
    fun yz() = Vec2l(y, z)
    fun sixNeighbors() = arrayOf(
        Vec3l(x - 1, y, z),
        Vec3l(x + 1, y, z),
        Vec3l(x, y - 1, z),
        Vec3l(x, y + 1, z),
        Vec3l(x, y, z - 1),
        Vec3l(x, y, z + 1)
    )

    companion object {
        val ZERO = Vec3l(0, 0, 0)

        fun parse(input: String): Vec3l {
            val (x, y, z) = input.longs()
            return Vec3l(x, y, z)
        }
    }
}

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
    val size get() = max - min + 1

    fun expand(other: HasBoundingBox3i): BoundingBox3i {
        val box = other.boundingBox
        return BoundingBox3i(min(min, box.min), max(max, box.max))
    }

    fun expand(amount: Int) = BoundingBox3i(min - Vec3i(amount, amount, amount), max + Vec3i(amount, amount, amount))
    operator fun contains(point: Vec3i) = point.x in min.x..max.x && point.y in min.y..max.y && point.z in min.z..max.z
    fun intersects(other: BoundingBox3i): Boolean {
        if (min.x > other.max.x || max.x < other.min.x) return false
        if (min.y > other.max.y || max.y < other.min.y) return false
        if (min.z > other.max.z || max.z < other.min.z) return false
        return true
    }
}

data class Cube(var x: Int, var y: Int, var z: Int, var dx: Int, var dy: Int, var dz: Int) : HasBoundingBox3i {
    fun intersects(other: Cube): Boolean {
        if (x >= other.x + other.dx || x + dx <= other.x) return false
        if (y >= other.y + other.dy || y + dy <= other.y) return false
        if (z >= other.z + other.dz || z + dz <= other.z) return false
        return true
    }

    constructor(bbox: BoundingBox3i) : this(bbox.min.x, bbox.min.y, bbox.min.z, bbox.size.x, bbox.size.y, bbox.size.z)
    val maxZ inline get() = z + dz - 1
    val maxY inline get() = y + dy - 1
    val maxX inline get() = x + dx - 1
    override val boundingBox get() = BoundingBox3i(Vec3i(x, y, z), Vec3i(maxX, maxY, maxZ))

    override fun toString() = "Cube($x,$y,$z~$maxX,$maxY,$maxZ)"
}
