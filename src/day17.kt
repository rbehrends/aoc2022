package day17

import util.*

val rocks = listOf(
  listOf(
    0b0011110
  ),
  listOf(
    0b0001000,
    0b0011100,
    0b0001000,
  ),
  listOf(
    0b0011100,
    0b0000100,
    0b0000100,
  ),
  listOf(
    0b0010000,
    0b0010000,
    0b0010000,
    0b0010000,
  ),
  listOf(
    0b0011000,
    0b0011000,
  )
)

fun canShift(rock: List<Int>, dir: Int): Boolean = when {
  dir < 0 -> rock.all { p -> (p and 0b1000000) == 0 }
  dir > 0 -> rock.all { p -> (p and 0b0000001) == 0 }
  else -> true
}

fun intersect(cave: List<Int>, rock: List<Int>, pos: Int): Boolean =
  rock.indices.any { i -> rock[i] and cave[pos + i] != 0 }

fun shift(rock: List<Int>, dir: Int): List<Int> =
  when {
    dir < 0 -> if (canShift(rock, dir)) rock.map { p -> p shl 1 } else rock
    dir > 0 -> if (canShift(rock, dir)) rock.map { p -> p shr 1 } else rock
    else -> rock
  }

fun place(cave: MutableList<Int>, rock: List<Int>, row: Int) {
  for (i in rock.indices)
    cave[row + i] = cave[row + i] or rock[i]
  while (cave.last() == 0)
    cave.removeLast()
}

fun main() {
  val jets = inputFile().readText().trim()
  part1 {
    val numRocks = 2022
    val cave = mutableListOf<Int>()
    var jetIndex = 0
    for (i in 0 until numRocks) {
      var rock = rocks[i % rocks.size]
      cave += listOf(0, 0, 0)
      var row = cave.size
      repeat(rock.size) { cave += 0 }
      while (true) {
        val dir = if (jets[jetIndex] == '<') -1 else 1
        jetIndex = (jetIndex + 1) % jets.length
        val shiftedRock = shift(rock, dir)
        if (!intersect(cave, shiftedRock, row))
          rock = shiftedRock
        if (row == 0 || intersect(cave, rock, row - 1)) {
          place(cave, rock, row)
          break
        }
        row--
      }
    }
    cave.size
  }
  part2 {
    class State(var counter: Int, var dropped: Int, var rows: Int)
    val numRocks = 1000000000000L
    val cave = mutableListOf<Int>()
    var jetIndex = 0
    var rockCounter = 0
    var skippedRows = 0L
    var totalRocks = 0L
    val tracker = List(jets.length) { List(rocks.size) { State(0, 0, 0) } }
    while (totalRocks++ < numRocks) {
      var rock = rocks[rockCounter]
      rockCounter = (rockCounter + 1) % rocks.size
      cave += listOf(0, 0, 0)
      var row = cave.size
      repeat(rock.size) { cave += 0 }
      while (true) {
        val dir = if (jets[jetIndex] == '<') -1 else 1
        jetIndex = (jetIndex + 1) % jets.length
        val shiftedRock = shift(rock, dir)
        if (!intersect(cave, shiftedRock, row))
          rock = shiftedRock
        if (row == 0 || intersect(cave, rock, row - 1)) {
          place(cave, rock, row)
          break
        }
        row--
      }
      if (skippedRows == 0L) {
        val state = tracker[jetIndex][rockCounter]
        if (++state.counter >= 2) {
          val rocksInSegment = (totalRocks - state.dropped).toInt()
          val rowsGained = cave.size - state.rows
          if (rowsGained <= state.rows && (1..rocksInSegment).all {
                i -> cave[cave.size - i] == cave[state.rows - i]
            }) {
            val segmentsSkipped = (numRocks - totalRocks) / rocksInSegment
            totalRocks += segmentsSkipped * rocksInSegment
            skippedRows = segmentsSkipped * rowsGained
            continue
          }
        }
        state.rows = cave.size
        state.dropped = totalRocks.toInt()
      }
    }
    skippedRows + cave.size
  }
}