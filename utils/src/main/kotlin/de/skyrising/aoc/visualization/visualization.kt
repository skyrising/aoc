package de.skyrising.aoc.visualization

import de.skyrising.aoc.PuzzleDay
import de.skyrising.aoc.Vec2i
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.nio.ByteBuffer
import java.nio.file.Path
import javax.swing.JFrame


interface Visualization : AutoCloseable {
    var video: Boolean
    var gui: Boolean
    var size: Vec2i
    val g: Graphics2D
    fun present()
}

interface Output {
    fun writeFrame(image: BufferedImage)
    fun close()
}

object DummyVisualization : Visualization {
    override var video = false
    override var gui = false
    override var size = Vec2i(1920, 1080)
    override val g: Graphics2D = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics()

    override fun present() {}
    override fun close() {
        g.dispose()
    }
}

class SwingOutput(day: PuzzleDay) : Output {
    private val frame = JFrame("${day.year} - Day ${day.day}")
    var size
        get() = Vec2i(frame.contentPane.width, frame.contentPane.height)
        set(value) {
            frame.contentPane.preferredSize = Dimension(value.x, value.y)
            frame.pack()
            frame.setLocationRelativeTo(null)
        }

    init {
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        frame.createBufferStrategy(2)
    }

    override fun writeFrame(image: BufferedImage) {
        val bs = frame.bufferStrategy
        val g = bs.drawGraphics as Graphics2D
        val insets = frame.insets
        g.drawImage(image, insets.left, insets.top, null)
        bs.show()
    }

    override fun close() {
        frame.isVisible = false
        frame.dispose()
    }
}

class FFmpegOutput(path: Path, size: Vec2i) : Output {
    private val process = ProcessBuilder("ffmpeg", "-y", "-f", "rawvideo", "-vcodec", "rawvideo", "-s", "${size.x}x${size.y}", "-pix_fmt", "argb", "-r", "60", "-i", "-", path.toString())
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
    private val output = process.outputStream

    override fun writeFrame(image: BufferedImage) {
        val buf = image.data.dataBuffer as DataBufferInt
        val ints = buf.data
        val bytes = ByteArray(image.width * image.height * 4)
        val byteBuf = ByteBuffer.wrap(bytes)
        byteBuf.asIntBuffer().put(ints)
        output.write(bytes)
    }

    override fun close() {
        output.close()
        process.waitFor()
    }
}

const val NS_PER_FRAME = 1e9 / 60

class RealVisualization(private val day: PuzzleDay) : Visualization {
    private var locked = false
    override var video = false
        set(value) {
            if (locked) throw IllegalStateException("Cannot disable video")
            field = value
        }
    override var gui = true
    private var frame = -1
    private var lastFrameTime = System.nanoTime()
    override var size = Vec2i(1920, 1080)
        set(value) {
            if (locked) throw IllegalStateException("Output size is locked")
            field = value
        }
    private val image by lazy {
        locked = true
        BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB)
    }
    override val g: Graphics2D by lazy { image.createGraphics() }
    private val outputs: List<Output> by lazy {
        locked = true
        val outputs = mutableListOf<Output>()
        if (gui) outputs.add(SwingOutput(day).also { it.size = size })
        if (video) outputs.add(FFmpegOutput(Path.of("viz/${day.year}-${day.day}.mp4"), size))
        outputs
    }

    override fun present() {
        frame++
        for (output in outputs) {
            output.writeFrame(image)
        }
        val now = System.nanoTime()
        val elapsed = now - lastFrameTime
        val wait = NS_PER_FRAME - elapsed
        if (wait > 0) try {
            Thread.sleep(wait.toLong() / 1000000, (wait % 1000000).toInt())
        } catch (_: InterruptedException) {}
        lastFrameTime = System.nanoTime()
    }

    override fun close() {
        g.dispose()
        for (output in outputs) {
            output.close()
        }
    }
}