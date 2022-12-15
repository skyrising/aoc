package de.skyrising.aoc2020

import de.skyrising.aoc.TestInput
import java.util.*

class BenchmarkDay18 : BenchmarkDay(18)

private interface Expr {
    fun eval(): Long
}

private enum class BinOpType {
    ADD {
        override fun invoke(a: Long, b: Long) = a + b
    },
    MUL {
        override fun invoke(a: Long, b: Long) = a * b
    };
    abstract operator fun invoke(a: Long, b: Long): Long
}
private data class BinOp(val a: Expr, val b: Expr, val op: BinOpType) : Expr {
    override fun eval() = op(a.eval(), b.eval())
}

private data class Const(val value: Long) : Expr {
    override fun eval() = value
}

private enum class TokenType { WHITESPACE, CONSTANT, BINOP, OPEN_PARENTHESIS, CLOSE_PARENTHESIS }
private data class Token(val type: TokenType, val value: String) {
    override fun toString() = "$type($value)"
}

private val TOKEN_MAP = mapOf(
    Regex("^\\s+") to TokenType.WHITESPACE,
    Regex("^\\(") to TokenType.OPEN_PARENTHESIS,
    Regex("^\\)") to TokenType.CLOSE_PARENTHESIS,
    Regex("^[+*]") to TokenType.BINOP,
    Regex("^\\d+") to TokenType.CONSTANT
)

private fun lex(s: String): List<Token> {
    var remaining = s
    val tokens = mutableListOf<Token>()
    outer@ while (remaining.isNotEmpty()) {
        for ((regex, type) in TOKEN_MAP) {
            val match = regex.find(remaining)
            // println("$remaining, $regex, $match")
            if (match?.range?.first == 0) {
                val value = match.value
                remaining = remaining.substring(value.length)
                if (type != TokenType.WHITESPACE) {
                    tokens.add(Token(type, value))
                }
                continue@outer
            }
        }
        throw IllegalArgumentException("Cannot parse '$remaining'")
    }
    return tokens
}

private typealias Lexer = String.(Int) -> Int
private typealias LexerMap = MutableCollection<Pair<Lexer, TokenType>>
private fun LexerMap.lexer(type: TokenType, lexer: Lexer) = add(Pair(lexer, type))
private inline fun LexerMap.stringLexer(type: TokenType, str: String) = lexer(type) {
    if (startsWith(str, it)) str.length else 0
}
private inline fun LexerMap.predicateLexer(type: TokenType, crossinline predicate: (Char) -> Boolean) = lexer(type) {
    for (i in it until length) {
        if (!predicate(this[i])) return@lexer i - it
    }
    length - it
}
private val TOKEN_MAP_2 = ArrayList<Pair<Lexer, TokenType>>().apply {
    stringLexer(TokenType.OPEN_PARENTHESIS, "(")
    stringLexer(TokenType.CLOSE_PARENTHESIS, ")")
    lexer(TokenType.BINOP) {
        when (this[it]) {
            '*', '+' -> 1
            else -> 0
        }
    }
    predicateLexer(TokenType.CONSTANT, Character::isDigit)
    predicateLexer(TokenType.WHITESPACE, Character::isWhitespace)
}

private fun lex2(s: String): List<Token> {
    var pos = 0
    val tokens = mutableListOf<Token>()
    outer@ while (pos < s.length) {
        for ((lexer, type) in TOKEN_MAP_2) {
            val len = lexer(s, pos)
            if (len <= 0) continue
            val value = s.substring(pos, pos + len)
            pos += len
            if (type != TokenType.WHITESPACE) {
                tokens.add(Token(type, value))
            }
            continue@outer
        }
        throw IllegalArgumentException("Cannot parse '${s.substring(pos)}'")
    }
    return tokens
}

private fun parse(tokens: List<Token>, precedence: EnumMap<BinOpType, Int>): Expr {
    var (expr, remaining) = parseStep(null, tokens, precedence)
    while (remaining.isNotEmpty()) {
        val (newExpr, newRemaining) = parseStep(expr, remaining, precedence)
        expr = newExpr
        remaining = newRemaining
    }
    return expr
}

