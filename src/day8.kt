package day8

import util.*

// Cartesian product
operator fun <T> Iterable<T>.times(other: Iterable<T>): List<Pair<T, T>> =
  flatMap { row -> other.map { col -> row to col } }

fun main() {
  val input = inputFile().readLines()
  val grid = Grid(input.size, input[0].length) { r, c -> input[r][c] - '0' }
  part1 {
    val visible = Grid(grid.rows, grid.columns, false)
    fun check(coords: List<Pair<Int, Int>>) {
      var last = -1
      for ((row, col) in coords) {
        if (grid[row, col] > last)
          visible[row, col] = true
        if (grid[row, col] >= last)
          last = grid[row, col]
      }
    }
    for (row in 0 until grid.rows) {
      check((row..row) * (0 until grid.columns))
      check((row..row) * (0 until grid.columns).reversed())
    }
    for (col in 0 until grid.columns) {
      check((0 until grid.rows) * (col..col))
      check((0 until grid.rows).reversed() * (col..col))
    }
    (0 until grid.rows).sumOf { row ->
      (0 until grid.columns).count { col -> visible[row, col] }
    }
  }
  part2 {
    (0 until grid.rows).maxOf { row ->
      (0 until grid.columns).maxOf { col ->
        val current = grid[row, col]
        fun count(coords: List<Pair<Int, Int>>): Int {
          val dirScore =
            coords.indexOfFirst { (r, c) -> grid[r, c] >= current }
          return if (dirScore < 0) coords.size else dirScore + 1
        }
        count((row + 1 until grid.rows) * (col..col)) *
            count((row - 1 downTo 0) * (col..col)) *
            count((row..row) * (col + 1 until grid.columns)) *
            count((row..row) * (col - 1 downTo 0))
      }
    }
  }
}