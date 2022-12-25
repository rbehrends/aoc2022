package day2a

import util.*

val scoreMap1 = mapOf(
  "A X" to 1 + 3,
  "A Y" to 2 + 6,
  "A Z" to 3 + 0,
  "B X" to 1 + 0,
  "B Y" to 2 + 3,
  "B Z" to 3 + 6,
  "C X" to 1 + 6,
  "C Y" to 2 + 0,
  "C Z" to 3 + 3,
)

val scoreMap2 = mapOf(
  "A X" to 3 + 0,
  "A Y" to 1 + 3,
  "A Z" to 2 + 6,
  "B X" to 1 + 0,
  "B Y" to 2 + 3,
  "B Z" to 3 + 6,
  "C X" to 2 + 0,
  "C Y" to 3 + 3,
  "C Z" to 1 + 6,
)

fun main() {
  val rounds = inputFile().readLines()
  part1 { rounds.sumOf { scoreMap1[it]!! } }
  part2 { rounds.sumOf { scoreMap2[it]!! } }
}
