package day12

import util.*

data class Path(val length: Int, val pos: Pos)

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
  part1 part1@{
    val pq = PriorityQueue<Path>(1, compareBy { it.length })
    val distance = Grid(grid.rows, grid.columns, -1)
    pq.add(Path(0, start))
    while (pq.isNotEmpty()) {
      val (length, pos) = pq.remove()!!
      if (pos == end)
        return@part1 length
      if (distance[pos] in 0..length) continue
      distance[pos] = length
      val elevation = grid[pos]
      for (adj in grid.directAdjacent(pos.row, pos.col))
        if (grid[adj] <= elevation + 1)
          pq.add(Path(length + 1, adj))
    }
  }
  part2 part2@{
    val pq = PriorityQueue<Path>(1, compareBy { it.length })
    val distance = Grid(grid.rows, grid.columns, -1)
    pq.add(Path(0, end))
    while (pq.isNotEmpty()) {
      val (length, pos) = pq.remove()!!
      val elevation = grid[pos]
      if (elevation == 0)
        return@part2 length
      if (distance[pos] in 0..length) continue
      distance[pos] = length
      for (adj in grid.directAdjacent(pos.row, pos.col))
        if (grid[adj] >= elevation - 1)
          pq.add(Path(length + 1, adj))
    }
  }
}