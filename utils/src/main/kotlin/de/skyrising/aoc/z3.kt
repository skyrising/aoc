package de.skyrising.aoc

import com.microsoft.z3.*

context(Context) operator fun <T: ArithSort> ArithExpr<T>.plus(other: ArithExpr<T>) = mkAdd(this, other)
context(Context) operator fun <T: ArithSort> ArithExpr<T>.minus(other: ArithExpr<T>) = mkSub(this, other)
context(Context) operator fun <T: ArithSort> ArithExpr<T>.times(other: ArithExpr<T>) = mkMul(this, other)
context(Context) operator fun <T: ArithSort> ArithExpr<T>.div(other: ArithExpr<T>) = mkDiv(this, other)
context(Context) operator fun ArithExpr<IntSort>.rem(other: ArithExpr<IntSort>) = mkRem(this, other)

context(Context) operator fun <T: ArithSort> ArithExpr<T>.unaryMinus() = mkUnaryMinus(this)
context(Context) operator fun <T: ArithSort> ArithExpr<T>.plus(other: Int) = mkAdd(this, mkInt(other))
context(Context) operator fun <T: ArithSort> ArithExpr<T>.minus(other: Int) = mkSub(this, mkInt(other))
context(Context) operator fun <T: ArithSort> ArithExpr<T>.times(other: Int) = mkMul(this, mkInt(other))
context(Context) operator fun <T: ArithSort> ArithExpr<T>.div(other: Int) = mkDiv(this, mkInt(other))
context(Context) operator fun ArithExpr<IntSort>.rem(other: Int) = mkRem(this, mkInt(other))

context(Context) operator fun <T: ArithSort> ArithExpr<T>.plus(other: Long) = mkAdd(this, mkInt(other))
context(Context) operator fun <T: ArithSort> ArithExpr<T>.minus(other: Long) = mkSub(this, mkInt(other))
context(Context) operator fun <T: ArithSort> ArithExpr<T>.times(other: Long) = mkMul(this, mkInt(other))
context(Context) operator fun <T: ArithSort> ArithExpr<T>.div(other: Long) = mkDiv(this, mkInt(other))
context(Context) operator fun ArithExpr<IntSort>.rem(other: Long) = mkRem(this, mkInt(other))

context(Context) infix fun <T: ArithSort> ArithExpr<T>.eq(other: ArithExpr<T>) = mkEq(this, other)
context(Context) infix fun <T: ArithSort> ArithExpr<T>.ne(other: ArithExpr<T>) = mkNot(mkEq(this, other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.lt(other: ArithExpr<T>) = mkLt(this, other)
context(Context) infix fun <T: ArithSort> ArithExpr<T>.le(other: ArithExpr<T>) = mkLe(this, other)
context(Context) infix fun <T: ArithSort> ArithExpr<T>.gt(other: ArithExpr<T>) = mkGt(this, other)
context(Context) infix fun <T: ArithSort> ArithExpr<T>.ge(other: ArithExpr<T>) = mkGe(this, other)
context(Context) infix fun <T: ArithSort> ArithExpr<T>.eq(other: Int) = mkEq(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.ne(other: Int) = mkNot(mkEq(this, mkInt(other)))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.lt(other: Int) = mkLt(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.le(other: Int) = mkLe(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.gt(other: Int) = mkGt(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.ge(other: Int) = mkGe(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.eq(other: Long) = mkEq(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.ne(other: Long) = mkNot(mkEq(this, mkInt(other)))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.lt(other: Long) = mkLt(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.le(other: Long) = mkLe(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.gt(other: Long) = mkGt(this, mkInt(other))
context(Context) infix fun <T: ArithSort> ArithExpr<T>.ge(other: Long) = mkGe(this, mkInt(other))

fun Context.mkReal(v: Vec2i) = Pair(mkReal(v.x), mkReal(v.y))
fun Context.mkReal(v: Vec2l) = Pair(mkReal(v.x), mkReal(v.y))
fun Context.mkReal(v: Vec3i) = Triple(mkReal(v.x), mkReal(v.y), mkReal(v.z))
fun Context.mkReal(v: Vec3l) = Triple(mkReal(v.x), mkReal(v.y), mkReal(v.z))

inline fun Context.mkRealConst(vararg names: String) = names.map { mkRealConst(it) }
inline fun Context.mkIntConst(vararg names: String) = names.map { mkIntConst(it) }

operator fun Solver.plusAssign(constraint: Expr<BoolSort>) = add(constraint)
operator fun Optimize.plusAssign(constraint: Expr<BoolSort>) = Add(constraint)

operator fun <R: Sort> Model.get(expr: Expr<R>): Expr<R> = getConstInterp(expr)
operator fun <R: Sort> Model.get(func: FuncDecl<R>): Expr<R> = getConstInterp(func)

fun RatNum.toLong() = if (denominator.int64 != 1L) throw IllegalArgumentException() else numerator.int64
fun IntNum.toLong() = int64
fun Expr<*>.toLong(): Long = when (this) {
    is RatNum -> toLong()
    is IntNum -> toLong()
    else -> throw IllegalArgumentException()
}
