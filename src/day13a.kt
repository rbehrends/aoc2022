package day13a

import util.*
import kotlin.math.*
import kotlinx.serialization.json.*

// Uses JSON parsing for the input

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

fun parse(s: String): Item {
  fun toItem(json: JsonElement): Item = when (json) {
    is JsonArray -> ItemList(json.map { toItem(it) })
    is JsonPrimitive -> Value(json.intOrNull!!)
    else -> throw IllegalArgumentException("bad input")
  }
  return toItem(Json.parseToJsonElement(s))
}

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