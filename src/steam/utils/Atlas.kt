@file:Suppress("UNCHECKED_CAST")

package steam.utils

import arc.Core
import arc.graphics.g2d.TextureRegion

fun String.frames(number: Int, suffix: String = "-") =
    Array<TextureRegion>(number) {
        Core.atlas.find("$this$suffix$it")
    }
/**
 * Slice sprites in order of
 * ```
 * 1 2 3 4
 * 5 6 7 8
 * ```
 */
fun String.slice(
    width: Int,
    height: Int = width,
) = Core.atlas.find(this).slice(width, height)
/**
 * Slice sprites in order of
 * ```
 * 1 2 3 4
 * 5 6 7 8
 * ```
 */
fun TextureRegion.slice(
    width: Int,
    height: Int = width,
) = this.split(width, height).flatten().toTypedArray()

fun String.sheetOneDirection(
    count: Int,
    byRow: Boolean = true,
) = Core.atlas.find(this).sheetOneDirection(count, byRow)

fun TextureRegion.sheetOneDirection(
    count: Int,
    byRow: Boolean = true,
) = run {
    val width = if (byRow) width / count else width
    val height = if (byRow) height / count else height
    if (byRow)
        this.split(width, height).run {
            Array(count) { i -> this[i][0] }
        }
    else
        this.split(width, height).run {
            Array(count) { i -> this[0][i] }
        }
}

fun String.sheet(
    count: Int,
    byRow: Boolean = true,
) = Core.atlas.find(this).sheet(count, byRow)

fun TextureRegion.sheet(
    count: Int,
    byRow: Boolean = true,
) = sheetOneDirection(count, byRow)