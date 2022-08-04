import arc.graphics.Pixmap
import org.junit.jupiter.api.Test
import steam.gen.StackIconMaker
import steam.gen.PixmapModelLayerForm
import steam.gen.PlainLayerProcessor
import steam.gen.plus
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class TestIconGenerator {
    val rootDir = File("")
    val assets = rootDir.resolve("assets")
    val templates = assets.resolve("sprites/template")
    val `ore-base1` = templates.resolve("ore-base1.png")
    val `ore-patch1` = templates.resolve("ore-patch1.png")
    fun `gen icon`(): Pixmap {
        val maker = StackIconMaker(32, 32)
        val layers = listOf(
            PixmapModelLayerForm(`ore-base1`) + PlainLayerProcessor(),
            PixmapModelLayerForm(`ore-patch1`) + PlainLayerProcessor()
        )
        val baked = maker.bake(layers)
        return baked.texture.pixels
    }
    @Test
    fun `test gen icon`() {
        `gen icon`()
    }
    @Test
    fun `test show icon`() {
        val icon = `gen icon`()
        val img = icon.toBufferedImage()
    }
    @Test
    fun `test read buffered image form local file`() {
        ImageIO.read(`ore-base1`)
    }
}

fun Pixmap.toByteArray() =
    ByteArray(pixels.remaining()).apply {
        pixels.get(this)
    }

fun Pixmap.toBufferedImage(): BufferedImage {
    val input = this.toByteArray().inputStream()
    val img: BufferedImage? = ImageIO.read(input)
    return img!!
}
