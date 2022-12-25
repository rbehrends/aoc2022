package day7a

// Improves on the original solution by ensuring worst case time
// complexity that is linear in the number of files and directories.

import util.*

const val smallDirSize = 100000
const val totalSpace = 70000000
const val neededSpace = 30000000

sealed class Node

class File(val size: Int) : Node()

class Dir(
  val parent: Dir?,
  val entries: MutableMap<String, Node> = mutableMapOf()
) : Node() {
  fun addDir(name: String): Dir =
    Dir(parent = this).also { entries[name] = it }

  fun addFile(name: String, size: Int): File =
    File(size = size).also { entries[name] = it }

  fun dirSizes(): List<Int> {
    val result = mutableListOf<Int>()
    fun accumulateDirSizes(dir: Dir) {
      result += dir.entries.values.sumOf {
        when (it) {
          is File -> it.size
          is Dir -> {
            accumulateDirSizes(it)
            result.last()
          }
        }
      }
    }
    accumulateDirSizes(this)
    return result
  }
}

fun sizeOfSmallDirs(root: Dir): Int =
  root.dirSizes().filter { it <= smallDirSize }.sum()

fun findDirToDelete(root: Dir): Int {
  val dirSizes = root.dirSizes()
  val totalSize = dirSizes.max()
  return dirSizes.filter { totalSize - it <= totalSpace - neededSpace }.min()
}

fun parseTerminalOutput(output: List<String>): Dir {
  val root = Dir(null)
  var cwd = root
  for (line in output) {
    if (line.startsWith("$ cd ")) {
      cwd = when (val dirname = line.drop(5)) {
        "/" -> root
        ".." -> cwd.parent!!
        else -> cwd.addDir(dirname)
      }
    } else if (line == "$ ls" || line.startsWith("dir ")) {
      // skip
    } else {
      val (size, name) = line.split(' ')
      cwd.addFile(name, size.toInt())
    }
  }
  return root
}

fun main() {
  val lines = inputFile().readLines()
  val root = parseTerminalOutput(lines)
  part1 { sizeOfSmallDirs(root) }
  part2 { findDirToDelete(root) }
}