package day22

import util.*

sealed class Move
data class Turn(val rot: Int) : Move()
data class Forward(val steps: Int) : Move()

val rxMove = Regex("""[A-Z]|[0-9]+""")
val facingDelta = listOf(Pos(0, 1), Pos(1, 0), Pos(0, -1), Pos(-1, 0))
val facingToEdges = listOf(
  Pos(0, 1) to Pos(1, 1),
  Pos(1, 1) to Pos(1, 0),
  Pos(1, 0) to Pos(0, 0),
  Pos(0, 0) to Pos(0, 1)
)
val deltaToFacing = facingDelta.withIndex().associate { (i, d) -> d to i }

fun password(pos: Pos, facing: Int): Int =
  1000 * (pos.row + 1) + 4 * (pos.col + 1) + facing

fun main() {
  val (mapData, moveData) = inputFile().readRecords()
  val mapLines = mapData.lines().toMutableList()
  val rows = mapLines.size
  val columns = mapLines.maxOf { it.length }
  val map = Grid(
    rows,
    columns
  ) { row, col -> if (col < mapLines[row].length) mapLines[row][col] else ' ' }
  val moves = rxMove.findAll(moveData).map {
    when (it.value[0]) {
      in '0'..'9' -> Forward(it.value.toInt())
      'L' -> Turn(-1)
      'R' -> Turn(1)
      else -> throw IllegalArgumentException("bad move: ${it.value}")
    }
  }
  part1 {
    var pos = Pos(0, map[0].indexOfFirst { it != ' ' })
    var facing = 0
    for (move in moves) {
      when (move) {
        is Turn -> facing = (facing + move.rot + 4) % 4
        is Forward -> {
          repeat(move.steps) loop@{
            val saved = pos
            val delta = facingDelta[facing]
            pos += delta
            if (pos !in map || map[pos] == ' ') {
              when (facing) {
                0 -> pos = Pos(pos.row, 0)
                1 -> pos = Pos(0, pos.col)
                2 -> pos = Pos(pos.row, columns - 1)
                3 -> pos = Pos(rows - 1, pos.col)
              }
              while (map[pos] == ' ') pos += delta
              if (map[pos] == '#')
                pos = saved
            } else {
              if (map[pos] == '#')
                pos = saved
            }
          }
        }
      }
    }
    password(pos, facing)
  }
  part2 {
    // faces of the die are represented as coordinates in a compressed grid.
    val faceSize = (0 until rows).minOf { row -> map[row].count { it != ' ' } }
    val faceColumns = columns / faceSize
    val faceRows = rows / faceSize
    val faceMap = Grid(
      faceRows,
      faceColumns
    ) { row, col -> map[row * faceSize, col * faceSize] != ' ' }
    val faceConnections = mutableMapOf<Pos, MutableList<Pos?>>()
    // step 1: connect directly adjacent faces
    for (row in 0 until faceRows)
      for (col in 0 until faceColumns)
        if (faceMap[row, col])
          faceConnections[Pos(row, col)] = MutableList(4) { null }
    for (pos in faceConnections.keys) {
      if (!faceMap[pos]) continue
      for (adj in faceMap.directAdjacent(pos.row, pos.col)) {
        if (faceMap[adj]) {
          val facing = deltaToFacing[adj - pos]!!
          faceConnections[pos]!![facing] = adj
          faceConnections[adj]!![(facing + 2) % 4] = pos
        }
      }
    }
    // step 2: connect remaining edges through folding
    var updated = true
    while (updated) {
      updated = false
      for ((face, adjList) in faceConnections) {
        for (dir in adjList.indices) {
          if (adjList[dir] != null) continue
          // We try if we can "fold" the next two faces so that their edges
          // become adjacent.
          //
          // example:
          // face = F, adjFace = A, linkedFace = L
          // dir = 0, invDir = 3
          //
          // F.
          // AL
          //
          // After folding F back and down and L back and left, they share
          // the right edge of F and the upper edge of L.
          val adjFace = adjList[(dir + 1) % 4] ?: continue
          // invDir = direction from adjFace back to face.
          // We can't just use (dir + 2) % 4 to go back because this may no
          // longer hold during later iterations once the cube has become
          // partially folded.
          val invDir = faceConnections[adjFace]!!.indexOf(face)
          // (invDir + rot) % 4 = direction from adjFace to linkedFace as we
          // rotate 90 degrees further.
          val linkedFace =
            faceConnections[adjFace]!![(invDir + 1) % 4] ?: continue
          // backDir = direction from linkedFace to adjFace
          val backDir = faceConnections[linkedFace]!!.indexOf(adjFace)
          updated = true
          adjList[dir] = linkedFace
          faceConnections[linkedFace]!![(backDir + 1) % 4] = face
        }
      }
    }
    // step 3: Walk the cube
    val corners = listOf(
      Pos(0, faceSize - 1), Pos(faceSize - 1, faceSize - 1),
      Pos(faceSize - 1, 0), Pos(0, 0)
    )
    var pos = Pos(0, map[0].indexOfFirst { it != ' ' })
    var facing = 0
    for (move in moves) {
      when (move) {
        is Turn -> facing = (facing + move.rot + 4) % 4
        is Forward -> {
          repeat(move.steps) {
            val saved = pos
            val delta = facingDelta[facing]
            pos += delta
            if (pos !in map || map[pos] == ' ') {
              val face = Pos(saved.row / faceSize, saved.col / faceSize)
              val relPos = Pos(saved.row % faceSize, saved.col % faceSize)
              val corner = corners[facing]
              val offset = corner manhattanDistance relPos
              val nextFace = faceConnections[face]!![facing]!!
              val newFacing =
                (faceConnections[nextFace]!!.indexOf(face) + 2) % 4
              val (start, end) = facingToEdges[(newFacing + 2) % 4]
              // Counting from end, because we've flipped the edge around.
              pos = end * (faceSize - 1) + (start - end) * offset
              pos += nextFace * faceSize
              if (map[pos] == '#')
                pos = saved
              else
                facing = newFacing
            } else if (map[pos] == '#') {
              pos = saved
            }
          }
        }
      }
    }
    password(pos, facing)
  }
}