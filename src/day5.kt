package day5

import util.*

data class Move(val num: Int, val from: Int, val to: Int)

fun parseStacks(lines: List<String>): List<MutableList<Char>> {
  val stackNums = lines.last().extractNumbers()
  val numStacks = stackNums.last()
  val result = List<MutableList<Char>>(numStacks) { mutableListOf() }
  for (line in lines.reversed().drop(1)) {
    line.chunked(4).forEachIndexed { i, crate ->
      if (crate[1] != ' ') result[i].add(crate[1])
    }
  }
  return result
}

fun main() {
  val (stackDesc, moveDesc) = inputFile().readRecords()
  val moves = moveDesc.trim().lines().map {
    it.extractNumbers().let { (a, b, c) -> Move(a, b, c) }
  }
  val inputStacks = parseStacks(stackDesc.split('\n'))
  part1 {
    val stacks = inputStacks.map { it.toMutableList() }
    for ((num, from, to) in moves) {
      for (i in 1..num) {
        stacks[to - 1].add(stacks[from - 1].removeLast())
      }
    }
    stacks.map { it.last() }.joinToString("")
  }
  part2 {
    val stacks = inputStacks.map { it.toMutableList() }
    for ((num, from, to) in moves) {
      val crates = stacks[from-1].let { it.subList(it.size - num, it.size) }
      stacks[to - 1].addAll(crates)
      crates.clear()
    }
    stacks.map { it.last() }.joinToString("")
  }
}