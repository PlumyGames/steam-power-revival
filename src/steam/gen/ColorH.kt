package steam.gen

import arc.graphics.Color
import arc.graphics.Pixmap

@JvmInline
value class Pixel(
    val rgba8888: Int,
) {
    constructor(r: Int, g: Int, b: Int, a: Int) : this(
        (r shl 24) or (g shl 16) or (b shl 8) or a
    )

    constructor(r: Float, g: Float, b: Float, a: Float) : this(
        ((r * 255).toInt() shl 24) or ((g * 255).toInt() shl 16) or ((b * 255).toInt() shl 8) or (a * 255).toInt()
    )

    val isEmpty get() = rgba8888 == Empty
    val r get() = rgba8888 and RED_MASK shr 24
    val g get() = rgba8888 and GREEN_MASK shr 16
    val b get() = rgba8888 and BLUE_MASK shr 8
    val a get() = rgba8888 and ALPHA_MASK
    val rf get() = (rgba8888 and RED_MASK shr 24) / 255f
    val gf get() = (rgba8888 and GREEN_MASK shr 16) / 255f
    val bf get() = (rgba8888 and BLUE_MASK shr 8) / 255f
    val af get() = (rgba8888 and ALPHA_MASK) / 255f
    val hasAlpha get() = a != 0xFF
    val isVisible get() = a != 0x00
    fun coverBy(b: Pixel): Pixel = if (b.isVisible) b else this
    fun alphaMixBy(): Pixel {
        return this
    }

    companion object {
        const val RED_MASK = -0x1000000
        const val GREEN_MASK = 0x00ff0000
        const val BLUE_MASK = 0x0000ff00
        const val ALPHA_MASK = 0x000000ff
        const val Empty = 0x00000000
        fun blend(bk: Pixel, fg: Pixel): Pixel {
            val fga = fg.af
            val r = fg.rf * fga + bk.rf * (1f - fga)
            val g = fg.gf * fga + bk.gf * (1f - fga)
            val b = fg.bf * fga + bk.bf * (1f - fga)
            val a = bk.af
            return Pixel(r, g, b, a)
        }

        fun Color.toPixel() = Pixel(rgba8888())
    }
}

operator fun Pixmap.set(x: Int, y: Int, pixel: Pixel) {
    this[x, y] = pixel.rgba8888
}