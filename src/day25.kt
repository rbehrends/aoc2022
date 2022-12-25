package day25

import util.*

fun parseSnafu(s: String): Long =
  s.fold(0L) { acc, d -> acc * 5L + "=-012".indexOf(d) - 2 }

fun Long.toSnafu(): String {
  var result = ""
  var num = this
  do {
    num += 2
    result += "=-012"[(num % 5).toInt()]
    num /= 5
  } while (num > 0)
  return result.reversed()
}

fun main() {
  val lines = inputFile().readLines()
  part1 {
    lines.map(::parseSnafu).sum().toSnafu()
  }
}