package kaloffl.rtrender

import java.util

import kaloffl.rtrender.Rasterizer.FragmentWriter

object Rasterizer {
  // small class that will be created by the main rasterize function and gets passed down all the way to where single pixels are written
  class FragmentWriter(val width: Int, val height: Int, id: Int, depth: Array[Int], uvw: Array[Float], ids: Array[Int]) {
    def write(x: Int, y: Int, z: Int, u: Float, v: Float, w: Float): Unit = {
      val index = x + y * width
      if (z < depth(index)) {
        depth(index) = z
        uvw(index * 3    ) = u
        uvw(index * 3 + 1) = v
        uvw(index * 3 + 2) = w
        ids(index) = id
      }
    }
  }
}

class Rasterizer(val width: Int,
                 val height: Int,
                 val vertices: Array[Vec3],
                 val vert_indices: Array[Int],
                 val texcoords: Array[Vec2],
                 val texcoord_indices: Array[Int],
                 val normals: Array[Vec3],
                 val norm_indices: Array[Int]
                ) {

  val screen_depth = new Array[Int](width * height)
  val screen_uvw = new  Array[Float](width * height * 3)
  val screen_ids = new Array[Int](width * height)
  val mapped_vertices = new Array[Vec3](vertices.length)

  def rasterize(vertex_shader: VertexShader, fragment_shader: FragmentShader, window: JfxDisplay): Unit = {

    util.Arrays.fill(screen_depth, Integer.MAX_VALUE)
    util.Arrays.fill(screen_ids, -1)

    var v = 0
    while (v < vertices.length) {
      mapped_vertices(v) = vertex_shader.map(vertices(v))
      v += 1
    }
    var t = 0
    while (t < vert_indices.length) {
      val fw = new Rasterizer.FragmentWriter(width, height, t, screen_depth, screen_uvw, screen_ids)
      rasterize(
        mapped_vertices(vert_indices(t)),
        mapped_vertices(vert_indices(t + 1)),
        mapped_vertices(vert_indices(t + 2)), fw)
      t += 3
    }

    var xy = 0
    while(xy < width * height) {
      val id = screen_ids(xy)
      if (id >= 0) {
        val u = screen_uvw(xy * 3)
        val v = screen_uvw(xy * 3 + 1)
        val w = screen_uvw(xy * 3 + 2)
        val normal =
          normals(norm_indices(id    )) * u +
          normals(norm_indices(id + 1)) * v +
          normals(norm_indices(id + 2)) * w
        val pos =
          vertices(vert_indices(id    )) * u +
          vertices(vert_indices(id + 1)) * v +
          vertices(vert_indices(id + 2)) * w
        val texcoord =
          texcoords(texcoord_indices(id    )) * u +
          texcoords(texcoord_indices(id + 1)) * v +
          texcoords(texcoord_indices(id + 2)) * w

        window.setPixel(xy, fragment_shader.draw(texcoord, normal, pos))
      }
      xy += 1
    }
  }

  def rasterize(sa: Vec3, sb: Vec3, sc: Vec3, fw: FragmentWriter): Unit = {

    // Clipping: we can't have any points behind the camera or else the world will end.
    //
    val da = sa.z - 0.1f
    val db = sb.z - 0.1f
    val dc = sc.z - 0.1f
    val vertices = new Array[Vec3](4)
    val attributes = new Array[Vec3](4)
    var vertex_count = 0
    if (da * db < 0) { // if sa and sb lie on different sides of the plane
      val fab = da / (da - db)
      vertices(vertex_count) = sa + (sb - sa) * fab
      attributes(vertex_count) = Vec3(1 - fab, fab, 0)
      vertex_count += 1
    }
    if (db > 0) { // if sb is on the correct side of the plane
      vertices(vertex_count) = sb
      attributes(vertex_count) = Vec3(0, 1, 0)
      vertex_count += 1
    }
    if (db * dc < 0) { // if db and dc lie on different sides of the plane
      val fbc = db / (db - dc)
      vertices(vertex_count) = sb + (sc - sb) * fbc
      attributes(vertex_count) = Vec3(0, 1 - fbc, fbc)
      vertex_count += 1
    }
    if (dc > 0) { // if sc is on the correct side of the plane
      vertices(vertex_count) = sc
      attributes(vertex_count) = Vec3(0, 0, 1)
      vertex_count += 1
    }
    if (dc * da < 0) { // if dc and da lie on different sides of the plane
      val fca = dc / (dc - da)
      vertices(vertex_count) = sc + (sa - sc) * fca
      attributes(vertex_count) = Vec3(fca, 0, 1 - fca)
      vertex_count += 1
    }
    if (da > 0) { // if sa is on the correct side of the plane
      vertices(vertex_count) = sa
      attributes(vertex_count) = Vec3(1, 0, 0)
      vertex_count += 1
    }

    // scale vertices to the screen size and divide the x and y by z to make things further away get smaller
    // attributes also get divided by z and later on scaled back up again to get perspective-correct attribute interpolation
    var v = 0
    while (v < vertex_count) {
      val vert = vertices(v)
      vertices(v) =
        Vec3(
          (vert.x / vert.z * 0.5f + 0.5f) * fw.width,
          (fw.height - vert.y / vert.z * fw.width) * 0.5f,
          1.0f / vert.z)
      attributes(v) = attributes(v) / vert.z
      v += 1
    }

    // finally render the triangle(s) to the screen
    if (3 == vertex_count) {
      rasterize2D(
        vertices(0), vertices(1), vertices(2),
        attributes(0), attributes(1), attributes(2),
        fw)
    } else if (4 == vertex_count) {
      rasterize2D(
        vertices(0), vertices(1), vertices(3),
        attributes(0), attributes(1), attributes(3),
        fw)
      rasterize2D(
        vertices(1), vertices(3), vertices(2),
        attributes(1), attributes(3), attributes(2),
        fw)
    }
  }

  def edgeFunction(ax: Float, ay: Float, bx: Float, by: Float, cx: Float, cy: Float): Float = {
    (cx - ax) * (by - ay) - (cy - ay) * (bx - ax)
  }

  // This is the unoptimized reference implementation for drawing the triangle onto the screen
  def rasterize2DSimple(a: Vec3, b: Vec3, c: Vec3, da: Vec3, db: Vec3, dc: Vec3, fw: FragmentWriter): Unit = {

    val minx = Math.min(Math.min(a.x, b.x), c.x)
    val maxx = Math.max(Math.max(a.x, b.x), c.x)
    val miny = Math.min(Math.min(a.y, b.y), c.y)
    val maxy = Math.max(Math.max(a.y, b.y), c.y)

    val cminx = Math.min(fw.width - 1, Math.max(0, Math.ceil(minx))).toInt
    val cmaxx = Math.max(0, Math.min(fw.width - 1, maxx)).toInt
    val cminy = Math.min(fw.height - 1, Math.max(0, Math.ceil(miny))).toInt
    val cmaxy = Math.max(0, Math.min(fw.height - 1, maxy)).toInt

    val ia = 1 / edgeFunction(a.x, a.y, b.x, b.y, c.x, c.y)

    var y = cminy
    while (y <= cmaxy) {
      var x = cminx
      while (x <= cmaxx) {
        val hs1 = edgeFunction(b.x, b.y, c.x, c.y, x, y) * ia
        val hs2 = edgeFunction(c.x, c.y, a.x, a.y, x, y) * ia
        val hs3 = edgeFunction(a.x, a.y, b.x, b.y, x, y) * ia
        if (hs1 >= 0 && hs2 >= 0 && hs3 >= 0) {
          val dx = da.x * hs1 + db.x * hs2 + dc.x * hs3
          val dy = da.y * hs1 + db.y * hs2 + dc.y * hs3
          val dz = da.z * hs1 + db.z * hs2 + dc.z * hs3
          val z = 1 / (a.z * hs1 + b.z * hs2 + c.z * hs3)
          val iz = (z * 1000.0).toInt
          fw.write(x, y, iz, dx * z, dy * z, dz * z)
        }
        x += 1
      }
      y += 1
    }
  }

  // optimized triangle rasterization
  def rasterize2D(a: Vec3, b: Vec3, c: Vec3, da: Vec3, db: Vec3, dc: Vec3, fw: FragmentWriter): Unit = {

    val minx = Math.min(Math.min(a.x, b.x), c.x)
    val maxx = Math.max(Math.max(a.x, b.x), c.x)
    val miny = Math.min(Math.min(a.y, b.y), c.y)
    val maxy = Math.max(Math.max(a.y, b.y), c.y)

    val cminx = Math.max(0, Math.ceil(minx)).toInt
    val cmaxx = Math.min(fw.width, Math.ceil(maxx)).toInt
    val cminy = Math.max(0, Math.ceil(miny)).toInt
    val cmaxy = Math.min(fw.height - 1, maxy).toInt

    if (cminx > cmaxx || cminy > cmaxy) return

    val ia = 1 / edgeFunction(a.x, a.y, b.x, b.y, c.x, c.y)

    val xs1 = (c.y - b.y) * ia
    val xs2 = (a.y - c.y) * ia
    val xs3 = (b.y - a.y) * ia

    val ys1 = (c.x - b.x) * ia
    val ys2 = (a.x - c.x) * ia
    val ys3 = (b.x - a.x) * ia

    val dax = da.x * xs1 + db.x * xs2 + dc.x * xs3
    val day = da.y * xs1 + db.y * xs2 + dc.y * xs3
    val daz = da.z * xs1 + db.z * xs2 + dc.z * xs3

    var hs1_row = -b.x * xs1 - (cminy - b.y) * ys1
    var hs2_row = -c.x * xs2 - (cminy - c.y) * ys2
    var hs3_row = -a.x * xs3 - (cminy - a.y) * ys3
    var y = cminy
    while (y <= cmaxy) {
      val s1 = Math.ceil(-hs1_row / xs1).toInt
      val s2 = Math.ceil(-hs2_row / xs2).toInt
      val s3 = Math.ceil(-hs3_row / xs3).toInt
      var x = cminx
      var xend = cmaxx
      if (xs1 > 0 && s1 > x) x = s1 else if (xs1 < 0 && s1 < xend) xend = s1
      if (xs2 > 0 && s2 > x) x = s2 else if (xs2 < 0 && s2 < xend) xend = s2
      if (xs3 > 0 && s3 > x) x = s3 else if (xs3 < 0 && s3 < xend) xend = s3
      x = Math.min(x, fw.width)
      xend = Math.max(xend, 0)

      var hs1 = hs1_row + xs1 * x
      var hs2 = hs2_row + xs2 * x
      var hs3 = hs3_row + xs3 * x
      var dx = da.x * hs1 + db.x * hs2 + dc.x * hs3
      var dy = da.y * hs1 + db.y * hs2 + dc.y * hs3
      var dz = da.z * hs1 + db.z * hs2 + dc.z * hs3
      while (x < xend) {
        val z = 1 / (a.z * hs1 + b.z * hs2 + c.z * hs3)
        val iz = (z * 1000.0).toInt
        fw.write(x, y, iz, dx * z, dy * z, dz * z)
        hs1 += xs1
        hs2 += xs2
        hs3 += xs3
        dx += dax
        dy += day
        dz += daz
        x += 1
      }
      hs1_row -= ys1
      hs2_row -= ys2
      hs3_row -= ys3
      y += 1
    }
  }
}
