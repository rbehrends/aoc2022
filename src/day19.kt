package day19

import util.*
import kotlin.math.*

data class Vec(val ore: Int,
               val clay: Int,
               val obsidian: Int,
               val geodes: Int) {
  operator fun plus(other: Vec): Vec =
    Vec(
      ore + other.ore,
      clay + other.clay,
      obsidian + other.obsidian,
      geodes + other.geodes
    )

  operator fun minus(other: Vec): Vec =
    Vec(
      ore - other.ore,
      clay - other.clay,
      obsidian - other.obsidian,
      geodes - other.geodes
    )

  infix fun lessEqual(other: Vec): Boolean =
    ore <= other.ore && clay <= other.clay && obsidian <= other.obsidian && geodes <= other.geodes
}

fun findMaxGeodes(costs: List<Vec>, maxTime: Int): Int {
  data class State(val time: Int, val resources: Vec, val bots: Vec)

  val botsInc =
    listOf(Vec(1, 0, 0, 0), Vec(0, 1, 0, 0), Vec(0, 0, 1, 0), Vec(0, 0, 0, 1))
  val obsidianPerGeodeBot = costs.last().obsidian
  // Since we can produce only one bot per minute, we don't need to be able
  // to generate more ore than the most ore any single bot requires, which
  // is an upper limit on the number of ore bots required.
  val maxOreBots = costs.maxOf { it.ore }
  val stack = Deque<State>()
  var best = 0
  stack += State(maxTime, Vec(0, 0, 0, 0), Vec(1, 0, 0, 0))
  while (stack.isNotEmpty()) {
    val state = stack.removeLast()!!
    best = max(best, state.resources.geodes + state.time * state.bots.geodes)
    // Skip this if we cannot possibly generate another geode bot before time
    // runs out AND generate another geode from it.
    if (state.time <= state.resources.obsidian - obsidianPerGeodeBot) continue
    for (resourceType in costs.indices.reversed()) {
      var (time, resources, bots) = state
      if (resourceType == 0 && bots.ore >= maxOreBots) continue
      val resourceCosts = costs[resourceType]
      while (time > 1) {
        if (resourceCosts lessEqual resources) {
          resources += bots
          time--
          resources -= resourceCosts
          stack += State(time, resources, bots + botsInc[resourceType])
          break
        }
        resources += bots
        time--
      }
    }
  }
  return best
}

fun main() {
  val blueprints = inputFile().readLines().map {
    val nums = it.extractNumbers()
    val costs = listOf(
      Vec(nums[1], 0, 0, 0),
      Vec(nums[2], 0, 0, 0),
      Vec(nums[3], nums[4], 0, 0),
      Vec(nums[5], 0, nums[6], 0)
    )
    nums[0] to costs
  }
  part1 {
    blueprints.sumOf { (id, costs) -> id * findMaxGeodes(costs, 24) }
  }
  part2 {
    blueprints.take(3)
        .fold(1) { acc, (_, costs) -> acc * findMaxGeodes(costs, 32) }
  }
}