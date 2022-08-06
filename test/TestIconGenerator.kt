import arc.files.Fi
import arc.graphics.Pixmap
import mindustry.graphics.Pal
import org.junit.jupiter.api.Test
import plumy.texture.*
import java.io.File
import javax.imageio.ImageIO

class TestIconGenerator {
    val rootDir = File("")
    val assets = rootDir.resolve("assets")
    val templates = assets.resolve("sprites/template")
    val `ore-base0` = templates.resolve("ore-base0.png")
    val `ore-patch0` = templates.resolve("ore-patch0.png")
    fun `gen icon`(): Pixmap {
        val maker = StackIconMaker(32, 32)
        val layers = listOf(
            PixmapModelLayerForm(`ore-base0`) + PlainLayerProcessor(),
            PixmapModelLayerForm(`ore-patch0`) + TintLerpLayerProcessor(Pal.accent, 0.7f)
        )
        val baked = maker.bake(layers)
        return baked.texture.toPixmap()
    }
    @Test
    fun `test gen icon`() {
        `gen icon`()
    }
    @Test
    fun `test output icon`() {
        val icon = `gen icon`()
        val output = File.createTempFile("test-generated-icon", ".png")
        Fi(output).writePng(icon)
    }
    @Test
    fun `test read buffered image form local file`() {
        ImageIO.read(`ore-base0`)
    }
}


