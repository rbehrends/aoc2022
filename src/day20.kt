package day20

import util.*

infix fun Int.mod(m: Int): Int = if (this < 0) this % m + m else this % m
infix fun Long.mod(m: Int): Long = if (this < 0) this % m + m else this % m

fun shuffle(list: MutableList<Pair<Int, Long>>, count: Int = 1) {
  val originalList = list.toList()
  repeat(count) {
    for ((index, by) in originalList) {
      val from = list.indexOfFirst { (i, _) -> i == index }
      val item = list.removeAt(from)
      val to = ((from + by) mod list.size).toInt()
      list.add(to, item)
    }
  }
}

fun checksum(list: List<Pair<Int, Long>>): Long {
  val zeroPos = list.indexOfFirst { (_, x) -> x == 0L }
  return (1..3).sumOf { i -> list[(zeroPos + 1000 * i) mod list.size].second }
}

fun main() {
  val numbers = inputFile().readText().extractSignedNumbers()
  part1 {
    val list =
      numbers.mapIndexed { i, x -> Pair(i, x.toLong()) }.toMutableList()
    shuffle(list)
    checksum(list)
  }
  part2 {
    val key = 811589153
    val list =
      numbers.mapIndexed { i, x -> Pair(i, x.toLong() * key) }.toMutableList()
    shuffle(list, 10)
    checksum(list)
  }
}