package day18

import util.*

fun main() {
  val cubes = inputFile().readLines().map { it.extractNumbers() }
      .map { (x, y, z) -> Vector(x, y, z) }.toSet()
  val dx = Vector(1, 0, 0)
  val dy = Vector(0, 1, 0)
  val dz = Vector(0, 0, 1)
  val deltas = listOf(dx, dy, dz, -dx, -dy, -dz)
  part1 {
    cubes.sumOf { cube -> deltas.count { delta -> cube + delta !in cubes } }
  }
  part2 {
    val maxX = cubes.maxOf { it.x }
    val maxY = cubes.maxOf { it.y }
    val maxZ = cubes.maxOf { it.z }
    val stack = Deque<Vector>().also { it += Vector(-1, -1, -1) }
    val outside = mutableSetOf<Vector>()
    while (stack.isNotEmpty()) {
      val vec = stack.removeLast()
      if (vec in outside || vec in cubes) continue
      outside += vec
      if (vec.x >= 0) stack += vec - dx
      if (vec.x <= maxX) stack += vec + dx
      if (vec.y >= 0) stack += vec - dy
      if (vec.y <= maxY) stack += vec + dy
      if (vec.z >= 0) stack += vec - dz
      if (vec.z <= maxZ) stack += vec + dz
    }
    val all = (-1..maxX + 1).flatMap { x ->
      (-1..maxY + 1).flatMap { y ->
        (-1..maxZ + 1).map { z -> Vector(x, y, z) }
      }
    }.toSet()
    val cubes2 = all - outside
    cubes2.sumOf { cube -> deltas.count { delta -> cube + delta !in cubes2 } }
  }
}