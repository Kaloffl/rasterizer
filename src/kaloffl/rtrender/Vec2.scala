package kaloffl.rtrender

object Vec2 {
  def dot(v1: Vec2, v2: Vec2): Float = v1.x * v2.x + v1.y * v2.y
  def normalize(v: Vec2): Vec2 = {
    val il = 1 / Math.sqrt(v.x * v.x + v.y * v.y).toFloat
    Vec2(v.x * il, v.y * il)
  }
}

case class Vec2(x: Float, y: Float) {
  def *(f: Float): Vec2 = Vec2(x * f, y * f)
  def /(f: Float): Vec2 = Vec2(x / f, y / f)
  def +(v: Vec2): Vec2 = Vec2(x + v.x, y + v.y)
  def -(v: Vec2): Vec2 = Vec2(x - v.x, y - v.y)
}