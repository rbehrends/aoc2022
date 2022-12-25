package day6

import util.*

fun findMarker(signal: String, size: Int): Int =
  signal.windowed(size).indexOfFirst { it.toSet().size == size } + size

fun main() {
  val signal = inputFile().readText().trim()
  part1 { findMarker(signal, 4) }
  part2 { findMarker(signal, 14) }
}