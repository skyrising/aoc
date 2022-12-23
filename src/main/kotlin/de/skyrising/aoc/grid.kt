package de.skyrising.aoc

import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap

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

class CharGrid(width: Int, height: Int, val data: CharArray, offset: Vec2i = Vec2i.ZERO) : Grid(offset, width, height) {
    operator fun get(point: Vec2i) = get(point.x, point.y)
    operator fun get(x: Int, y: Int) = data[index(x, y)]
    operator fun set(point: Vec2i, value: Char) = set(point.x, point.y, value)
    operator fun set(x: Int, y: Int, value: Char) {
        data[index(x, y)] = value
    }
    operator fun set(points: Iterable<Vec2i>, value: Char) {
        for (point in points) {
            this[point] = value
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
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (predicate(data[localIndex(x, y)])) {
                    result.add(Vec2i(x + offset.x, y + offset.y))
                }
            }
        }
        return result
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
}