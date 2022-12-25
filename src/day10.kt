package day10

import util.*

data class CPU(var regX: Int = 1, var cycle: Int = 0)

sealed class Instruction {
  abstract val cycles: Int
  abstract fun execute(cpu: CPU)
}

object Noop : Instruction() {
  override val cycles = 1
  override fun execute(cpu: CPU) {}
}

class AddX(val x: Int) : Instruction() {
  override val cycles = 2
  override fun execute(cpu: CPU) {
    cpu.regX += x
  }
}

fun main() {
  val program = inputFile().readLines().map {
    val code = it.split(' ')
    when (code[0]) {
      "noop" -> Noop
      "addx" -> AddX(code[1].toInt())
      else -> throw IllegalStateException("bad command")
    }
  }
  part1 {
    val interval = 40
    val cpu = CPU()
    var signal = 0
    var checkpoint = interval / 2
    for (instr in program) {
      cpu.cycle += instr.cycles
      if (cpu.cycle >= checkpoint) {
        signal += checkpoint * cpu.regX
        if (checkpoint >= interval * 11 / 2)
          break
        checkpoint += interval
      }
      instr.execute(cpu)
    }
    signal
  }
  part2 {
    val rows = 6
    val columns = 40
    val display = MutableList(rows) { MutableList(columns) { '.' } }
    val cpu = CPU()
    outer@ for (instr in program) {
      for (i in 1..instr.cycles) {
        val row = cpu.cycle / columns
        if (row >= display.size)
          break@outer
        val col = cpu.cycle % columns
        if (col in cpu.regX - 1..cpu.regX + 1) {
          display[row][col] = '#'
        }
        cpu.cycle += 1
      }
      instr.execute(cpu)
    }
    display.joinToString("\n") { row -> row.joinToString("") }
  }
}