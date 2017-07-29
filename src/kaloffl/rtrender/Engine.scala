package kaloffl.rtrender

import Vec3._

case class Player(view: Viewpoint, velocity: Vec3)

class Engine(val texture: Texture,
             val window: JfxDisplay,
             val initial_player: Player,
             val rasterizer: Rasterizer
            ) {

  def run(): Unit = {
    var player = initial_player
    var time = 0f
    val clear_color = Color.from_srgb(154, 206, 235) // Can I get the icon in cornflower-blue?

    while (true) {
      handleInput(window.events)
      player = simulate(player)
      val view = player.view

      val before = System.nanoTime

      window.fill(clear_color)
      val light_position = Vec3(0, Math.sin(time).toFloat * 0.5f + 0.5f, 0)
      val vertex_shader   = new WorldToCameraShader(view.position, view.right, view.up, view.forward)
      val fragment_shader = new TestShader(view.position, light_position, texture)

      rasterizer.rasterize(vertex_shader, fragment_shader, window)
      window.commit()

      val after = System.nanoTime
      val duration = after - before
      time += Math.max(duration / 1000000000.0f, 1.0f / 30.0f)
      if (duration > 1000000000) {
        println("rendertime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
      } else {
        println("rendertime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
      }
      val sleep = 30000000L - duration
      if (0 < sleep) {
        Thread.sleep(sleep / 1000000, (sleep % 1000000).toInt)
      }
    }
  }

  var oldMX = 0
  var oldMY = 0
  var pitch = 10f
  var yaw = 180f
  var move_forward = false
  var move_backward = false
  var move_up = false
  var move_down = false
  var move_left = false
  var move_right = false

  def handleInput(events: Iterator[InputEvent]): Unit = {
    while (events.hasNext) {
      events.next match {
        case KeyEvent(KeyEvent.Key_W, down) => move_forward = down
        case KeyEvent(KeyEvent.Key_S, down) => move_backward = down
        case KeyEvent(KeyEvent.Key_A, down) => move_left = down
        case KeyEvent(KeyEvent.Key_D, down) => move_right = down
        case KeyEvent(KeyEvent.Key_Shift, down) => move_up = down
        case KeyEvent(KeyEvent.Key_Control, down) => move_down = down

        case MouseEvent(x, y, true) =>
          pitch += 0.2f * (y - oldMY)
          yaw += 0.2f * (x - oldMX)
          oldMX = x
          oldMY = y
          pitch = Math.min(89.0f, Math.max(-89.0f, pitch))
          while(yaw < 0f) {
            yaw += 360f
          }
          while(yaw >= 360f) {
            yaw -= 360f
          }
        case MouseEvent(x, y, false) =>
          oldMX = x
          oldMY = y

        case _ =>
      }
    }
  }

  def simulate(player: Player): Player = {
    val forward = from_angles(pitch / 90, yaw / 360)
    val mv_forward = normalize(Vec3(player.view.forward.x, 0, player.view.forward.z))
    val mv_right = Vec3(player.view.right.x, 0, player.view.right.z)
    val mv_up = Vec3(0, 1, 0)
    val acceleration =
      mv_forward * ((if (move_forward) 1 else 0) + (if (move_backward) -1 else 0)) +
      mv_right   * ((if (move_right  ) 1 else 0) + (if (move_left    ) -1 else 0)) +
      mv_up      * ((if (move_up     ) 1 else 0) + (if (move_down    ) -1 else 0))

    Player(
      Viewpoint(player.view.position + player.velocity, forward),
      player.velocity * 0.75f + acceleration * 0.2f)
  }
}
