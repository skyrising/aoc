package de.skyrising.aoc

import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap
import it.unimi.dsi.fastutil.objects.AbstractObject2CharMap
import it.unimi.dsi.fastutil.objects.Object2CharMap

val characters = mapDisplayToInts("""
 ██  ███   ██  ███  ████ ████  ██  █  █ ███    ██ █  █ █    █   ██   █ ██  ███   ██  ███   ███ ██████  █ █   ██   ██  █ █   █████
█  █ █  █ █  █ █  █ █    █    █  █ █  █  █      █ █ █  █    ██ ████  ██  █ █  █ █  █ █  █ █      █  █  █ █   ██   ██  █ █   █   █
█  █ ███  █    █  █ ███  ███  █    ████  █      █ ██   █    █ █ ██ █ ██  █ █  █ █  █ █  █ █      █  █  █  █ █ █   █ ██   █ █   █ 
████ █  █ █    █  █ █    █    █ ██ █  █  █      █ █ █  █    █   ██  ███  █ ███  █ ██ ███   ██    █  █  █  █ █ █ █ █ ██    █   █  
█  █ █  █ █  █ █  █ █    █    █  █ █  █  █   █  █ █ █  █    █   ██   ██  █ █    █  █ █ █     █   █  █  █   █  ██ ███  █   █  █   
█  █ ███   ██  ███  ████ █     ███ █  █ ███   ██  █  █ ████ █   ██   █ ██  █     ██ ██  █ ███    █   ██    █  █   ██  █   █  ████
""".trimIndent()).mapIndexed { idx, it -> it to "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[idx] }.associateByTo(Int2CharOpenHashMap(), { it.first }, { it.second })

fun mapDisplayToInts(display: String, litChar: Char = '█'): IntArray {
    val lines = display.lines().filter(String::isNotEmpty)
    val width = lines[0].length
    val height = lines.size
    if (height != 6) error("Invalid height")
    val length = (width + 1) / 5
    val result = IntArray(length)
    for (i in result.indices) {
        for (y in 0 until 6) {
            val line = lines[y]
            for (x in 0 until 5) {
                result[i] = result[i] shl 1
                if (i * 5 + x >= width) continue
                if (line[i * 5 + x] == litChar) {
                    result[i] = result[i] or 1
                }
            }
        }
    }
    return result
}

fun parseDisplay(display: String, litChar: Char = '█'): String {
    val ints = mapDisplayToInts(display, litChar)
    val sb = StringBuilder()
    for (i in ints) {
        sb.append(characters.getOrDefault(i, '?'))
    }
    return sb.toString()
}

abstract class Grid(val offset: Vec2i, val width: Int, val height: Int) {
    val size get() = Vec2i(width, height)

    protected fun index(x: Int, y: Int): Int {
        if (!contains(x, y)) throw IndexOutOfBoundsException("($x, $y) is not in [$offset,${offset + Vec2i(width, height)})")
        return (y - offset.y) * width + (x - offset.x)
    }

    fun localIndex(x: Int, y: Int): Int {
        if (x < 0 || y < 0 || x >= width || y >= height) throw IndexOutOfBoundsException("($x, $y) is not in [0, $width) x [0, $height)")
        return y * width + x
    }

    fun contains(x: Int, y: Int) = x >= offset.x && y >= offset.y && x < offset.x + width && y < offset.y + height
    operator fun contains(point: Vec2i) = contains(point.x, point.y)
}

class IntGrid(width: Int, height: Int, val data: IntArray, offset: Vec2i = Vec2i.ZERO) : Grid(offset, width, height) {
    operator fun get(point: Vec2i) = get(point.x, point.y)
    operator fun get(x: Int, y: Int) = data[index(x, y)]
    operator fun set(point: Vec2i, value: Int) = set(point.x, point.y, value)
    operator fun set(x: Int, y: Int, value: Int) {
        data[index(x, y)] = value
    }
    operator fun set(points: Iterable<Vec2i>, value: Int) {
        for (point in points) {
            this[point] = value
        }
    }

    inline fun forEach(action: (Int, Int, Int) -> Unit) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                action(x + offset.x, y + offset.y, data[y * width + x])
            }
        }
    }
}

class CharGrid(width: Int, height: Int, val data: CharArray, offset: Vec2i = Vec2i.ZERO) : Grid(offset, width, height), Sequence<Object2CharMap.Entry<Vec2i>> {
    operator fun get(point: Vec2i) = get(point.x, point.y)
    operator fun get(x: Int, y: Int) = data[index(x, y)]
    operator fun get(x: IntRange, y: Int) = String(data, index(x.first, y), x.last - x.first + 1)
    operator fun set(point: Vec2i, value: Char) = set(point.x, point.y, value)
    operator fun set(x: Int, y: Int, value: Char) {
        data[index(x, y)] = value
    }
    operator fun set(points: Iterable<Vec2i>, value: Char) {
        for (point in points) {
            this[point] = value
        }
    }

