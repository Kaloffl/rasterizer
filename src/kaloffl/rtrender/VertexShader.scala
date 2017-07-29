package kaloffl.rtrender

trait VertexShader {
  def map(vertex: Vec3): Vec3
}

class WorldToCameraShader(camera_position: Vec3,
                          camera_right: Vec3,
                          camera_up: Vec3,
                          camera_forward: Vec3
                         ) extends VertexShader {

  override def map(vertex: Vec3): Vec3 = {
    // translation: make all vertex coordinates relative to the camera position
    val tv = vertex - camera_position

    // base change: express the vertex coordinates in terms of the camera base vectors
    return Vec3(
      camera_right  .x * tv.x + camera_right  .y * tv.y + camera_right  .z * tv.z,
      camera_up     .x * tv.x + camera_up     .y * tv.y + camera_up     .z * tv.z,
      camera_forward.x * tv.x + camera_forward.y * tv.y + camera_forward.z * tv.z)
  }
}
