package steam.world.drawer

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawConstruct : DrawBlock() {
    var layer = Layer.blockAdditive

    lateinit var constructRegion: TextureRegion

    override fun draw(b: Building) {
        Draw.draw(layer) {
            Drawf.construct(b, constructRegion, 0f, b.progress(), b.warmup(), b.totalProgress())
        }
    }

    override fun load(block: Block) {
        super.load(block)
        constructRegion = Core.atlas.find("${block.name}-construct")
    }
}