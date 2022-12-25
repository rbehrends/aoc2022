package day10a

import util.*

// codegolfed alternative version

fun main() {
  var sum = 1
  val data = inputFile().readText().trim().split(Regex("\\s")).map {
    when (it) {
      "noop", "addx" -> sum
      else -> { sum.apply { sum += it.toInt() } }
    }
  }.withIndex()
  part1 {
    data.filter { (i, _) ->  i % 40 == 19 }.sumOf { (i, x) -> (i+1) * x }
  }
  part2 {
    val display = MutableList(6) { MutableList (40) { '.' } }
    for ((i, x) in data)
      if (i % 40 - x in -1 .. 1) display[i / 40 ][i % 40] = '#'
    display.joinToString("\n") { it.joinToString("") }
  }
}
