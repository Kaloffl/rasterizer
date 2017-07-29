package kaloffl.rtrender

import java.io.File
import javax.imageio.ImageIO

object ExampleBox {

  def main(args: Array[String]): Unit = {
    val width = 1280
    val height = 720
    val texture = ImageIO.read(new File("res/test.png"))

    new Engine(
      texture = new Texture(
        buffer = texture.getRGB(0, 0, texture.getWidth, texture.getHeight, null, 0, texture.getWidth),
        width = texture.getWidth,
        height = texture.getHeight),
      window = new JfxDisplay(width, height),
      initial_player = Player(
        Viewpoint(
          position = Vec3(20, -7, 0),
          forward = Vec3(0, 0, 1)),
        velocity = Vec3(0, 0, 0)),
      rasterizer = new Rasterizer(
        width = width,
        height = height,
        vertices = Array(
          Vec3(-10, -10, -10), Vec3(-10, -10,  10),
          Vec3(-10,  10, -10), Vec3(-10,  10,  10),
          Vec3( 10, -10, -10), Vec3( 10, -10,  10),
          Vec3( 10,  10, -10), Vec3( 10,  10,  10)),
        vert_indices = Array(
          6, 7, 2, 3, 2, 7,
          2, 3, 0, 1, 0, 3,
          7, 6, 5, 4, 5, 6,
          4, 6, 0, 2, 0, 6,
          3, 7, 1, 5, 1, 7,
          4, 0, 5, 1, 5, 0),
        texcoords = Array(
          Vec2(0, 0), Vec2(0, 1), Vec2(1, 0), Vec2(1, 1), Vec2(1, 0), Vec2(0, 1)),
        texcoord_indices = Array(
          0, 1, 2, 3, 4, 5,
          0, 1, 2, 3, 4, 5,
          0, 1, 2, 3, 4, 5,
          0, 1, 2, 3, 4, 5,
          0, 1, 2, 3, 4, 5,
          0, 1, 2, 3, 4, 5),
        normals = Array(
          Vec3(1, 0, 0), Vec3(-1, 0, 0), Vec3(0, 1, 0), Vec3(0, -1, 0), Vec3(0, 0, 1), Vec3(0, 0, -1)),
        norm_indices = Array(
          2, 2, 2, 2, 2, 2,
          1, 1, 1, 1, 1, 1,
          0, 0, 0, 0, 0, 0,
          5, 5, 5, 5, 5, 5,
          4, 4, 4, 4, 4, 4,
          3, 3, 3, 3, 3, 3)
        )).run()
  }
}
