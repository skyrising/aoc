@file:Suppress("NOTHING_TO_INLINE")

package de.skyrising.aoc

import com.microsoft.z3.*

context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.plus(other: ArithExpr<T>) = ctx.mkAdd(this, other)
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.minus(other: ArithExpr<T>) = ctx.mkSub(this, other)
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.times(other: ArithExpr<T>) = ctx.mkMul(this, other)
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.div(other: ArithExpr<T>) = ctx.mkDiv(this, other)
context(ctx: Context) operator fun ArithExpr<IntSort>.rem(other: ArithExpr<IntSort>) = ctx.mkRem(this, other)

context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.unaryMinus() = ctx.mkUnaryMinus(this)
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.plus(other: Int) = ctx.mkAdd(this, ctx.mkInt(other))
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.minus(other: Int) = ctx.mkSub(this, ctx.mkInt(other))
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.times(other: Int) = ctx.mkMul(this, ctx.mkInt(other))
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.div(other: Int) = ctx.mkDiv(this, ctx.mkInt(other))
context(ctx: Context) operator fun ArithExpr<IntSort>.rem(other: Int) = ctx.mkRem(this, ctx.mkInt(other))

context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.plus(other: Long) = ctx.mkAdd(this, ctx.mkInt(other))
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.minus(other: Long) = ctx.mkSub(this, ctx.mkInt(other))
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.times(other: Long) = ctx.mkMul(this, ctx.mkInt(other))
context(ctx: Context) operator fun <T: ArithSort> ArithExpr<T>.div(other: Long) = ctx.mkDiv(this, ctx.mkInt(other))
context(ctx: Context) operator fun ArithExpr<IntSort>.rem(other: Long) = ctx.mkRem(this, ctx.mkInt(other))

context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.eq(other: ArithExpr<T>) = ctx.mkEq(this, other)
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.ne(other: ArithExpr<T>) = ctx.mkNot(ctx.mkEq(this, other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.lt(other: ArithExpr<T>) = ctx.mkLt(this, other)
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.le(other: ArithExpr<T>) = ctx.mkLe(this, other)
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.gt(other: ArithExpr<T>) = ctx.mkGt(this, other)
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.ge(other: ArithExpr<T>) = ctx.mkGe(this, other)
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.eq(other: Int) = ctx.mkEq(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.ne(other: Int) = ctx.mkNot(ctx.mkEq(this, ctx.mkInt(other)))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.lt(other: Int) = ctx.mkLt(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.le(other: Int) = ctx.mkLe(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.gt(other: Int) = ctx.mkGt(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.ge(other: Int) = ctx.mkGe(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.eq(other: Long) = ctx.mkEq(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.ne(other: Long) = ctx.mkNot(ctx.mkEq(this, ctx.mkInt(other)))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.lt(other: Long) = ctx.mkLt(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.le(other: Long) = ctx.mkLe(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.gt(other: Long) = ctx.mkGt(this, ctx.mkInt(other))
context(ctx: Context) infix fun <T: ArithSort> ArithExpr<T>.ge(other: Long) = ctx.mkGe(this, ctx.mkInt(other))

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
