package steam.utils

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.GlyphLayout
import arc.scene.ui.layout.Scl
import arc.util.Align
import arc.util.pooling.Pools
import mindustry.Vars
import mindustry.graphics.Pal
import mindustry.ui.Fonts
import plumy.world.WorldXY

/**
 * Draw text and underline in default size
 * @return the width of text
 */
fun drawTextEasy(
    text: String,
    x: WorldXY,
    y: WorldXY,
    color: Color = Pal.accent,
    scale: Float = 1f,
): WorldXY {
    if (Vars.renderer.pixelator.enabled()) return 0f
    val font = Fonts.outline
    val layout = Pools.obtain(GlyphLayout::class.java, ::GlyphLayout)
    val ints = font.usesIntegerPositions()
    font.setUseIntegerPositions(false)
    font.data.setScale(1f / 4f / Scl.scl(scale))
    layout.setText(font, text)
    val width = layout.width
    font.color = color
    font.draw(text, x, y + layout.height, Align.center)
    font.setUseIntegerPositions(ints)
    font.color = Color.white
    font.data.setScale(1f)
    Draw.reset()
    Pools.free(layout)
    return width
}