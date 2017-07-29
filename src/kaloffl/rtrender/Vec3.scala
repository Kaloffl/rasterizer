package kaloffl.rtrender

object Vec3 {
  val Origin = Vec3(0, 0, 0)
  val Left = Vec3(1, 0, 0)
  val Right = Vec3(-1, 0, 0)
  val Up = Vec3(0, 1, 0)
  val Down = Vec3(0, -1, 0)
  val Front = Vec3(0, 0, 1)
  val Back = Vec3(0, 0, -1)

  def from_angles(pitch: Float, yaw: Float): Vec3 = {
    val angle = yaw * 2.0 * Math.PI
    val rnd = pitch
    val distSq = 1.0 - rnd * rnd
    val dist = Math.sqrt(distSq).toFloat

    val nx = dist * Math.cos(angle).toFloat
    val ny = rnd
    val nz = dist * Math.sin(angle).toFloat
    return Vec3(nx, ny, nz)
  }
  def length_squared(v: Vec3): Float = v.x * v.x + v.y * v.y + v.z * v.z
  def length(v: Vec3): Float = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z).toFloat
  def dot(v1: Vec3, v2: Vec3): Float = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
  def cross(v1: Vec3, v2: Vec3): Vec3 =
    Vec3(
      v1.y * v2.z - v2.y * v1.z,
      v1.z * v2.x - v2.z * v1.x,
      v1.x * v2.y - v2.x * v1.y)
  def normalize(v: Vec3): Vec3 = {
    val il = 1 / Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z).toFloat
    Vec3(v.x * il, v.y * il, v.z * il)
  }
  def reflect(v: Vec3, axis: Vec3): Vec3 = v - axis * (dot(v, axis) * 2)
}

case class Vec3(x: Float, y: Float, z: Float) {
  def *(f: Float): Vec3 = Vec3(x * f, y * f, z * f)
  def /(f: Float): Vec3 = Vec3(x / f, y / f, z / f)
  def /(v: Vec3): Vec3 = Vec3(x / v.x, y / v.y, z / v.z)
  def +(v: Vec3): Vec3 = Vec3(x + v.x, y + v.y, z + v.z)
  def -(v: Vec3): Vec3 = Vec3(x - v.x, y - v.y, z - v.z)
  def unary_-(): Vec3 = Vec3(-x, -y, -z)
}