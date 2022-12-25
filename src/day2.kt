package day2

import util.*

enum class Outcome(val score: Int) {
  Loss(0), Draw(3), Win(6)
}

enum class Item(val value: Int) {
  Rock(1), Paper(2), Scissors(3);

  fun scoreVs(other: Item): Int = (((value - other.value) + 4) % 3) * 3

  fun itemForOutcome(outcome: Outcome): Item =
    values().first { it.scoreVs(this) == outcome.score }
}

val itemMap = mapOf(
  "A" to Item.Rock,
  "B" to Item.Paper,
  "C" to Item.Scissors,
  "X" to Item.Rock,
  "Y" to Item.Paper,
  "Z" to Item.Scissors,
)

val outcomeMap = mapOf(
  "X" to Outcome.Loss,
  "Y" to Outcome.Draw,
  "Z" to Outcome.Win,
)

fun main() {
  val rounds = inputFile().readLines().map {
    it.split(' ')
  }
  part1 {
    val decoded = rounds.map { (a, b) -> Pair(itemMap[a]!!, itemMap[b]!!) }
    decoded.sumOf { (other, mine) -> mine.value + mine.scoreVs(other) }
  }
  part2 {
    val decoded = rounds.map { (a, b) -> Pair(itemMap[a]!!, outcomeMap[b]!!) }
    decoded.sumOf { (other, outcome) ->
      val mine = other.itemForOutcome(outcome)
      mine.value + mine.scoreVs(other)
    }
  }
}
