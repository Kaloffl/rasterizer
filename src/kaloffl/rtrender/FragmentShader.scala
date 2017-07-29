package kaloffl.rtrender

import Vec3._

trait FragmentShader {
  def draw(texcoord: Vec2, normal: Vec3, world_pos: Vec3): Color
}

class TestShader(val cam_pos: Vec3,
                 val light_pos: Vec3,
                 val texture: Texture
                ) extends FragmentShader{

  override def draw(texcoord: Vec2, normal: Vec3, world_pos: Vec3): Color = {
    val albedo = texture.read_nearest(texcoord.x % 1, texcoord.y % 1)

    val to_light = normalize(light_pos - world_pos)
    val to_cam = normalize(cam_pos - world_pos)

    val diffuse = Math.max(0.1f, dot(normal, to_light))
    val specular = Math.pow(Math.max(0, -dot(reflect(to_cam, normal), to_light)), 2).toFloat
    val shade = diffuse + specular

    return albedo * shade
  }
}