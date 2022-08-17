package steam.utils

import arc.Core
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf

fun String.frames(number: Int, suffix: String = "-") =
    Array<TextureRegion>(number) {
        Core.atlas.find("$this$suffix$it")
    }

fun String.sheet(
    tileWidth: Int,
    tileHeight: Int = tileWidth,
) = Core.atlas.find(this).sheet(tileWidth, tileHeight)
/**
 * Slice sprites in order of
 * ```
 * 1 2 3 4
 * 5 6 7 8
 * ```
 */
fun TextureRegion.sheet(
    tileWidth: Int,
    tileHeight: Int = tileWidth,
    texSize: Int = 32
): Array<TextureRegion> {
    val size = tileWidth * tileHeight
    val sheetW = (u2 - u) / tileWidth
    val sheetH = (v2 - v) / tileHeight
    return Array(size) {
        val x = ((it % tileWidth).toFloat() / tileWidth)
        val y = ((it / tileWidth).toFloat() / tileHeight)
        val r = TextureRegion(this)

        //small amount of margin to prevent anti-aliasing causing weird lines
        r.u = Mathf.map(x, u, u2) + sheetW * 0.01f
        r.v = Mathf.map(y, v, v2) + sheetH * 0.01f

        r.u2 = r.u + sheetW * 0.98f
        r.v2 = r.v + sheetH * 0.98f
        r.width = texSize
        r.height = texSize
        r
    }
}

fun String.sheetOneDirection(
    number: Int,
    byRow: Boolean = true,
) = Core.atlas.find(this).sheetOneDirection(number, byRow)

fun TextureRegion.sheetOneDirection(
    number: Int,
    byRow: Boolean = true,
) = run {
    val width = if (byRow) width / number else width
    val height = if (byRow) height else height / number
    if (byRow)
        this.split(width, height).run {
            Array(number) { i -> this[i][0] }
        }
    else
        this.split(width, height).run {
            Array(number) { i -> this[0][i] }
        }
}