private fun parseStep(left: Expr?, tokens: List<Token>, precedence: EnumMap<BinOpType, Int>): Pair<Expr, List<Token>> {
    // println("$left, ${tokens.subList(0, minOf(2, tokens.size))}${if (tokens.size > 2) "..." else ""}")
    when (tokens[0].type) {
        TokenType.OPEN_PARENTHESIS -> {
            if (left != null) throw IllegalArgumentException("Unexpected '('")
            var depth = 0
            for (i in tokens.indices) {
                when (tokens[i].type) {
                    TokenType.OPEN_PARENTHESIS -> depth++
                    TokenType.CLOSE_PARENTHESIS -> depth--
                    else -> {}
                }
                if (depth == 0) {
                    val expr = parse(tokens.subList(1, i), precedence)
                    return Pair(expr, tokens.subList(i + 1, tokens.size))
                }
            }
        }
        TokenType.CLOSE_PARENTHESIS -> throw IllegalArgumentException("Unexpected ')'")
        TokenType.CONSTANT -> {
            if (left != null) throw IllegalArgumentException("Unexpected '${tokens[0].value}'")
            return Pair(Const(tokens[0].value.toLong()), tokens.subList(1, tokens.size))
        }
        TokenType.BINOP -> {
            if (left == null) throw IllegalArgumentException("Unexpected '${tokens[0].value}'")
            return parseBinOp(tokens, left, 0, precedence)
        }
        TokenType.WHITESPACE -> parseStep(left, tokens.subList(1, tokens.size), precedence)
    }
    throw IllegalStateException()
}

private fun getBinOp(s: String) = when (s) {
    "+" -> BinOpType.ADD
    "*" -> BinOpType.MUL
    else -> null
}

private fun parseBinOp(tokens: List<Token>, lhs: Expr, minPrecedence: Int, precedence: EnumMap<BinOpType, Int>): Pair<Expr, List<Token>> {
    var mutLhs = lhs
    var pos = tokens
    while (pos.isNotEmpty() && pos[0].type == TokenType.BINOP) {
        val op = getBinOp(pos[0].value)
        if (op == null || precedence[op]!! < minPrecedence) break
        var rhs = parseStep(null, pos.subList(1, pos.size), precedence)
        pos = rhs.second
        while (pos.isNotEmpty() && pos[0].type == TokenType.BINOP) {
            val op2 = getBinOp(pos[0].value)
            if (op2 == null || precedence[op2]!! < precedence[op]!! || (precedence[op2]!! == precedence[op]/* && !op2.rightAssociative*/)) break
            rhs = parseBinOp(pos, rhs.first, precedence[op2]!!, precedence)
            pos = rhs.second
        }
        mutLhs = BinOp(mutLhs, rhs.first, op)
        pos = rhs.second
    }
    return Pair(mutLhs, pos)
}

fun registerDay18() {
    val test = TestInput("2 * 3 + (4 * 5)")
    puzzle(18, "Operation Order v1") {
        var sum = 0L
        for (line in lines) {
            val expr = parse(lex(line), EnumMap(mapOf(BinOpType.MUL to 1, BinOpType.ADD to 1)))
            sum += expr.eval()
            // println("$line: $expr, ${expr.eval()}")
        }
        sum
    }
    puzzle(18, "Operation Order v2") {
        var sum = 0L
        for (line in lines) {
            val expr = parse(lex2(line), EnumMap(mapOf(BinOpType.MUL to 1, BinOpType.ADD to 1)))
            sum += expr.eval()
            // println("$line: $expr, ${expr.eval()}")
        }
        sum
    }
    puzzle(18, "Part 2 v1") {
        var sum = 0L
        for (line in lines) {
            val expr = parse(lex(line), EnumMap(mapOf(BinOpType.MUL to 1, BinOpType.ADD to 2)))
            sum += expr.eval()
            // println("$line: $expr, ${expr.eval()}")
        }
        sum
    }
    puzzle(18, "Part 2 v2") {
        var sum = 0L
        for (line in lines) {
            val expr = parse(lex2(line), EnumMap(mapOf(BinOpType.MUL to 1, BinOpType.ADD to 2)))
            sum += expr.eval()
            // println("$line: $expr, ${expr.eval()}")
        }
        sum
    }
}