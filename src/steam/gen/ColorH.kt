package steam.gen

import arc.graphics.Pixmap

@JvmInline
value class Pixel(
    val rgba8888: Int,
) {
    val isEmpty get() = rgba8888 == Empty
    val r get() = rgba8888 and RED_MASK shr 24
    val g get() = rgba8888 and GREEN_MASK shr 16
    val b get() = rgba8888 and BLUE_MASK shr 8
    val a get() = rgba8888 and ALPHA_MASK
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
    }
}

operator fun Pixmap.set(x: Int, y: Int, pixel: Pixel) {
    this[x, y] = pixel.rgba8888
}