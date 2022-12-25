package day11

import util.*

val operations = mapOf<Char, (Long, Long) -> Long>(
  '+' to { a, b -> a + b },
  '-' to { a, b -> a - b },
  '*' to { a, b -> a * b },
)

data class Operation(val op: Char, val arg1: Long?, val arg2: Long?) {
  fun update(worry: Long): Long =
    operations[op]!!(arg1 ?: worry, arg2 ?: worry)
}

data class Test(val mod: Long, val ifTrue: Int, val ifFalse: Int) {
  fun target(worry: Long): Int =
    if (worry % mod == 0L) ifTrue else ifFalse
}

data class Monkey(
  val id: Int,
  val items: MutableList<Long>,
  val op: Operation,
  val test: Test,
  var inspections: Long = 0L
)

fun parseRecord(record: List<String>): Monkey {
  val id = record[0].extractNumbers().first()
  val items = record[1].extractNumbers().map { it.toLong() }.toMutableList()
  val testMod = record[3].extractNumbers().first().toLong()
  val ifTrue = record[4].extractNumbers().first()
  val ifFalse = record[5].extractNumbers().first()
  val test = Test(mod = testMod, ifTrue = ifTrue, ifFalse = ifFalse)
  val (opArg1, opName, opArg2) = record[2].trim().split(Regex("\\s+")).drop(3)
  val op = Operation(opName[0], opArg1.toLongOrNull(), opArg2.toLongOrNull())
  return Monkey(id, items, op, test)
}

fun round(monkeys: List<Monkey>, adjust: (Long) -> Long) {
  for (monkey in monkeys) {
    with(monkey) {
      for (item in items) {
        inspections += 1
        val updatedItem = adjust(op.update(item))
        monkeys[test.target(updatedItem)].items += updatedItem
      }
      items.clear()
    }
  }
}

fun parseInputFile() =
  inputFile().readRecords().map { parseRecord(it.lines()) }

fun main() {
  part1 {
    val monkeys = parseInputFile()
    repeat(20) {
      round(monkeys) { x -> x / 3 }
    }
    monkeys.map { it.inspections }.sortedDescending().let { (a, b) -> a * b }
  }
  part2 {
    val monkeys = parseInputFile()
    val mod = monkeys.map { it.test.mod }.fold(1, Long::times)
    repeat(10_000) {
      round(monkeys) { x -> x % mod }
    }
    monkeys.map { it.inspections }.sortedDescending().let { (a, b) -> a * b }
  }
}