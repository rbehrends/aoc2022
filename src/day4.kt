package day4

import util.*

infix fun IntRange.subrangeOf(other: IntRange) =
  first >= other.first && last <= other.last

infix fun IntRange.overlaps(other: IntRange) =
  first in other || last in other || other.first in this || other.last in this

fun main() {
  val assignments = inputFile().readLines().map {
    it.extractNumbers().let { (a1, b1, a2, b2) -> Pair(a1..b1, a2..b2) }
  }
  part1 {
    assignments.count { (r1, r2) -> r1 subrangeOf r2 || r2 subrangeOf r1 }
  }
  part2 {
    assignments.count { (r1, r2) -> r1 overlaps r2 }
  }
}