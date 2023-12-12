package de.skyrising.aoc2021.day18

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2021, 18)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
        [[[5,[2,8]],4],[5,[[9,9],0]]]
        [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
        [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
        [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
        [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
        [[[[5,4],[7,7]],8],[[8,3],8]]
        [[9,3],[[9,9],[6,[4,9]]]]
        [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
        [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """)
    val test1 = TestInput("""
        [1,1]
        [2,2]
        [3,3]
        [4,4]
        [5,5]
    """)
    part1("Snailfish") {
        lines.map(SnailfishNumber::parse).reduce(SnailfishNumber::plus).magnitude()
    }
    part2 {
        lines.map(SnailfishNumber::parse).pairs().map { (a, b) -> a + b }.maxOf(SnailfishNumber::magnitude)
    }
}

fun <T> List<T>.pairs(): Set<Pair<T, T>> {
    val pairs = HashSet<Pair<T, T>>(size * (size - 1))
    for (i in indices) {
        for (j in indices) {
            if (i != j) {
                pairs.add(this[i] to this[j])
            }
        }
    }
    return pairs
}

class SnailfishNumber(var a: Any, var b: Any) {
    init {
        if ((a !is Int && a !is SnailfishNumber) || (b !is Int && b !is SnailfishNumber)) throw IllegalArgumentException()
    }

    operator fun plus(other: SnailfishNumber): SnailfishNumber {
        val result = SnailfishNumber(this.clone(), other.clone())
        reduce(result)
        return result
    }

    fun magnitude(): Int {
        return 3 * magnitude(a) + 2 * magnitude(b)
    }

    override fun toString() = "[$a,$b]"

    operator fun component1() = a
    operator fun component2() = b

    operator fun get(a: Boolean) = if (a) this.a else this.b
    operator fun set(a: Boolean, v: Any) {
        if (a) {
            this.a = v
        } else {
            this.b = v
        }
    }

    fun clone(): SnailfishNumber = SnailfishNumber(
        if (a is SnailfishNumber) (a as SnailfishNumber).clone() else a,
        if (b is SnailfishNumber) (b as SnailfishNumber).clone() else b
    )

    companion object {
        fun parse(s: String) = parse0(s).first

        private fun magnitude(o: Any): Int {
            return if (o is Int) o else if (o is SnailfishNumber) o.magnitude() else throw IllegalStateException()
        }

        private fun reduce(n: SnailfishNumber) {
            while (true) {
                if (tryExplode(n, 0){} != null) continue
                if (trySplit(n)) continue
                break
            }
        }

        private fun processExplode(exp: Pair<Int?, Int?>, n: SnailfishNumber, a: Boolean): Pair<Int?, Int?> {
            val (x, y) = exp
            val v = if (a) y else x
            if (v != null) {
                var t = n
                var i = !a
                while (t[i] !is Int) {
                    t = t[i] as SnailfishNumber
                    i = a
                }
                t[i] = (t[i] as Int) + v
                return if (a) Pair(x, null) else Pair(null, y)
            }
            return exp
        }

        private fun tryExplode(n: SnailfishNumber, depth: Int, explode: () -> Unit): Pair<Int?, Int?>? {
            val (a, b) = n
            if (a is Int && b is Int && depth >= 4) {
                explode()
                return Pair(a, b)
            }
            if (a is SnailfishNumber) {
                val aExp = tryExplode(a, depth + 1) {
                    n.a = 0
                }
                if (aExp != null) {
                    return processExplode(aExp, n, true)
                }
            }
            if (b is SnailfishNumber) {
                val bExp = tryExplode(b, depth + 1) {
                    n.b = 0
                }
                if (bExp != null) {
                    return processExplode(bExp, n, false)
                }
            }
            return null
        }

        private fun trySplit(n: SnailfishNumber): Boolean {
            val (a, b) = n
            if (a is SnailfishNumber) {
                if (trySplit(a)) return true
            } else if (a is Int && a > 9) {
                n.a = SnailfishNumber(a / 2, (a + 1) / 2)
                return true
            }
            if (b is SnailfishNumber) {
                if (trySplit(b)) return true
            } else if (b is Int && b > 9) {
                n.b = SnailfishNumber(b / 2, (b + 1) / 2)
                return true
            }
            return false
        }

        private fun parse0(s: String): Pair<SnailfishNumber, String> {
            val (a, remA) = if (s[1] == '[') {
                parse0(s.substring(1))
            } else {
                val comma = s.indexOf(',')
                s.substring(1, comma).toInt() to s.substring(comma)
            }
            var (b, remB) = if (remA[1] == '[') {
                parse0(remA.substring(1))
            } else {
                val end = remA.indexOf(']')
                remA.substring(1, end).toInt() to remA.substring(end + 1)
            }
            if (remB.startsWith("]")) remB = remB.substring(1)
            return SnailfishNumber(a, b) to remB
        }
    }
}