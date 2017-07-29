package kaloffl.rtrender

import java.util
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.{ImageView, PixelFormat, WritableImage}
import javafx.scene.input.{Clipboard, ClipboardContent, KeyCode, MouseButton}
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

import kaloffl.rtrender.KeyEvent.Key

class JfxDisplay(val width: Int, val height: Int) {

  // TODO automatically choose the correct buffer format according to the platform preferences
  var int_buffer: Array[Int] = new Array[Int](width * height)
  var byte_buffer: Array[Byte] = _// new Array[Byte](width * height * 4)

  def setPixel(xy: Int, color: Color) {
    //if (xy  >= 0 && xy < width * height) {
      if (null != int_buffer) {
        val rgb = Color.to_srgb(color)
        int_buffer(xy) = 0xFF000000 | rgb
      } else {
        byte_buffer(xy * 4    ) = color.sb.toByte
        byte_buffer(xy * 4 + 1) = color.sg.toByte
        byte_buffer(xy * 4 + 2) = color.sr.toByte
        byte_buffer(xy * 4 + 3) = -1
      }
    //}
  }

  def setPixel(x: Int, y: Int, color: Color) {
    //if (x >= 0 && x < width && y >= 0 && y < height) {
      if (null != int_buffer) {
        val rgb = Color.to_srgb(color)
        int_buffer(x + y * width) = 0xFF000000 | rgb
      } else {
        byte_buffer((x + y * width) * 4    ) = color.sb.toByte
        byte_buffer((x + y * width) * 4 + 1) = color.sg.toByte
        byte_buffer((x + y * width) * 4 + 2) = color.sr.toByte
        byte_buffer((x + y * width) * 4 + 3) = -1
      }
    //}
  }

  def fill(color: Color): Unit = {
    if (null != int_buffer) {
      val rgb = Color.to_srgb(color)
      util.Arrays.fill(int_buffer, 0xFF000000 | rgb)
    } else {
      var i = 0
      val end = width * height * 4
      val sr = color.sr.toByte
      val sg = color.sg.toByte
      val sb = color.sb.toByte
      while (i < end) {
        byte_buffer(i    ) = sb
        byte_buffer(i + 1) = sg
        byte_buffer(i + 2) = sr
        byte_buffer(i + 3) = -1
        i += 4
      }
    }
  }

  def commit(): Unit = {
    if (null != int_buffer) {
      ActualDisplay.instance.image.getPixelWriter.setPixels(
        0, 0, width, height, PixelFormat.getIntArgbInstance, int_buffer, 0, width)
    } else {
      ActualDisplay.instance.image.getPixelWriter.setPixels(
        0, 0, width, height, PixelFormat.getByteBgraPreInstance, byte_buffer, 0, width * 4)
    }
  }

  val events = new Iterator[InputEvent] {
    override def hasNext: Boolean = !ActualDisplay.instance.events.isEmpty
    override def next: InputEvent = ActualDisplay.instance.events.poll
  }

  ActualDisplay.width = width
  ActualDisplay.height = height

  new Thread(() => {
    Application.launch(classOf[ActualDisplay])
  }).start()
  while (null == ActualDisplay.instance) {
    Thread.sleep(100)
  }
}

object ActualDisplay {
  var width = 0
  var height = 0
  var instance: ActualDisplay = _

