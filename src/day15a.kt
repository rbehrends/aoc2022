package day15a

import util.*
import kotlin.math.*

data class Vec(val x: Int, val y: Int) {
  operator fun plus(other: Vec): Vec = Vec(x + other.x, y + other.y)
  operator fun minus(other: Vec): Vec = Vec(x - other.x, y + other.y)
  fun distanceTo(other: Vec): Int = abs(x - other.x) + abs(y - other.y)
}

data class Area(val center: Vec, val size: Int) {
  operator fun contains(point: Vec): Boolean =
    point.distanceTo(center) <= size

  fun outsideBorder(): Sequence<Vec> = sequence {
    for (i in 0..size) {
      yield(center + Vec(i, size + 1 - i))
      yield(center + Vec(size + 1 - i, -i))
      yield(center + Vec(-i, -size - 1 + i))
      yield(center + Vec(-size - 1 + i, i))
    }
  }
}

typealias Segment = Pair<Int, Int>

fun mergeSegments(segments: List<Segment>): List<Segment> {
  val sorted = segments.sortedBy { it.first }
  val result = mutableListOf(sorted.first())
  for ((lo, hi) in sorted.drop(1)) {
    val (prevLo, prevHi) = result.last()
    if (lo <= prevHi + 1) {
      result.removeLast()
      result += min(lo, prevLo) to max(hi, prevHi)
    } else {
      result += lo to hi
    }
  }
  return result
}

fun buildSegments(pairs: List<Pair<Vec, Vec>>, targetRow: Int): List<Segment> {
  val segments = mutableListOf<Pair<Int, Int>>()
  for ((sensor, beacon) in pairs) {
    val dist = sensor.distanceTo(beacon)
    val spare = dist - abs(sensor.y - targetRow)
    if (spare >= 0) segments += (sensor.x - spare) to (sensor.x + spare)
  }
  return mergeSegments(segments)
}

fun makeArea(sensor: Vec, beacon: Vec): Area =
  Area(sensor, sensor.distanceTo(beacon))

fun tuningFrequency(x: Int, y: Int): Long = x * 4_000_000L + y

fun main() {
  val pairs = inputFile().readLines()
    .map { line -> line.extractSignedNumbers() }
    .map { (x1, y1, x2, y2) -> Vec(x1, y1) to Vec(x2, y2) }
  val gridSize = 4_000_000
  part1 {
    val targetRow = gridSize / 2
    val segments = buildSegments(pairs, targetRow)
    segments.sumOf { (lo, hi) -> hi - lo + 1 } -
        pairs.map { it.second }.toSet().count { (x, y) ->
          y == targetRow && segments.any { (lo, hi) -> x in lo..hi }
        }
  }
  part2 part2@{
    val areas = pairs.map { (sensor, beacon) -> makeArea(sensor, beacon) }
    for (area in areas) {
      for (point in area.outsideBorder()) {
        if (point.x in 0..gridSize
            && point.y in 0..gridSize
            && areas.all { area2 -> point !in area2 })
          return@part2 tuningFrequency(point.x, point.y)
      }
    }
  }
}

