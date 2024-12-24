package de.skyrising.aoc

import jdk.incubator.vector.IntVector
import jdk.incubator.vector.VectorOperators

inline operator fun IntVector.plus(other: IntVector): IntVector = lanewise(VectorOperators.ADD, other)
inline operator fun IntVector.minus(other: IntVector): IntVector = lanewise(VectorOperators.SUB, other)
inline infix fun IntVector.xor(other: IntVector): IntVector = lanewise(VectorOperators.XOR, other)
inline infix fun IntVector.and(other: IntVector): IntVector = lanewise(VectorOperators.AND, other)
inline infix fun IntVector.shl(amount: Int): IntVector = lanewise(VectorOperators.LSHL, amount)
inline infix fun IntVector.ushr(amount: Int): IntVector = lanewise(VectorOperators.LSHR, amount)
