package util

@JvmInline
value class Bits(val bits: Long = 0L) {
  companion object {
    private fun allBits(iter: Iterable<Int>): Long =
      iter.fold(0L) { a, b -> a or (1L shl b) }
  }

  constructor(iter: Iterable<Int>) : this(allBits(iter))

  operator fun contains(bit: Int): Boolean = (bits and (1L shl bit)) != 0L
  operator fun plus(bit: Int): Bits = Bits(bits or (1L shl bit))
  operator fun minus(bit: Int): Bits = Bits(bits and (1L shl bit).inv())
  infix fun union(other: Bits): Bits = Bits(bits or other.bits)
  infix fun intersect(other: Bits): Bits = Bits(bits and other.bits)
  infix fun difference(other: Bits): Bits = Bits(bits and other.bits.inv())
  infix fun subset(other: Bits): Boolean = (bits and other.bits) == bits
  infix fun symdiff(other: Bits): Bits = Bits(bits xor other.bits)
  fun isEmpty(): Boolean = bits == 0L
  fun isNotEmpty(): Boolean = bits != 0L
}