package day21

import util.*

sealed class Expr
data class Value(val value: Long) : Expr()
data class Op(val op: String, val lhs: String, val rhs: String) : Expr()

typealias Equations = Map<String, Expr>

fun eval(equations: Equations, varname: String): Long =
  when (val expr = equations[varname]!!) {
    is Value -> expr.value
    is Op -> {
      val lhs = eval(equations, expr.lhs)
      val rhs = eval(equations, expr.rhs)
      when (expr.op) {
        "+" -> lhs + rhs
        "*" -> lhs * rhs
        "-" -> lhs - rhs
        "/" -> lhs / rhs
        else -> throw IllegalArgumentException("invalid operation")
      }
    }
  }

// Represents the expression a * x + b
data class SymExpr(val a: Rational, val b: Rational) {
  override fun toString(): String =
    if (a == 0.toRational()) "$b" else "$a * x + $b"
}

fun symEval(equations: Equations, varname: String): SymExpr =
  if (varname == "humn")
    SymExpr(a = 1.toRational(), b = 0.toRational())
  else when (val expr = equations[varname]!!) {
    is Value -> SymExpr(0.toRational(), expr.value.toRational())
    is Op -> {
      val lhs = symEval(equations, expr.lhs)
      val rhs = symEval(equations, expr.rhs)
      // at least one of lhs.a and rhs.a is always zero
      when (expr.op) {
        "+" -> SymExpr(lhs.a + rhs.a, lhs.b + rhs.b)
        "-" -> SymExpr(lhs.a - rhs.a, lhs.b - rhs.b)
        "*" -> SymExpr(lhs.a * rhs.b + rhs.a * lhs.b, lhs.b * rhs.b)
        "/" -> SymExpr(lhs.a / rhs.b + rhs.a / lhs.b, lhs.b / rhs.b)
        else -> throw IllegalArgumentException("invalid operation")
      }
    }
  }

fun main() {
  val lines = inputFile().readLines()
  val equations = lines.associate {
    val tokens = it.split(Regex("[: ]+"))
    if (tokens.size == 2)
      tokens.first() to Value(tokens[1].toLong())
    else
      tokens.first() to Op(op = tokens[2], lhs = tokens[1], rhs = tokens[3])
  }.toMutableMap()
  part1 {
    eval(equations, "root")
  }
  part2 {
    equations["root"] = (equations["root"]!! as Op).copy(op = "-")
    val eq = symEval(equations, "root")
    // solve a * x + b = 0 => x = -b/a
    -eq.b / eq.a
  }
}