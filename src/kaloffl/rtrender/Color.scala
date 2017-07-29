package kaloffl.rtrender

import java.util

object Color {
  // LUT of gamma-correct float value for an srgb byte value
  val byte_to_float = new Array[Float](256)
  for (b <- 0 until 256) {
    byte_to_float(b) = Math.pow(b / 255.0, 2.2).toFloat
  }

  // Uses a binary search through the srgb->float LUT to get from float back to a srgb byte
  def float_to_byte(f: Float): Int = {
    var i = util.Arrays.binarySearch(byte_to_float, f)
    if (i < 0) i = -i + 1
    return Math.min(i, 255)
  }

  def from_srgb(r: Int, g: Int, b: Int): Color =
    Color(byte_to_float(r), byte_to_float(g), byte_to_float(b))

  def from_srgb(srgb: Int): Color =
    Color(
      byte_to_float(srgb >> 16 & 0xFF),
      byte_to_float(srgb >>  8 & 0xFF),
      byte_to_float(srgb       & 0xFF))

  def to_srgb(c: Color): Int = {
    val r = float_to_byte(c.r)
    val g = float_to_byte(c.g)
    val b = float_to_byte(c.b)
    return r << 16 | g << 8 | b
  }
}

case class Color(r: Float, g: Float, b: Float) {
  def sr: Int = Color.float_to_byte(r)
  def sg: Int = Color.float_to_byte(g)
  def sb: Int = Color.float_to_byte(b)

  def +(c: Color) = Color(r + c.r, g + c.g, b + c.b)
  def *(f: Float) = Color(r * f, g * f, b * f)
}
