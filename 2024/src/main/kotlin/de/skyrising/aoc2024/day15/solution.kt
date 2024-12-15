package de.skyrising.aoc2024.day15

import de.skyrising.aoc.*

val test = TestInput("""
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
""")

@PuzzleName("")
fun PuzzleInput.part1(): Any {
    val (room, moves) = lines.splitOnEmpty()
    val roomGrid = CharGrid.parse(room)
    var robotPos = roomGrid.where { it == '@' }.first()
    roomGrid[robotPos] = '.'
    for (move in moves.joinToString("")) {
        val direction = Direction("^>v<".indexOf(move))
        val newPos = robotPos + direction
        if (roomGrid[newPos] == '#') continue
        if (roomGrid[newPos] == 'O') {
            var boxPos = newPos
            while (roomGrid[boxPos] == 'O') {
                boxPos += direction
            }
            if (roomGrid[boxPos] == '#') continue
            roomGrid[boxPos] = 'O'
            roomGrid[newPos] = '.'
        }
        robotPos = newPos
    }
    return roomGrid.where { it == 'O' }.sumOf { it.x + it.y * 100 }
}

fun PuzzleInput.part2(): Any {
    val (room, moves) = lines.splitOnEmpty()
    val roomGrid = CharGrid.parse(room)
    val scale = Vec2i(2, 1)
    var robotPos = roomGrid.where { it == '@' }.first() * scale
    val walls = roomGrid.where { it == '#' }.flatMapTo(mutableSetOf()) { listOf(it * scale, it * scale + Vec2i.E) }
    val boxes = roomGrid.where { it == 'O' }.mapTo(mutableSetOf()) { it * scale }
    val scaledGrid = walls.charGrid('.') { '#' }.also {
        it[boxes] = '['
        it[boxes.map { it.east }] = ']'
        it[robotPos] = '@'
    }
    outer@for (move in moves.joinToString("")) {
        val direction = Direction("^>v<".indexOf(move))
        val moveSet = mutableSetOf<Vec2i>()
        val todo = ArrayDeque<Vec2i>()
        todo.add(robotPos)
        while (todo.isNotEmpty()) {
            val pos = todo.removeFirst()
            if (!moveSet.add(pos)) continue
            when (scaledGrid[pos]) {
                '#' -> continue@outer
                '.' -> {
                    moveSet.remove(pos)
                    continue
                }
                '[' -> todo.add(pos.east)
                ']' -> todo.add(pos.west)
            }
            todo.add(pos + direction)
        }
        for (pos in moveSet.reversed()) {
            scaledGrid[pos + direction] = scaledGrid[pos]
            if (pos - direction !in moveSet) scaledGrid[pos] = '.'
        }
        robotPos += direction
    }
    return scaledGrid.where { it == '[' }.sumOf { it.x + it.y * 100 }
}
