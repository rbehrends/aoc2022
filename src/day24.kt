package day24

import util.*

const val north: Byte = 1
const val south: Byte = 2
const val west: Byte = 4
const val east: Byte = 8

infix fun Byte.and(other: Byte): Byte = (toInt() and other.toInt()).toByte()
infix fun Byte.or(other: Byte): Byte = (toInt() or other.toInt()).toByte()
inline val Int.b get() = this.toByte()

val codes = mapOf(
  '.' to 0,
  '^' to north,
  'v' to south,
  '<' to west,
  '>' to east,
  '#' to -1,
)

fun next(a: Int, b: Int): Int = if (a + 2 == b) 1 else a + 1
fun prev(a: Int, b: Int): Int = if (a == 1) b - 2 else a - 1

fun nextRound(map: Grid<Byte>): Grid<Byte> {
  val rows = map.rows
  val columns = map.columns
  val result = Grid<Byte>(rows, columns, 0)
  fun update(row: Int, col: Int, dir: Byte) {
    result[row, col] = result[row, col] or dir
  }
  for (row in 0 until rows)
    for (col in 0 until columns) {
      val cell = map[row, col]
      if (cell < 0) {
        result[row, col] = -1; continue
      }
      if ((cell and north) != 0.b) update(prev(row, rows), col, north)
      if ((cell and south) != 0.b) update(next(row, rows), col, south)
      if ((cell and west) != 0.b) update(row, prev(col, columns), west)
      if ((cell and east) != 0.b) update(row, next(col, columns), east)
    }
  return result
}

fun makeMaps(map: Grid<Byte>): List<Grid<Byte>> {
  val numMaps = lcm(map.rows - 2, map.columns - 2)
  val mapList = mutableListOf(map)
  for (i in 1 until numMaps)
    mapList += nextRound(mapList.last())
  return mapList
}

fun findPath(start: Pos,
             end: Pos,
             mapList: List<Grid<Byte>>,
             startTime: Int): Int =
  shortestPath(
    Pair(start, (startTime + 1) % mapList.size),
    until = { it.first == end }, neighbors = { (pos, mapIndex) ->
      val currMap = mapList[mapIndex]
      if (currMap[pos] != 0.b)
        listOf()
      else
        (currMap.directAdjacent(pos.row, pos.col) + pos).map { newPos ->
          Pair(newPos, (mapIndex + 1) % mapList.size)
        }

    }).length + 1

fun main() {
  val map = inputFile().readLines().let { lines ->
    val rows = lines.size
    val columns = lines[0].length
    Grid(rows, columns) { row, column ->
      codes[lines[row][column]]!!
    }
  }
  part1 {
    val mapList = makeMaps(map)
    val start = Pos(0, 1)
    val end = Pos(map.rows - 1, map.columns - 2)
    findPath(start, end, mapList, 0)
  }
  part2 {
    val mapList = makeMaps(map)
    val start = Pos(0, 1)
    val end = Pos(map.rows - 1, map.columns - 2)
    val t1 = findPath(start, end, mapList, 0)
    val t2 = findPath(end, start, mapList, t1) + t1
    findPath(start, end, mapList, t2) + t2
  }
}
