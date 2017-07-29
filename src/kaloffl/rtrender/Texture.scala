package kaloffl.rtrender

class Texture(buffer: Array[Int], width: Int, height: Int) {

  val color_buffer: Array[Color] = new Array[Color](width * height)
  for (xy <- 0 until height * width) {
    color_buffer(xy) = Color.from_srgb(buffer(xy))
  }

  def read_nearest(x: Int, y: Int): Color = color_buffer(x + y * width)

  def read_nearest(x: Float, y: Float): Color = color_buffer((x * width).toInt + (y * height).toInt * width)
}
