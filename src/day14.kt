package day14

import util.*
import kotlin.math.*

data class Vec(val x: Int, val y: Int) {
  operator fun plus(other: Vec): Vec = Vec(x + other.x, y + other.y)
}

fun makeGrid(paths: List<List<Vec>>): MutableSet<Vec> {
  val grid = mutableSetOf<Vec>()
  for (path in paths) {
    var from = path.first()
    grid += from
    for (to in path.drop(1)) {
      var pos = to
      val delta = Vec((from.x - to.x).sign, (from.y - to.y).sign)
      while (pos != from) {
        grid += pos
        pos += delta
      }
      from = to
    }
  }
  return grid
}

fun main() {
  val paths = inputFile().readLines().map { line ->
    line.extractNumbers().chunked(2).map { (x, y) -> Vec(x, y) }
  }
  val start = Vec(500, 0)
  val deltas = listOf(Vec(0, 1), Vec(-1, 1), Vec(1, 1))
  part1 {
    val grid = makeGrid(paths)
    val numRocks = grid.size
    val bottom = grid.maxOf { (_, y) -> y }
    while (true) {
      var pos = start
      while (pos.y < bottom) {
        val delta = deltas.firstOrNull { d -> pos + d !in grid } ?: break
        pos += delta
      }
      if (pos.y == bottom) break
      grid += pos
    }
    grid.size - numRocks
  }
  part2 {
    val grid = makeGrid(paths)
    val numRocks = grid.size
    val bottom = grid.maxOf { (_, y) -> y } + 1
    while (start !in grid) {
      var pos = start
      while (pos.y < bottom) {
        val delta = deltas.firstOrNull { d -> pos + d !in grid } ?: break
        pos += delta
      }
      grid += pos
    }
    grid.size - numRocks
  }
}