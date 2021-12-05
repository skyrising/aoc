package de.skyrising.aoc2021

class BenchmarkDay4 : BenchmarkDayV1(4)

fun registerDay4() {
    val test = """
            7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

            22 13 17 11  0
             8  2 23  4 24
            21  9 14 16  7
             6 10  3 18  5
             1 12 20 15 19
            
             3 15  0  2 22
             9 18 13 17  5
            19  8  7 25 23
            20 11 10 24  4
            14 21 16 12  6
            
            14 21 17 24  4
            10 16 15  9 19
            18  8 23 26 20
            22 11 13  6  5
             2  0 12  3  7
        """.trimIndent().lines()

    fun readInput(input: List<String>): Pair<List<Int>, List<IntArray>> {
        val numbers = input[0].split(',').map(String::toInt)
        val boards = mutableListOf<IntArray>()
        for (i in 0 until (input.size - 1) / 6) {
            val board = IntArray(25)
            for (y in 0 until 5) {
                val line = input[2 + i * 6 + y].trim().split(Regex("\\s+"))
                for (x in 0 until 5) {
                    board[y * 5 + x] = line[x].toInt()
                }
            }
            boards.add(board)
        }
        return numbers to boards
    }

    puzzleLS(4, "Giant Squid") {
        val (numbers, boards) = readInput(it)
        val markedByRow = ByteArray(boards.size * 5)
        val markedByColumn = ByteArray(boards.size * 5)
        for (number in numbers) {
            for (i in boards.indices) {
                val board = boards[i]
                for (y in 0 until 5) {
                    for (x in 0 until 5) {
                        val num = board[y * 5 + x]
                        if (num != number) continue
                        markedByRow[i * 5 + y] = (markedByRow[i * 5 + y].toInt() or (1 shl x)).toByte()
                        markedByColumn[i * 5 + x] = (markedByColumn[i * 5 + x].toInt() or (1 shl y)).toByte()
                    }
                }
            }
            var winningBoard: Int? = null
            for (i in markedByRow.indices) {
                if (markedByRow[i].toInt() == 0x1f) {
                    winningBoard = i / 5
                }
            }
            for (i in markedByColumn.indices) {
                if (markedByColumn[i].toInt() == 0x1f) {
                    winningBoard = i / 5
                }
            }
            if (winningBoard != null) {
                val board = boards[winningBoard]
                var sum = 0
                for (y in 0 until 5) {
                    for (x in 0 until 5) {
                        if ((markedByRow[winningBoard * 5 + y].toInt() shr x) and 1 == 0) {
                            sum += board[y * 5 + x]
                        }
                    }
                }
                return@puzzleLS sum * number
            }
        }
        -1
    }
    puzzleLS(4, "Part Two") {
        val (numbers, boards) = readInput(it)
        val markedByRow = ByteArray(boards.size * 5)
        val markedByColumn = ByteArray(boards.size * 5)
        val wonBoards = linkedSetOf<Int>()
        val wonNumbers = linkedSetOf<Int>()
        for (number in numbers) {
            for (i in boards.indices) {
                val board = boards[i]
                for (y in 0 until 5) {
                    for (x in 0 until 5) {
                        val num = board[y * 5 + x]
                        if (num != number) continue
                        markedByRow[i * 5 + y] = (markedByRow[i * 5 + y].toInt() or (1 shl x)).toByte()
                        markedByColumn[i * 5 + x] = (markedByColumn[i * 5 + x].toInt() or (1 shl y)).toByte()
                    }
                }
            }
            for (i in markedByRow.indices) {
                if (markedByRow[i].toInt() == 0x1f) {
                    wonBoards.add(i / 5)
                    wonNumbers.add(number)
                }
            }
            for (i in markedByColumn.indices) {
                if (markedByColumn[i].toInt() == 0x1f) {
                    wonBoards.add(i / 5)
                    wonNumbers.add(number)
                }
            }
            if (wonBoards.size == boards.size) break
        }
        val winningBoard = wonBoards.last()
        val board = boards[winningBoard]
        var sum = 0
        for (y in 0 until 5) {
            for (x in 0 until 5) {
                if ((markedByRow[winningBoard * 5 + y].toInt() shr x) and 1 == 0) {
                    sum += board[y * 5 + x]
                }
            }
        }
        return@puzzleLS sum * wonNumbers.last()
    }
}