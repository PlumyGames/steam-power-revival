package steam.gen

import arc.graphics.Color
import arc.graphics.Pixmap

class PlainLayerProcessor : ILayerProcessor {
    override fun process(raw: Pixmap): Pixmap {
        return raw
    }
}

class TintLayerProcessor(
    val color: Color,
) : ILayerProcessor {
    override fun process(raw: Pixmap): Pixmap {
        val width = raw.width
        val height = raw.height
        val res = Pixmap(height, width)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val c = Pixel(raw[x, y])
                if (c.isVisible) {
                    // TODO: Tint
                    res[x, y] = c
                } else {
                    res[x, y] = Pixel.Empty
                }
            }
        }
        return res
    }
}