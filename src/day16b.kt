package day16b

import util.*
import kotlin.math.*

@JvmInline
value class Valve(val id: Int) {
  private companion object {
    val valveCache = mutableMapOf<String, Int>()
    var valveId = 0
  }

  constructor(name: String) : this(valveCache.computeIfAbsent(name) { valveId++ })

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

fun main() {
  val maze = inputFile().readLines().map { line ->
    rxData.findAll(line).map { it.value }.toList()
  }.associate {
    Valve(it[0]) to Connection(
      it[1].toInt(),
      Valve(it[0]),
      it.drop(2).map(::Valve)
    )
  }
  val startTime = 30
  val directConnections = findDirectConnections(maze)
  part1 {
    // DFS is faster than BFS here by a fair margin.
    val stack = Deque<State>()
    val seen = mutableMapOf<Pair<Valve, Bits>, Int>()
    stack += State(0, startTime, Valve("AA"), Bits())
    var best = 0
    while (stack.isNotEmpty()) {
      val (flow, time, valve, opened) = stack.removeLast()
      val key = Pair(valve, opened)
      if (key in seen && seen[key]!! >= flow) continue
      seen[key] = flow
      val room = directConnections[valve]!!
      best = max(best, flow)
      if (time == 0)
        continue
      for ((next, dist) in room.to) {
        val newTime = time - dist - 1
        if (newTime >= 0 && next !in opened) {
          val conn = directConnections[next]!!
          val newFlow = flow + conn.flow * newTime
          stack += State(newFlow, newTime, next, opened + next)
        }
      }
    }
    best
  }
  part2 {
    val pq = Deque<State>()
    val seen = mutableMapOf<Pair<Valve, Bits>, Int>()
    pq += State(0, startTime - 4, Valve("AA"), Bits())
    while (pq.isNotEmpty()) {
      val state = pq.removeLast()
      val (flow, time, valve, opened) = state
      val key = Pair(valve, opened)
      if (key in seen && seen[key]!! >= flow) continue
      seen[key] = flow
      val room = directConnections[valve]!!
      if (time == 0)
        continue
      for ((next, dist) in room.to) {
        val newTime = time - dist - 1
        if (newTime >= 0 && next !in opened) {
          val conn = directConnections[next]!!
          val newFlow = flow + conn.flow * newTime
          pq += State(newFlow, newTime, next, opened + next)
        }
      }
    }
    val best = seen.map { (state, flow) -> state.second to flow }
      .groupBy({ it.first }, { it.second }).mapValues { it.value.max() }
    best.maxOf { (opened1, flow1) ->
      best.maxOf { (opened2, flow2) ->
        if ((opened1 intersect opened2).isEmpty()) flow1 + flow2 else 0
      }
    }
  }
}
