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

class IntGrid(val width: Int, val height: Int, private val data: IntArray) {
    operator fun get(point: Vec2i) = get(point.x, point.y)
    operator fun get(x: Int, y: Int) = data[y * width + x]
    operator fun set(x: Int, y: Int, value: Int) {
        data[y * width + x] = value
    }

    inline fun forEach(action: (Int, Int, Int) -> Unit) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                action(x, y, this[x, y])
            }
        }
    }

    fun contains(x: Int, y: Int) = x in 0 until width && y in 0 until height
    fun contains(point: Vec2i) = contains(point.x, point.y)
}