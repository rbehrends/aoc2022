package util

import java.io.File
import java.math.BigInteger
import kotlin.math.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias PriorityQueue<T> = java.util.PriorityQueue<T>
typealias BitSet = java.util.BitSet
typealias Deque<T> = java.util.ArrayDeque<T>

data class Pos(val row: Int, val col: Int) {
  override fun toString(): String = "($row, $col)"
  operator fun plus(other: Pos): Pos = Pos(row + other.row, col + other.col)
  operator fun minus(other: Pos): Pos = Pos(row - other.row, col - other.col)
  operator fun times(scale: Int): Pos = Pos(row * scale, col * scale)
  infix fun manhattanDistance(other: Pos): Int =
    max((row - other.row).absoluteValue, (col - other.col).absoluteValue)
}

data class Vector(val x: Int, val y: Int, val z: Int) {
  operator fun plus(other: Vector): Vector =
    Vector(x + other.x, y + other.y, z + other.z)
  operator fun minus(other: Vector): Vector =
    Vector(x - other.x, y - other.y, z - other.z)
  operator fun unaryMinus(): Vector = Vector(-x, -y, -z)
}

operator fun String.get(r: IntRange): String = slice(r)

private val numRegex = Regex("""\d+""")
private val signedNumRegex = Regex("""-?\d+""")

fun String.extractNumbers(): List<Int> =
  numRegex.findAll(this).map { it.value.toInt() }.toList()

fun String.extractSignedNumbers(): List<Int> =
  signedNumRegex.findAll(this).map { it.value.toInt() }.toList()

fun File.readRecords(): List<String> =
  readText().split("\n\n")

fun currentFile(): String {
  val stackTrace = Thread.currentThread().stackTrace
  for (i in 1 until stackTrace.size) {
    val frame = stackTrace[i]
    val file = frame.fileName
    if (file.startsWith("day") && file.endsWith(".kt"))
      return file
  }
  throw IllegalStateException("stack trace does not contain day*.kt file")
}

fun day(): String = numRegex.find(currentFile())!!.value

fun inputFile(): File {
  val envFile = System.getenv("AOC_INPUT_FILE")
  return File(envFile ?: "input/input${day()}.txt")
}

fun testFile(): File = File("input/test${day()}.txt")

fun <T> part1(timed: Boolean = true, body: () -> T) = part(1, timed, body)
fun <T> part2(timed: Boolean = true, body: () -> T) = part(2, timed, body)

@OptIn(ExperimentalTime::class)
fun <T> part(n: Int, timed: Boolean = true, body: () -> T) {
  var result: String
  if (timed) {
    val time = measureTime {
      result = body().toString()
    }
    if ('\n' in result)
      println("part ${n}: (${time})\n${result}")
    else
      println("part ${n}: ${result} (${time})")
  } else {
    result = body.toString()
    println("part ${n}:${if ('\n' in result) "\n" else " "}${body()}")
  }
}

data class Path<T>(val node: T, val length: Int, val prev: Path<T>? = null) {
  fun path(): List<T> {
    val result = mutableListOf<T>()
    var current: Path<T>? = this
    while (current != null) {
      result.add(current.node)
      current = current.prev
    }
    result.reverse()
    return result
  }
}

// Dijkstra's algorithm
fun <T> shortestPath(
  start: T,
  until: (T) -> Boolean,
  neighbors: (T) -> Iterable<T>
): Path<T> {
  val pq = PriorityQueue<Path<T>>(1, compareBy { it.length })
  pq.add(Path(start, 0, null))
  val distance = mutableMapOf<T, Int>()
  while (pq.isNotEmpty()) {
    val path = pq.remove()!!
    if (path.node in distance && path.length >= distance[path.node]!!)
      continue
    distance[path.node] = path.length
    if (until(path.node))
      return path
    for (adj in neighbors(path.node)) {
      pq += Path(adj, path.length + 1, path)
    }
  }
  throw IllegalStateException("path not found")
}

typealias BigInt = BigInteger

fun Int.toBigInt(): BigInt = BigInt.valueOf(toLong())
fun Long.toBigInt(): BigInt = BigInt.valueOf(this)

data class Rational(val num: BigInt, val denom: BigInt) {
  constructor(num: Long, denom: Long):
      this(num.toBigInt(), denom.toBigInt())
  constructor(num: Int, denom: Int):
      this(num.toBigInt(), denom.toBigInt())
  constructor(num: Int): this(num, 1)
  constructor(num: Long): this(num, 1L)
  override fun toString(): String =
    if (denom == 1.toBigInt()) "$num" else "$num/$denom"
  fun reduce(): Rational =
    num.gcd(denom).let { Rational(num/it, denom/it) }
        .let { if (it.denom.signum() < 0) Rational(-it.num, -it.denom) else it}
  operator fun plus(other: Rational): Rational =
    Rational(num * other.denom + denom * other.num,
      denom * other.denom).reduce()
  operator fun minus(other: Rational): Rational =
    Rational(num * other.denom - denom * other.num,
      denom * other.denom).reduce()
  operator fun times(other: Rational): Rational =
    Rational(num * other.num, denom * other.denom).reduce()
  operator fun div(other: Rational): Rational =
    Rational(num * other.denom, denom * other.num).reduce()
  operator fun unaryMinus(): Rational =
    Rational(-num, denom)
}

fun Int.toRational(): Rational = Rational(this, 1)
fun Long.toRational(): Rational = Rational(this, 1)
//fun Int.gcd(other: Int): Int = gcd(this, other)
//fun Int.lcm(other: Int): Int = lcm(this, other)
//fun Long.gcd(other: Long): Long = gcd(this, other)
//fun Long.lcm(other: Long): Long = lcm(this, other)

tailrec fun gcd(a: Int, b: Int): Int =
  if (b == 0) a else gcd(b, a % b)

tailrec fun gcd(a: Long, b: Long): Long =
  if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b
