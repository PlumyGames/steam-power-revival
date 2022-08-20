package steam.world.drawer

import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawBlock

class DrawLiquidWarmup(var drawLiquid: Liquid, var padding: Float = 0f) : DrawBlock() {

    override fun draw(build: Building) {
        LiquidBlock.drawTiledFrames(
            build.block.size,
            build.x, build.y,
            padding, drawLiquid,
            build.warmup()
        )
    }
}