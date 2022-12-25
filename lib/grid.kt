package util

class Grid<T>(val rows: Int, val columns: Int, val init: (Int, Int) -> T) {
  constructor(rows: Int, columns: Int, defaultValue: T)
      : this(rows, columns, { _, _ -> defaultValue })

  fun toString(display: (T) -> String): String =
    grid.map { row -> row.map(display).joinToString("") }.joinToString("\n")

  override fun toString(): String = toString { it.toString() }

  val grid: MutableList<MutableList<T>> =
    MutableList(rows) { row ->
      MutableList(columns) { col -> init(row, col) }
    }

  operator fun get(row: Int, col: Int): T =
    grid[row][col]

  operator fun get(row: Int, col: Int, defaultValue: T): T =
    if (row < 0 || row >= rows || col < 0 || col >= columns)
      defaultValue
    else
      grid[row][col]

  operator fun get(pos: Pos): T =
    grid[pos.row][pos.col]

  operator fun get(row: Int): List<T> =
    grid[row]

  operator fun get(pos: Pos, defaultValue: T): T =
    get(pos.row, pos.col, defaultValue)

  operator fun set(row: Int, col: Int, value: T) {
    grid[row][col] = value
  }

  operator fun set(pos: Pos, value: T) {
    grid[pos.row][pos.col] = value
  }

  operator fun contains(pos: Pos): Boolean =
    pos.row in 0 until rows && pos.col in 0 until columns

  fun clone(): Grid<T> =
    Grid(rows, columns) { row, col -> grid[row][col] }

  fun adjacent(row: Int, col: Int): List<Pos> =
    MutableList(0) { Pos(0, 0) }.apply {
      for (r in row - 1..row + 1)
        for (c in col - 1..col + 1)
          if (r >= 0 && c >= 0 && r < rows && c < columns && (r != row || c != col))
            add(Pos(r, c))
    }

  fun adjacentValues(row: Int, col: Int): List<T> =
    MutableList(0) { init(0, 0) }.apply {
      for (r in row - 1..row + 1)
        for (c in col - 1..col + 1)
          if (r >= 0 && c >= 0 && r < rows && c < columns && (r != row || c != col))
            add(grid[r][c])
    }

  fun directAdjacent(row: Int, col: Int): List<Pos> =
    MutableList(0) { Pos(0, 0) }.apply {
      if (row > 0) add(Pos(row - 1, col))
      if (col > 0) add(Pos(row, col - 1))
      if (row + 1 < rows) add(Pos(row + 1, col))
      if (col + 1 < columns) add(Pos(row, col + 1))
    }

  fun directAdjacentValues(row: Int, col: Int): List<T> =
    MutableList(0) { init(0, 0) }.also {
      if (row > 0) it.add(grid[row - 1][col])
      if (col > 0) it.add(grid[row][col - 1])
      if (row + 1 < rows) it.add(grid[row + 1][col])
      if (col + 1 < columns) it.add(grid[row][col + 1])
    }
}
