package steam.gen

import arc.graphics.Color
import arc.graphics.Pixmap
import steam.gen.Pixel.Companion.blend
import steam.gen.Pixel.Companion.toPixel

class PlainLayerProcessor : ILayerProcessor {
    override fun process(raw: Pixmap): Pixmap {
        return raw
    }
}

class TintLayerProcessor(
    val color: Color,
) : ILayerProcessor {
    val dye = color.toPixel()
    override fun process(raw: Pixmap): Pixmap {
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
        return res
    }
}