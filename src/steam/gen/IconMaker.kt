package steam.gen

import arc.graphics.Pixmap

class Icon(override val texture: Pixmap) : IBakedModel
class IconMaker(
    val width: Int,
    val height: Int,
) : IBakery {
    override fun bake(layers: List<IModelLayer>): IBakedModel {
        val res = Pixmap(width, height)
        for (layer in layers) {
            val processed = layer.process()
            res.coverBy(processed)
        }
        return Icon(res)
    }
}

fun Pixmap.coverBy(cover: Pixmap) {
    val width = this.width
    val height = this.height
    for (x in 0 until width) {
        for (y in 0 until height) {
            val c = Pixel(this[x, y])
            val t = Pixel(cover[x, y])
            val r = c.coverBy(t)
            this[x, y] = r
        }
    }
}