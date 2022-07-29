package steam.world.drawer

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawReservoir(val drawLiquid: Liquid?) : DrawBlock() {
    var alpha = 1f
    lateinit var liquidRegion: TextureRegion

    override fun draw(build: Building) {
        val drawn = drawLiquid ?: build.liquids.current()
        Draw.scl(build.liquids[drawn] / build.block.liquidCapacity * alpha)
        Drawf.liquid(liquidRegion, build.x, build.y, alpha, drawn.color)
        Draw.scl()
    }

    override fun load(block: Block) {
        liquidRegion = Core.atlas.find("${block.name}-liquid")
    }
}