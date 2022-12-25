package day3

import util.*

fun prio(item: Char): Int = when (item) {
  in 'a'..'z' -> item - 'a' + 1
  in 'A'..'Z' -> item - 'A' + 27
  else -> throw IllegalArgumentException()
}

fun prio(items: Set<Char>): Int = items.sumOf(::prio)

fun main() {
  val rucksacks = inputFile().readLines()
  part1 {
    val compartments = rucksacks.map {
      val mid = it.length / 2
      Pair(it.take(mid).toSet(), it.drop(mid).toSet())
    }
    compartments.sumOf { (c1, c2) -> prio(c1 intersect c2) }
  }
  part2 {
    val groups = rucksacks.map { it.toSet() }.chunked(3)
    groups.sumOf { (a, b, c) -> prio(a intersect b intersect c) }
  }
}