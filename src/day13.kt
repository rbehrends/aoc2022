package day13

import util.*
import kotlin.math.*

sealed class Item : Comparable<Item>

data class Value(val value: Int) : Item() {
  override fun toString(): String = value.toString()

  override operator fun compareTo(other: Item): Int = when (other) {
    is Value -> value compareTo other.value
    is ItemList -> ItemList(listOf(this)) compareTo other
  }
}

data class ItemList(val items: List<Item>) : Item() {
  override fun toString(): String =
    items.joinToString(",", "[", "]") { it.toString() }

  override operator fun compareTo(other: Item): Int = when (other) {
    is Value -> this compareTo ItemList(listOf(other))
    is ItemList ->
      (0 until min(items.size, other.items.size)).asSequence().map {
        items[it] compareTo other.items[it]
      }.firstOrNull { it != 0 } ?: (items.size compareTo other.items.size)
  }
}

class Parser(input: String) {
  var pos = 0
  val text = "$input."
  val ch inline get() = text[pos]
  inline fun match(pred: () -> Boolean) = pred().also { if (it) pos++ }
  fun parseItem(): Item = when {
    ch in '0'..'9' -> {
      val start = pos
      while (match { ch in '0'..'9' });
      Value(text[start until pos].toInt())
    }

    match { ch == '[' } -> {
      val result = mutableListOf<Item>()
      if (!match { ch == ']' }) {
        result += parseItem()
        while (match { ch == ',' })
          result += parseItem()
      }
      match { ch == ']' }
      ItemList(result)
    }

    else -> throw IllegalArgumentException("parse error")
  }
}

fun parse(s: String): Item = Parser(s).parseItem()

fun main() {
  val data = inputFile().readRecords()
    .map { it.lines().let { (a, b) -> parse(a) to parse(b) } }
  part1 {
    data.withIndex().filter { (_, items) -> items.first <= items.second }
      .sumOf { (index, _) -> index + 1 }
  }
  part2 {
    val div1 = parse("[[2]]")
    val div2 = parse("[[6]]")
    with((data.flatMap { (a, b) -> listOf(a, b) } + div1 + div2).sorted()) {
      (indexOfFirst { it == div1 } + 1) * (indexOfFirst { it == div2 } + 1)
    }
  }
}