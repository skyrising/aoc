package de.skyrising.aoc2023.day17

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 17)

data class Node(val posX: Int, val posY: Int, val dir: Direction, val dirCount: Int)

fun IntGrid.getOutgoing(v: Vertex<Node>): Set<Edge<Node, Unit?>> {
    val edges = mutableSetOf<Edge<Node, Unit?>>()
    val node = v.value
    for (i in 0..3) {
        val dir = Direction(i)
        if (dir == -node.dir) continue
        if (node.dirCount >= 3 && dir == node.dir) continue
        val posX = node.posX + dir.x
        val posY = node.posY + dir.y
        if (!contains(posX, posY)) continue
        val loss = this[posX, posY]
        edges.add(Edge(v, Vertex(Node(posX, posY, dir, if (dir == node.dir) node.dirCount + 1 else 1)), loss, Unit))
    }
    return edges
}

fun IntGrid.getOutgoing2(v: Vertex<Node>): Set<Edge<Node, Unit?>> {
    val edges = mutableSetOf<Edge<Node, Unit?>>()
    val node = v.value
    for (i in 0..3) {
        val dir = Direction(i)
        if (dir == -node.dir) continue
        if (node.dirCount in 1..3 && dir != node.dir) continue
        if (node.dirCount >= 10 && dir == node.dir) continue
        val posX = node.posX + dir.x
        val posY = node.posY + dir.y
        if (!contains(posX, posY)) continue
        val loss = this[posX, posY]
        edges.add(Edge(v, Vertex(Node(posX, posY, dir, if (dir == node.dir) node.dirCount + 1 else 1)), loss, Unit))
    }
    return edges
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """)
    fun PuzzleInput.run(out: IntGrid.(Vertex<Node>) -> Set<Edge<Node, Unit?>>, isEnd: (Vertex<Node>,Vec2i)->Boolean): Int {
        val cg = charGrid
        val grid = IntGrid(cg.width, cg.height, IntArray(cg.width * cg.height) {
            cg.data[it].digitToInt()
        })
        val end = Vec2i(cg.width - 1, cg.height - 1)
        val path = dijkstra(Vertex(Node(0, 0, Direction.E, 0)), { isEnd(it, end) }, { grid.out(it) })!!
        return path.sumOf { it.weight }
    }
    part1("Clumsy Crucible") {
        run(IntGrid::getOutgoing) { v, end -> v.value.posX == end.x && v.value.posY == end.y }
    }
    part2 {
        run(IntGrid::getOutgoing2) { v, end -> v.value.posX == end.x && v.value.posY == end.y && v.value.dirCount >= 4 }
    }
}