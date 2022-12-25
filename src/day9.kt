package day9

import util.*
import kotlin.math.*

data class Pos(val x: Int, val y: Int) {
  fun move(dir: Char): Pos =
    when (dir) {
      'R' -> copy(x = x + 1)
      'L' -> copy(x = x - 1)
      'U' -> copy(y = y + 1)
      'D' -> copy(y = y - 1)
      else -> throw IllegalArgumentException("invalid direction")
    }

  fun follow(head: Pos): Pos =
    if (max(abs(head.x - x), abs(head.y - y)) >= 2)
      Pos(x + (head.x - x).sign, y + (head.y - y).sign)
    else
      this
}

fun main() {
  val moves = inputFile().readLines().map {
    it.split(' ').let { (dir, steps) -> dir[0] to steps.toInt() }
  }
  part1 {
    var head = Pos(0, 0)
    var tail = Pos(0, 0)
    val positions = mutableSetOf<Pos>()
    for ((dir, steps) in moves) {
      repeat(steps) {
        head = head.move(dir)
        tail = tail.follow(head)
        positions += tail
      }
    }
    positions.size
  }
  part2 {
    val rope = MutableList(10) { Pos(0, 0) }
    val positions = mutableSetOf<Pos>()
    for ((dir, steps) in moves) {
      repeat(steps) {
        rope[0] = rope[0].move(dir)
        for (i in 1 until rope.size)
          rope[i] = rope[i].follow(rope[i - 1])
        positions += rope.last()
      }
    }
    positions.size
  }
}