package de.skyrising.aoc

import kotlin.math.abs

data class Vec2i(val x: Int, val y: Int) {
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

    fun withZ(z: Int) = Vec3i(x, y, z)

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
    }
}

data class Vec3i(val x: Int, val y: Int, val z: Int) {
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

    companion object {
        val ZERO = Vec3i(0, 0, 0)
    }
}

operator fun Int.times(other: Vec2i) = other * this
operator fun Int.times(other: Vec3i) = other * this