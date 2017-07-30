package kaloffl.rtrender

class Texture(buffer: Array[Int], width: Int, height: Int) {

  val color_buffer: Array[Color] = new Array[Color](width * height)
  for (y <- 0 until height; x <- 0 until width) {
    val xy = Texture.xy_to_z_curve(x, y, width)
    color_buffer(xy) = Color.from_srgb(buffer(x + y * width))
  }

  def read_nearest(x: Int, y: Int): Color = color_buffer(Texture.xy_to_z_curve(x, y, width))

  def read_nearest(x: Float, y: Float): Color = color_buffer(Texture.xy_to_z_curve((x * width).toInt, (y * height).toInt, width))
}

object Texture {
    def xy_to_z_curve(x: Int, y: Int, size: Int): Int = {
        var nx = x
        var ny = y
        var nsize = size
        var index = 0
        while (1 < nsize) {
            nsize = nsize / 2
            if (ny >= nsize) {
                ny -= nsize
                index += nsize * nsize
            }
            if (nx >= nsize) {
                nx -= nsize
                index += nsize * nsize * 2
            }
        }
        return index
    }
}