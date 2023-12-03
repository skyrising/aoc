package de.skyrising.aoc2022

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay21 : BenchmarkDayV1(21)

private interface MonkeyMath {
    fun compute(monkeys: Map<String, MonkeyMath>): LongFraction
    fun toPolynomial(monkeys: Map<String, MonkeyMath>): LongPolynomial
}

private data class MonkeyConst(val value: Long) : MonkeyMath {
    override fun compute(monkeys: Map<String, MonkeyMath>) = LongFraction(value)
    override fun toPolynomial(monkeys: Map<String, MonkeyMath>) = LongPolynomial(arrayOf(LongFraction(value)))
}

private data class MonkeyOp(val lhs: String, val rhs: String, val op: Char) : MonkeyMath {
    override fun compute(monkeys: Map<String, MonkeyMath>): LongFraction {
        val a = monkeys[lhs]!!.compute(monkeys)
        val b = monkeys[rhs]!!.compute(monkeys)
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> error("Unknown op $op")
        }
    }

    override fun toPolynomial(monkeys: Map<String, MonkeyMath>): LongPolynomial {
        val a = if (lhs == "humn") LongPolynomial.X else monkeys[lhs]!!.toPolynomial(monkeys)
        val b = if (rhs == "humn") LongPolynomial.X else monkeys[rhs]!!.toPolynomial(monkeys)
        val c = when (op) {
            '+' -> a + b
            '-' -> a - b
            '=' -> b - a
            '*' -> a * b
            '/' -> a / b.coefficients[0]
            else -> error("Unknown op $op")
        }
        return c
    }
}

private fun parseInput(input: PuzzleInput): MutableMap<String, MonkeyMath> {
    val monkeys = linkedMapOf<String, MonkeyMath>()
    for (line in input.lines) {
        val name = line.substring(0, 4)
        val parts = line.substring(6).split(' ')
        if (parts.size == 1) {
            monkeys[name] = MonkeyConst(parts[0].toLong())
        } else {
            monkeys[name] = MonkeyOp(parts[0], parts[2], parts[1][0])
        }
    }
    return monkeys
}

@Suppress("unused")
fun registerDay21() {
    val test = TestInput("""
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """)
    part1("Monkey Math") {
        val monkeys = parseInput(this)
        monkeys["root"]!!.compute(monkeys)
    }
    part2 {
        val monkeys = parseInput(this)
        val root = monkeys["root"] as MonkeyOp
        MonkeyOp(root.lhs, root.rhs, '=').toPolynomial(monkeys).rootNear(LongFraction(0))
    }
}