package day12a

import util.*

// Refactored the shortest path algorithm and put it in util
// for reuse.

fun main() {
  val lines = inputFile().readLines()
  var start = Pos(0, 0)
  var end = Pos(0, 0)
  val grid = Grid(lines.size, lines[0].length) { row, col ->
    lines[row][col].let {
        when (it) {
          'S' -> { start = Pos(row, col); 'a' }
          'E' -> { end = Pos(row, col); 'z'}
          else -> it
        } - 'a'
    }
  }
  part1 {
    shortestPath(start, until = { it == end }) { (row, col) ->
      grid.directAdjacent(row, col).filter { adj ->
        grid[adj] - grid[row, col] <= 1
      }
    }.length
  }
  part2 {
    shortestPath(end, until = { grid[it] == 0 }) { pos ->
      grid.directAdjacent(pos.row, pos.col).filter { adj ->
        grid[pos] - grid[adj] <= 1
      }
    }.length
  }
}