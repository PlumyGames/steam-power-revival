package steam.world.drawer

import arc.struct.Seq
import mindustry.entities.part.DrawPart
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.draw.DrawBlock

class DrawBuilding : DrawBlock() {
    val parts = Seq<DrawPart>()

    override fun load(block: Block) {
        parts.forEach { it.load(block.name) }
    }

    override fun draw(build: Building) {
        val params = DrawPart.params.set(build.warmup(), build.progress(), build.progress(), 1 - build.progress(), 0f, 0f, build.x, build.y, 90f)
        parts.forEach { it.draw(params) }
    }
}