package kaloffl.rtrender

import Vec3._

case class Viewpoint(position: Vec3, forward: Vec3) {
  val right = normalize(cross(forward, Vec3.Up))
  val up = cross(right, forward)
}