package day6a

import util.*

fun findMarker(signal: String, size: Int): Int {
  var bits = 0
  fun update(i: Int) {
    bits = bits xor (1 shl (signal[i] - 'a'))
  }
  for (i in 0 until size) update(i)
  for (i in size until signal.length) {
    if (bits.countOneBits() == size) return i
    update(i)
    update(i - size)
  }
  if (bits.countOneBits() == size && size <= signal.length)
    return signal.length
  throw IllegalStateException("no marker found")
}

fun main() {
  val signal = inputFile().readText().trim()
  part1 { findMarker(signal, 4) }
  part2 { findMarker(signal, 14) }
}