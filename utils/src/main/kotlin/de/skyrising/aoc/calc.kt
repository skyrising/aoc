package de.skyrising.aoc

import org.apache.commons.math3.util.ArithmeticUtils.gcd
import kotlin.math.max

infix fun Long.over(other: Long) = LongFraction(this, other).reduce()

data class LongFraction(val numerator: Long, val denominator: Long) {
    init {
        require(denominator != 0L)
    }
    constructor(value: Long) : this(value, 1)

    operator fun plus(other: LongFraction): LongFraction {
        return numerator * other.denominator + other.numerator * denominator over denominator * other.denominator
    }

    operator fun minus(other: LongFraction): LongFraction {
        return numerator * other.denominator - other.numerator * denominator over denominator * other.denominator
    }

    operator fun times(other: LongFraction): LongFraction {
        return numerator * other.numerator over denominator * other.denominator
    }

    operator fun div(other: LongFraction): LongFraction {
        if (denominator == other.denominator) return numerator over other.numerator
        return numerator * other.denominator over denominator * other.numerator
    }

    operator fun unaryMinus(): LongFraction {
        return -numerator over denominator
    }

    operator fun compareTo(other: LongFraction): Int {
        return (numerator * other.denominator).compareTo(other.numerator * denominator)
    }

    override fun toString(): String {
        return if (denominator != 1L) "$numerator/$denominator" else "$numerator"
    }

    fun equals(value: Long) = denominator == 1L && numerator == value

    fun reduce(): LongFraction {
        val gcd = gcd(numerator, denominator)
        if (gcd == 1L) return this
        return numerator / gcd over denominator / gcd
    }
}

data class LongPolynomial(val coefficients: Array<LongFraction>) {
    constructor(constant: Long) : this(arrayOf(LongFraction(constant)))
    constructor(constant: LongFraction) : this(arrayOf(constant))

    fun eval(x: LongFraction): LongFraction {
        var result = LongFraction(0)
        for (i in coefficients.indices) {
            result = result * x + coefficients[i]
        }
        return result
    }

    operator fun plus(other: LongPolynomial): LongPolynomial {
        val result = Array(max(coefficients.size, other.coefficients.size)) { LongFraction(0) }
        for (i in coefficients.indices) {
            result[i] += coefficients[i]
        }
        for (i in other.coefficients.indices) {
            result[i] += other.coefficients[i]
        }
        return LongPolynomial(result)
    }

    operator fun minus(other: LongPolynomial): LongPolynomial {
        val result = Array(max(coefficients.size, other.coefficients.size)) { LongFraction(0) }
        for (i in coefficients.indices) {
            result[i] += coefficients[i]
        }
        for (i in other.coefficients.indices) {
            result[i] -= other.coefficients[i]
        }
        return LongPolynomial(result)
    }

    operator fun times(other: LongPolynomial): LongPolynomial {
        val result = Array(coefficients.size + other.coefficients.size - 1) { LongFraction(0) }
        for (i in coefficients.indices) {
            for (j in other.coefficients.indices) {
                result[i + j] += coefficients[i] * other.coefficients[j]
            }
        }
        return LongPolynomial(result)
    }

    operator fun div(other: LongFraction): LongPolynomial {
        return LongPolynomial(coefficients.map { it / other }.toTypedArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LongPolynomial
        if (!coefficients.contentDeepEquals(other.coefficients)) return false
        return true
    }

    override fun hashCode(): Int {
        return coefficients.contentDeepHashCode()
    }

    override fun toString(): String {
        return coefficients.indices.joinToString(" + ") {
            when (it) {
                0 -> coefficients[it].toString()
                1 -> if (coefficients[it].equals(1)) "x" else "${coefficients[it]}x"
                else -> if (coefficients[it].equals(1)) "x^$it" else "${coefficients[it]}x^$it"
            }
        }
    }

    fun rootNear(x: LongFraction): LongFraction? {
        when (coefficients.size) {
            0 -> return null
            1 -> return if (coefficients[0].equals(0)) x else null
            2 -> {
                val a = coefficients[1]
                val b = coefficients[0]
                if (a.equals(0)) return if (b.equals(0)) x else null
                return -b / a
            }
            else -> TODO("Newton's method")
        }
    }

    companion object {
        val ZERO = LongPolynomial(0)
        val ONE = LongPolynomial(1)
        val X = LongPolynomial(arrayOf(LongFraction(0), LongFraction(1)))
    }
}