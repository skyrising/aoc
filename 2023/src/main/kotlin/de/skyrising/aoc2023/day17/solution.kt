package de.skyrising.aoc2023.day17

import de.skyrising.aoc.*
import kotlin.math.absoluteValue

data class Node(val posX: Int, val posY: Int, val dir: Direction, val dirCount: Int)

private inline fun IntGrid.getOutgoing(node: Node): Collection<Edge<Node, Unit?>> {
    val edges = ArrayList<Edge<Node, Unit?>>(3)
    for (i in 0..3) {
        val dir = Direction(i)
        if (dir == -node.dir) continue
        if (node.dirCount >= 3 && dir == node.dir) continue
        val posX = node.posX + dir.x
        val posY = node.posY + dir.y
        if (!contains(posX, posY)) continue
        val loss = this[posX, posY]
        edges.add(Edge(node, Node(posX, posY, dir, if (dir == node.dir) node.dirCount + 1 else 1), loss, Unit))
    }
    return edges
}

private inline fun IntGrid.getOutgoing2(node: Node): Collection<Edge<Node, Unit?>> {
    val edges = ArrayList<Edge<Node, Unit?>>(3)
    for (i in 0..3) {
        val dir = Direction(i)
        if (dir == -node.dir) continue
        if (node.dirCount in 1..3 && dir != node.dir) continue
        if (node.dirCount >= 10 && dir == node.dir) continue
        var step = 1
        if (node.dir == dir && node.dirCount in 1..3)
            step = 4 - node.dirCount
        else if (dir != node.dir)
            step = 4
        while (!contains(node.posX + dir.x * step, node.posY + dir.y * step)) step--
        if (step <= 0) continue
        var posX = node.posX
        var posY = node.posY
        var loss = 0
        repeat(step) {
            posX += dir.x
            posY += dir.y
            loss += this[posX, posY]
        }
        edges.add(Edge(node, Node(posX, posY, dir, if (dir == node.dir) node.dirCount + step else step), loss, Unit))
    }
    return edges
}

private const val ASTAR = false

private inline fun PuzzleInput.run(out: IntGrid.(Node) -> Collection<Edge<Node, Unit?>>, isEnd: (Node)->Boolean): Int {
    val cg = charGrid
    val grid = IntGrid(cg.width, cg.height, IntArray(cg.width * cg.height) {
        cg.data[it].digitToInt()
    })
    val start = Node(0, 0, Direction.E, 0)
    val end = Vec2i(cg.width - 1, cg.height - 1)
    val path = if(ASTAR) {
        astar(start,
            { (end.x - it.posX).absoluteValue + (end.y - it.posY).absoluteValue },
            { it.posX == end.x && it.posY == end.x && isEnd(it) },
            { grid.out(it) }
        )!!
    } else {
        dijkstra(start,
            { it.posX == end.x && it.posY == end.x && isEnd(it) },
            { grid.out(it) }
        )!!
    }
    return path.sumOf { it.weight }
}

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

@PuzzleName("Clumsy Crucible")
fun PuzzleInput.part1() = run(IntGrid::getOutgoing) { true }
fun PuzzleInput.part2() = run(IntGrid::getOutgoing2) { it.dirCount >= 4 }
