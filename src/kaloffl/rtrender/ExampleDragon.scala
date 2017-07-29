package kaloffl.rtrender

import java.io.File
import javax.imageio.ImageIO

object ExampleDragon {

  def main(args: Array[String]): Unit = {
    val width = 1280
    val height = 720

    val (vertices, vert_indices, normals, norm_indices, texcoords, texcoord_indices) = ObjImporter.load("C:/dev/dragon.obj")

    val texture = ImageIO.read(new File("C:/dev/dragon.png"))
    val tex_width = texture.getWidth
    val tex_height = texture.getHeight
    val tex_buffer = texture.getRGB(0, 0, tex_width, tex_height, null, 0, tex_width)

    new Engine(
      texture = new Texture(
        buffer = tex_buffer,
        width = tex_width,
        height = tex_height),
      window = new JfxDisplay(width, height),
      initial_player = Player(
        Viewpoint(
          position = Vec3(5, 1, 5),
          forward = Vec3(0, 0, 1)),
        velocity = Vec3(0, 0, 0)),
      rasterizer = new Rasterizer(
        width = width,
        height = height,
        vertices = vertices,
        vert_indices = vert_indices,
        texcoords = texcoords,
        texcoord_indices = texcoord_indices,
        normals = normals,
        norm_indices = norm_indices
      )).run()
  }
}
