package day16a

import util.*
import kotlin.math.*

@JvmInline
value class Valve(val id: Int) {
  private companion object {
    val valveCache = mutableMapOf<String, Int>()
    var valveId = 0
  }
  constructor(name: String): this(valveCache.computeIfAbsent(name) { valveId++ })
  override fun toString(): String {
    return valveCache.keys.firstOrNull { valveCache[it]!! == id } ?: "??"
  }
}

operator fun Bits.contains(valve: Valve): Boolean = contains(valve.id)
operator fun Bits.plus(valve: Valve): Bits = plus(valve.id)

data class Connection(val flow: Int, val from: Valve, val to: List<Valve>)

data class DirectConnection(
  val flow: Int,
  val from: Valve,
  val to: List<Pair<Valve, Int>>
)

data class State(
  val flow: Int,
  val time: Int,
  val valve: Valve,
  val opened: Bits
)

data class State2(
  val flow: Int,
  val time1: Int,
  val time2: Int,
  val valve1: Valve,
  val valve2: Valve,
  val opened: Bits
)

val rxData = Regex("""\d+|[A-Z][A-Z]""")

fun findDirectConnections(maze: Map<Valve, Connection>): Map<Valve, DirectConnection> {
  val result = mutableMapOf<Valve, DirectConnection>()
  for ((valve, conn) in maze) {
    // ad-hoc BFS
    val dest = mutableListOf<Pair<Valve, Int>>()
    val seen = mutableSetOf<Valve>()
    val queue = Deque<Pair<Valve, Int>>()
    queue.add(valve to 0)
    while (queue.isNotEmpty()) {
      val next = queue.removeFirst()
      val (from, dist) = next
      if (from in seen) continue
      dest.add(next)
      seen += from
      val conn2 = maze[from]!!
      for (to in conn2.to)
        queue.addLast(to to dist + 1)
    }
    result[valve] = DirectConnection(conn.flow, conn.from,
      dest.filter { (v, _) -> v != valve && maze[v]!!.flow > 0 })
  }
  return result
}

fun findDistances(connections: Map<Valve, DirectConnection>): Map<Valve, Int> {
  val result = mutableMapOf<Valve, Int>()
  for (conn in connections.values) {
    if (conn.flow == 0) continue
    for ((valve, dist) in conn.to) {
      if (connections[valve]!!.flow == 0) continue
      result[valve] = min(result[valve] ?: dist, dist)
    }
  }
  return result
}

fun main() {
  val maze = inputFile().readLines().map { line ->
    rxData.findAll(line).map { it.value }.toList()
  }.associate {
    Valve(it[0]) to Connection(it[1].toInt(), Valve(it[0]), it.drop(2).map(::Valve))
  }
  val startTime = 30
  val directConnections = findDirectConnections(maze)
  // Getting a good bound for h(x) in A* matters a lot for performance.
  val distances = findDistances(directConnections)
  // flows is a sorted list of the per-valve flows, with as many zeroes
  // interspersed between two values as it takes steps to get from that
  // valve to the next valve with a non-zero flow.
  val flows = distances.map { (k, v) -> directConnections[k]!!.flow to v }
    .sortedByDescending { it.first }
    .flatMap { listOf(it.first) + List(it.second) { 0 } }
    .toMutableList()
    .apply { repeat(max(0, startTime - size)) { add(0) } }
  // flowBounds[n] is an upper bound for the total flow that can possibly
  // be achieved in n steps.
  val flowBounds = List(startTime * 2) { n ->
    flows.take(n).mapIndexed { i, b -> b * (n - i) }.sum()
  }
  part1 part1@{
    val pq = PriorityQueue<State>(1,
      compareBy { -it.flow - flowBounds[it.time] })
    val seen = mutableMapOf<Pair<Valve, Bits>, Int>()
    pq += State(0, startTime, Valve("AA"), Bits())
    while (true) {
      val state = pq.remove()
      val (flow, time, valve, opened) = state
      val key = Pair(valve, opened)
      if (key in seen && seen[key]!! >= flow) continue
      seen[key] = flow
      val room = directConnections[valve]!!
      if (time == 0)
        return@part1 flow
      for ((next, dist) in room.to) {
        val newTime = time - dist - 1
        pq += if (newTime >= 0 && next !in opened) {
          val dest = directConnections[next]!!
          val newFlow = flow + dest.flow * newTime
          State(newFlow, newTime, next, opened + next)
        } else {
          State(flow, 0, next, opened)
        }
      }
    }
  }
  part2 part2@{
    val pq = PriorityQueue<State2>(1,
      compareBy { -it.flow - flowBounds[it.time1 + it.time2] })
    val seen = mutableMapOf<Triple<Valve, Valve, Bits>, Int>()
    pq += State2(0, startTime - 4, startTime - 4, Valve("AA"), Valve("AA"), Bits())
    while (true) {
      val state = pq.remove()
      val (flow, time1, time2, valve1, valve2, opened) = state
      val key = Triple(valve1, valve2, opened)
      if (key in seen && seen[key]!! >= flow) continue
      seen[key] = flow
      val room1 = directConnections[valve1]!!
      val room2 = directConnections[valve2]!!
      if (time1 == 0 && time2 == 0)
        return@part2 flow
      if (time1 > time2) {
        for ((next, dist) in room1.to) {
          val newTime = time1 - dist - 1
          pq += if (newTime >= 0 && next !in opened) {
            val conn = directConnections[next]!!
            val newFlow = flow + conn.flow * newTime
            State2(newFlow, newTime, time2, next, valve2, opened + next)
          } else {
            State2(flow, 0, time2, next, valve2, opened)
          }
        }
      } else {
        for ((next, dist) in room2.to) {
          val newTime = time2 - dist - 1
          pq += if (newTime >= 0 && next !in opened) {
            val dest = directConnections[next]!!
            val newFlow = flow + dest.flow * newTime
            State2(newFlow, time1, newTime, valve1, next, opened + next)
          } else {
            State2(flow, time1, 0, valve1, next, opened)
          }
        }
      }
    }
  }
}