    override fun iterator() = iterator {
        forEach { x, y, c -> yield(AbstractObject2CharMap.BasicEntry(Vec2i(x, y), c)) }
    }

    val positions get() = sequence { forEachPosition { x, y -> yield(Vec2i(x, y)) } }

    inline fun forEachPosition(action: (Int, Int) -> Unit) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                action(x + offset.x, y + offset.y)
            }
        }
    }

    inline fun forEach(action: (Int, Int, Char) -> Unit) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                action(x + offset.x, y + offset.y, data[localIndex(x, y)])
            }
        }
    }

    inline fun where(predicate: (Char) -> Boolean): List<Vec2i> {
        val result = mutableListOf<Vec2i>()
        forEach { x, y, c -> if (predicate(c)) result.add(Vec2i(x, y)) }
        return result
    }

    inline fun indexOfFirst(predicate: (Char) -> Boolean): Vec2i {
        val index = data.indexOfFirst(predicate)
        return Vec2i(offset.x + index % width, offset.y + index / width)
    }
    inline fun count(predicate: (Char) -> Boolean) = data.count(predicate)

    override fun hashCode(): Int {
        var result = offset.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharGrid) return false
        if (width != other.width || height != other.height || offset != other.offset) return false
        return data.contentEquals(other.data)
    }

    override fun toString(): String {
        val sb = StringBuilder((width + 1) * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                sb.append(data[localIndex(x, y)])
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    fun translatedView(translation: Vec2i): CharGrid {
        return CharGrid(width, height, data, offset + translation)
    }

    fun subGrid(start: Vec2i, endExclusive: Vec2i): CharGrid {
        if (start.x < offset.x || start.y < offset.y || endExclusive.x > offset.x + width || endExclusive.y > offset.y + height) {
            throw IndexOutOfBoundsException("[$start, $endExclusive) is not in [$offset, ${offset + size})")
        }
        val width = endExclusive.x - start.x
        val height = endExclusive.y - start.y
        val data = CharArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                data[y * width + x] = this[x + start.x, y + start.y]
            }
        }
        return CharGrid(width, height, data, start)
    }

    data class FloodFillSpan(val x1: Int, val x2: Int, val y: Int, val dy: Int)

    inline fun floodFill(start: Vec2i, fill: Char, inside: (Int, Int) -> Boolean): Int {
        if (!inside(start.x, start.y)) return 0
        val s = ArrayDeque<FloodFillSpan>()
        s.add(FloodFillSpan(start.x, start.x, start.y, 1))
        s.add(FloodFillSpan(start.x, start.x, start.y - 1, -1))
        var count = 0
        while (s.isNotEmpty()) {
            var (x1, x2, y, dy) = s.removeFirst()
            if (y < offset.y || y >= offset.y + height) continue
            var x = x1
            while (x - 1 >= offset.x && inside(x - 1, y)) {
                this[x--, y] = fill
                count++
            }
            if (x < x1)
                s.add(FloodFillSpan(x, x1 - 1, y - dy, -dy))
            while (x1 <= x2) {
                while (x1 < offset.x + width && inside(x1, y)) {
                    this[x1++, y] = fill
                    count++
                }
                if (x1 > x) s.add(FloodFillSpan(x, x1 - 1, y + dy, dy))
                if (x1 - 1 > x2) s.add(FloodFillSpan(x2 + 1, x1 - 1, y - dy, -dy))
                x1++
                while (x1 <= x2 && !inside(x1, y)) x1++
                x = x1
            }
        }
        return count
    }

    fun floodFill(start: Vec2i, fill: Char): Int {
        val c = this[start]
        return floodFill(start, fill) { x, y -> this[x, y] == c }
    }

    fun getT(y: Int, x: Int) = this[x, y]
    fun setT(y: Int, x: Int, c: Char) { this[x, y] = c }

    companion object {
        fun parse(lines: List<String>): CharGrid {
            val width = lines.maxOf { it.length }
            val height = lines.size
            val grid = CharGrid(width, height, CharArray(width * height))
            for (row in 0 until height) {
                val line = lines[row]
                for (col in 0 until width) {
                    grid[col, row] = if (col < line.length) line[col] else ' '
                }
            }
            return grid
        }
    }
}

fun Collection<Vec2i>.charGrid(bg: Char = ' ', init: (Vec2i) -> Char): CharGrid {
    val grid = boundingBox().charGrid { bg }
    for (point in this) {
        grid[point] = init(point)
    }
    return grid
}