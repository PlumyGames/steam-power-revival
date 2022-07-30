package steam.utils

import arc.Core
import arc.graphics.g2d.TextureRegion

fun String.frames(number: Int, suffix: String = "-") =
    Array<TextureRegion>(number) {
        Core.atlas.find("$this$suffix$it")
    }

fun String.sheet(
    width: Int,
    height: Int = width,
) = Core.atlas.find(this).sheet(width, height)
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
): Array<TextureRegion> {
    val row = height / tileWidth
    val column = width / tileHeight
    return Array(row * column) { i ->
        val rowByColumn = split(tileWidth, tileHeight)
        rowByColumn[i % column][i / column]
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
