package de.skyrising.aoc2022.day22

import de.skyrising.aoc.*

private fun parseInput(input: PuzzleInput): Pair<CharGrid, List<String>> {
    val grid = CharGrid.parse(input.lines.subList(0, input.lines.size - 2))
    val path = Regex("\\d+|R|L").findAll(input.lines.last()).map { it.value }.toList()
    return grid to path
}

private fun nextTile(grid: CharGrid, state: VecState, transitions: Map<VecState, VecState>? = null): VecState {
    var (pos, dir) = state
    return if (transitions == null) {
        do {
            pos = (pos + dir + grid.size) % grid.size
        } while (grid[pos] == ' ')
        if (grid[pos] == '.') pos to dir else state
    } else {
        val next = transitions[state] ?: ((pos + dir + grid.size) % grid.size to dir)
        when (grid[next.first]) {
            '.' -> next
            '#' -> state
            else -> error("Invalid tile: $next='${grid[next.first]}' ($pos+$dir)")
        }
    }
}

private val FACINGS = arrayOf(Vec2i.E, Vec2i.S, Vec2i.W, Vec2i.N)

private fun move(grid: CharGrid, state: VecState, step: String, transitions: Map<VecState, VecState>? = null) = when(step) {
    "R" -> state.first to FACINGS[(FACINGS.indexOf(state.second) + 1) % 4]
    "L" -> state.first to FACINGS[(FACINGS.indexOf(state.second) + 3) % 4]
    else -> {
        val steps = step.toInt()
        var state = state
        repeat(steps) {
            state = nextTile(grid, state, transitions)
        }
        state
    }
}

private typealias EdgeSide = Pair<Line2i, Vec2i>
private typealias Edge = Pair<EdgeSide, EdgeSide>
private typealias VecState = Pair<Vec2i, Vec2i>

private fun EdgeSide.reverse() = first.reverse() to second

private class CubeNetDSL(size: Int, val A: Vec2i, val B: Vec2i, val C: Vec2i, val D: Vec2i, val E: Vec2i, val F: Vec2i) {
    private val last = size - 1
    val edges = mutableListOf<Edge>()
    fun bottom(side: Vec2i) = Line2i(Vec2i(side.x, side.y + last), Vec2i(side.x + last, side.y + last)) to Vec2i.S
    fun top(side: Vec2i) = Line2i(Vec2i(side.x, side.y), Vec2i(side.x + last, side.y)) to Vec2i.N
    fun left(side: Vec2i) = Line2i(Vec2i(side.x, side.y), Vec2i(side.x, side.y + last)) to Vec2i.W
    fun right(side: Vec2i) = Line2i(Vec2i(side.x + last, side.y), Vec2i(side.x + last, side.y + last)) to Vec2i.E
    fun edge(edge: Edge) {
        edges.add(edge)
    }
}

private fun cubeNet(size: Int, A: Vec2i, B: Vec2i, C: Vec2i, D: Vec2i, E: Vec2i, F: Vec2i, block: CubeNetDSL.() -> Unit) =
    CubeNetDSL(size, A * size, B * size, C * size, D * size, E * size, F * size).apply(block).edges

private fun cubeNetTest(size: Int) = cubeNet(size, Vec2i(2, 0), Vec2i(0, 1), Vec2i(1, 1), Vec2i(2, 1), Vec2i(2, 2), Vec2i(3, 2)) {
    // ..A.
    // BCD.
    // ..EF
    edge(left(A) to top(C))
    edge(top(A) to top(B).reverse())
    edge(right(A) to right(F).reverse())
    edge(left(B) to bottom(F).reverse())
    edge(bottom(B) to bottom(E).reverse())
    edge(bottom(C) to left(E).reverse())
    edge(right(D) to top(F).reverse())
}

private fun cubeNetReal(size: Int) = cubeNet(size, Vec2i(1, 0), Vec2i(2, 0), Vec2i(1, 1), Vec2i(0, 2), Vec2i(1, 2), Vec2i(0, 3)) {
    // .AB
    // .C.
    // DE.
    // F..
    edge(left(A) to left(D).reverse())
    edge(top(A) to left(F))
    edge(top(B) to bottom(F))
    edge(right(B) to right(E).reverse())
    edge(bottom(B) to right(C))
    edge(left(C) to top(D))
    edge(bottom(E) to right(F))
}

private fun buildTransitions(edges: List<Edge>): MutableMap<VecState, VecState> {
    val transitions = mutableMapOf<VecState, VecState>()
    for ((a, b) in edges) {
        val (aLine, aDir) = a
        val aPoints = aLine.toList()
        val (bLine, bDir) = b
        val bPoints = bLine.toList()
        for (i in aPoints.indices) {
            transitions[aPoints[i] to aDir] = bPoints[i] to -bDir
            transitions[bPoints[i] to bDir] = aPoints[i] to -aDir
        }
    }
    return transitions
}

private fun run(input: PuzzleInput, transitions: Map<VecState, VecState>? = null): Int {
    val (grid, path) = parseInput(input)
    var state = grid.where { it == '.' }.first() to Vec2i.E
    for (step in path) {
        state = move(grid, state, step, transitions)
    }
    val pos = state.first
    return pos.y * 1000 + pos.x * 4 + FACINGS.indexOf(state.second) + 1004
}

val test = TestInput("""
            ...#
            .#..
            #...
            ....
    ...#.......#
    ........#...
    ..#....#....
    ..........#.
            ...#....
            .....#..
            .#......
            ......#.
    
    10R5L5R10L4R5L5
""")

@PuzzleName("Monkey Map")
fun PuzzleInput.part1() = run(this)
fun PuzzleInput.part2() = run(this, buildTransitions(cubeNetReal(50)))