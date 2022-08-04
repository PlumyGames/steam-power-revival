package steam.gen

import arc.graphics.Color
import arc.graphics.Pixmap
import steam.gen.Pixel.Companion.blend
import steam.gen.Pixel.Companion.toPixel

class PlainLayerProcessor : ILayerProcessor {
    override fun process(original: ITexture): ITexture = original
}

class TintLayerProcessor(
    val color: Color,
) : ILayerProcessor {
    val dye = color.toPixel()
    override fun process(original: ITexture): ITexture {
        val raw = original.pixels
        val width = raw.width
        val height = raw.height
        val res = Pixmap(width, height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val c = Pixel(raw[x, y])
                if (c.isVisible) {
                    res[x, y] = blend(c, dye)
                }
            }
        }
        return ProcessedTexture(res)
    }
}