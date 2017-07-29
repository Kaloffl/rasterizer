package kaloffl.rtrender

import java.io.{BufferedInputStream, FileInputStream}
import java.util
import java.util.Scanner

/**
  * Just quickly hacked together to load an example model. This thing can probably not even load any other obj files.
  */
object ObjImporter {

  def load(path: String): (Array[Vec3], Array[Int], Array[Vec3], Array[Int], Array[Vec2], Array[Int]) = {

    val inputStream = new BufferedInputStream(new FileInputStream(path))
    val scanner = new Scanner(inputStream)

    class i3(val a: Int, val b: Int, val c: Int)

    val verts_list = new util.ArrayList[Vec3]()
    val uvs_list = new util.ArrayList[Vec2]()
    val normals_list = new util.ArrayList[Vec3]()
    val indices_list = new util.ArrayList[i3]()

    while(scanner.hasNextLine) {
      val parts = scanner.nextLine().split(" ")
      parts(0) match {
        case "v" =>
          verts_list.add(Vec3(
            java.lang.Float.parseFloat(parts(1)),
            java.lang.Float.parseFloat(parts(2)),
            java.lang.Float.parseFloat(parts(3))))
        case "vt" =>
          uvs_list.add(Vec2(
            java.lang.Float.parseFloat(parts(1)),
            java.lang.Float.parseFloat(parts(2))))
        case "vn" =>
          normals_list.add(Vec3(
            java.lang.Float.parseFloat(parts(1)),
            java.lang.Float.parseFloat(parts(2)),
            java.lang.Float.parseFloat(parts(3))))
        case "f" =>
          for (p <- parts.tail) {
            val pps = p.split("/")
            indices_list.add(new i3(
              java.lang.Integer.parseInt(pps(0)) - 1,
              java.lang.Integer.parseInt(pps(1)) - 1,
              java.lang.Integer.parseInt(pps(2)) - 1))
          }
        case _ => // ignore
      }
    }

    val verts_array = verts_list.toArray[Vec3](new Array[Vec3](verts_list.size()))
    val verts_index_array = new Array[Int](indices_list.size())
    val uvs_array = uvs_list.toArray[Vec2](new Array[Vec2](uvs_list.size()))
    val uvs_index_array = new Array[Int](indices_list.size())
    val normals_array = normals_list.toArray[Vec3](new Array[Vec3](normals_list.size()))
    val normals_index_array = new Array[Int](indices_list.size())

    var i = 0
    while (i < indices_list.size()) {
      verts_index_array(i) = indices_list.get(i).a
      uvs_index_array(i) = indices_list.get(i).b
      normals_index_array(i) = indices_list.get(i).c
      i += 1
    }
    return (verts_array, verts_index_array, normals_array, normals_index_array, uvs_array, uvs_index_array)
  }
}
