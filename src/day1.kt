package day1

import util.*

fun main() {
  val calories =
    inputFile().readRecords().map { rec -> rec.extractNumbers().sum() }
  part1 { calories.max() }
  part2 { calories.sortedDescending().take(3).sum() }
}