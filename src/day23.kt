package day23

import util.*

val north = Pos(-1, 0)
val south = Pos(1, 0)
val west = Pos(0, -1)
val east = Pos(0, 1)

val checkList = listOf(
  listOf(north, north + east, north + west),
  listOf(south, south + east, south + west),
  listOf(west, north + west, south + west),
  listOf(east, north + east, south + east)
)

val allDirections = listOf(
  north, north + east, east, south + east,
  south, south + west, west, north + west
)

fun update(elves: Set<Pos>, round: Int): Set<Pos> {
  val proposed = mutableListOf<Pair<Pos, Pos>>()
  for (elf in elves) {
    if (allDirections.all { elf + it !in elves }) continue
    check@ for (i in checkList.indices) {
      val dirs = checkList[(round + i) % checkList.size]
      for (dir in dirs)
        if (elf + dir in elves)
          continue@check
      proposed += elf to elf + dirs[0]
      break
    }
  }
  val validMoves = proposed
      .groupBy(
        keySelector = { (_, loc) -> loc },
        valueTransform = { (elf, _) -> elf })
      .filter { (_, elves) -> elves.size == 1 }
      .map { (loc, elves) -> elves.first() to loc }.toMap()
  return elves.map { elf -> validMoves[elf] ?: elf }.toSet()
}

fun main() {
  val initialMap = inputFile().readText().trim().lines()
  val elves = initialMap.withIndex().flatMap { (rowIndex, row) ->
    row.withIndex().filter { (_, ch) -> ch == '#' }.map { (colIndex, _) ->
      Pos(rowIndex, colIndex)
    }
  }.toSet()
  part1 {
    val finalState = (0 until 10).fold(elves, ::update)
    val minCol = finalState.minOf { it.col }
    val maxCol = finalState.maxOf { it.col }
    val minRow = finalState.minOf { it.row }
    val maxRow = finalState.maxOf { it.row }
    val area = (maxRow - minRow + 1) * (maxCol - minCol + 1)
    area - finalState.size
  }
  part2 part2@{
    var old = elves
    for (round in generateSequence(0) { it + 1 }) {
      val new = update(old, round)
      if (new == old) return@part2 round + 1
      old = new
    }
  }
}