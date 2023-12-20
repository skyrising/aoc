package de.skyrising.aoc.visualization.graphviz

import de.skyrising.aoc.visualization.parseSimpleSVG
import java.io.ByteArrayInputStream

fun renderGraph(dot: String, format: String = "plain"): ByteArray {
    val process = ProcessBuilder("dot", "-T$format").start()
    process.outputStream.use { it.write(dot.toByteArray()) }
    process.outputStream.close()
    return process.inputStream.readAllBytes()
}

fun renderGraphToSVG(dot: String) = parseSimpleSVG(ByteArrayInputStream(renderGraph(dot, "svg")))