  val jfxKeyMap: util.Map[KeyCode, Key] = {
    val map = new util.HashMap[KeyCode, Key]
    map.put(KeyCode.A, KeyEvent.Key_A)
    map.put(KeyCode.B, KeyEvent.Key_B)
    map.put(KeyCode.C, KeyEvent.Key_C)
    map.put(KeyCode.D, KeyEvent.Key_D)
    map.put(KeyCode.E, KeyEvent.Key_E)
    map.put(KeyCode.F, KeyEvent.Key_F)
    map.put(KeyCode.G, KeyEvent.Key_G)
    map.put(KeyCode.H, KeyEvent.Key_H)
    map.put(KeyCode.I, KeyEvent.Key_I)
    map.put(KeyCode.J, KeyEvent.Key_J)
    map.put(KeyCode.K, KeyEvent.Key_K)
    map.put(KeyCode.L, KeyEvent.Key_L)
    map.put(KeyCode.M, KeyEvent.Key_M)
    map.put(KeyCode.N, KeyEvent.Key_N)
    map.put(KeyCode.O, KeyEvent.Key_O)
    map.put(KeyCode.P, KeyEvent.Key_P)
    map.put(KeyCode.Q, KeyEvent.Key_Q)
    map.put(KeyCode.R, KeyEvent.Key_R)
    map.put(KeyCode.S, KeyEvent.Key_S)
    map.put(KeyCode.T, KeyEvent.Key_T)
    map.put(KeyCode.U, KeyEvent.Key_U)
    map.put(KeyCode.V, KeyEvent.Key_V)
    map.put(KeyCode.W, KeyEvent.Key_W)
    map.put(KeyCode.X, KeyEvent.Key_X)
    map.put(KeyCode.Y, KeyEvent.Key_Y)
    map.put(KeyCode.Z, KeyEvent.Key_Z)
    map.put(KeyCode.DIGIT0, KeyEvent.Key_0)
    map.put(KeyCode.DIGIT1, KeyEvent.Key_1)
    map.put(KeyCode.DIGIT2, KeyEvent.Key_2)
    map.put(KeyCode.DIGIT3, KeyEvent.Key_3)
    map.put(KeyCode.DIGIT4, KeyEvent.Key_4)
    map.put(KeyCode.DIGIT5, KeyEvent.Key_5)
    map.put(KeyCode.DIGIT6, KeyEvent.Key_6)
    map.put(KeyCode.DIGIT7, KeyEvent.Key_7)
    map.put(KeyCode.DIGIT8, KeyEvent.Key_8)
    map.put(KeyCode.DIGIT9, KeyEvent.Key_9)
    map.put(KeyCode.F1, KeyEvent.Key_F1)
    map.put(KeyCode.F2, KeyEvent.Key_F2)
    map.put(KeyCode.F3, KeyEvent.Key_F3)
    map.put(KeyCode.F4, KeyEvent.Key_F4)
    map.put(KeyCode.F5, KeyEvent.Key_F5)
    map.put(KeyCode.F6, KeyEvent.Key_F6)
    map.put(KeyCode.F7, KeyEvent.Key_F7)
    map.put(KeyCode.F8, KeyEvent.Key_F8)
    map.put(KeyCode.F9, KeyEvent.Key_F9)
    map.put(KeyCode.F10, KeyEvent.Key_F10)
    map.put(KeyCode.F11, KeyEvent.Key_F11)
    map.put(KeyCode.F12, KeyEvent.Key_F12)
    map.put(KeyCode.TAB, KeyEvent.Key_Tab)
    map.put(KeyCode.SPACE, KeyEvent.Key_Space)
    map.put(KeyCode.BACK_SPACE, KeyEvent.Key_Backspace)
    map.put(KeyCode.ENTER, KeyEvent.Key_Enter)
    map.put(KeyCode.SHIFT, KeyEvent.Key_Shift)
    map.put(KeyCode.CONTROL, KeyEvent.Key_Control)
    map.put(KeyCode.ALT, KeyEvent.Key_Alt)
    map.put(KeyCode.ESCAPE, KeyEvent.Key_Escape)
    map.put(KeyCode.UP, KeyEvent.Key_Up)
    map.put(KeyCode.DOWN, KeyEvent.Key_Down)
    map.put(KeyCode.LEFT, KeyEvent.Key_Left)
    map.put(KeyCode.RIGHT, KeyEvent.Key_Right)
    map
  }
}

class ActualDisplay extends Application {

  var updatePending = false
  var image = new WritableImage(ActualDisplay.width, ActualDisplay.height)
  val view = new ImageView(image)
  val events = new util.LinkedList[InputEvent]

  override def start(stage: Stage): Unit = {
    val content = new BorderPane
    content.setCenter(view)
    val scene = new Scene(content, ActualDisplay.width, ActualDisplay.height)
    scene.setOnKeyPressed((ev: javafx.scene.input.KeyEvent) => {
      val key = ActualDisplay.jfxKeyMap.get(ev.getCode)
      if (null != key) {
        events.add(new KeyEvent(key, true))
      }
      if (ev.isControlDown && ev.getCode == KeyCode.C) {
        val content = new ClipboardContent
        content.putImage(image)
        Clipboard.getSystemClipboard.setContent(content)
      }
    })
    scene.setOnKeyReleased((ev: javafx.scene.input.KeyEvent) => {
      val key = ActualDisplay.jfxKeyMap.get(ev.getCode)
      if (null != key) {
        events.add(new KeyEvent(key, false))
      }
      if (ev.isControlDown && ev.getCode == KeyCode.C) {
        val content = new ClipboardContent
        content.putImage(image)
        Clipboard.getSystemClipboard.setContent(content)
      }
    })
    scene.setOnMousePressed((ev: javafx.scene.input.MouseEvent) => {
      val key = ev.getButton match {
        case MouseButton.PRIMARY => KeyEvent.Mouse_1
        case MouseButton.SECONDARY => KeyEvent.Mouse_2
        case MouseButton.MIDDLE => KeyEvent.Mouse_3
        case _ => null
      }
      events.add(new KeyEvent(key, true))
    })
    scene.setOnMouseReleased((ev: javafx.scene.input.MouseEvent) => {
      val key = ev.getButton match {
        case MouseButton.PRIMARY => KeyEvent.Mouse_1
        case MouseButton.SECONDARY => KeyEvent.Mouse_2
        case MouseButton.MIDDLE => KeyEvent.Mouse_3
        case _ => null
      }
      events.add(new KeyEvent(key, false))
    })
    scene.setOnMouseMoved((ev: javafx.scene.input.MouseEvent) => {
      events.add(MouseEvent(ev.getSceneX.toInt, ev.getSceneY.toInt, dragged = false))
    })
    scene.setOnMouseDragged((ev: javafx.scene.input.MouseEvent) => {
      events.add(MouseEvent(ev.getSceneX.toInt, ev.getSceneY.toInt, dragged = true))
    })
    scene.setOnScroll((ev: javafx.scene.input.ScrollEvent) => {
      events.add(ScrollEvent(ev.getDeltaX, ev.getDeltaY))
    })
    scene.setOnZoom((ev: javafx.scene.input.ZoomEvent) => {
      events.add(ZoomEvent(ev.getZoomFactor))
    })
    stage.setScene(scene)
    stage.setResizable(false)
    stage.setOnCloseRequest(_ => System.exit(0))
    stage.show()
    ActualDisplay.instance = this
  }
}