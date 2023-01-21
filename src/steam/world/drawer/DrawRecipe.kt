package steam.world.drawer

import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import steam.world.crafting.MultiCrafter

class DrawRecipe : DrawBlock() {
    override fun draw(build: Building) {
        if (build !is MultiCrafter.MultiCrafterBuild) return
        (build.block as MultiCrafter).processList.allRecipe.forEach {
            it.drawer?.draw(build)
        }
    }

    override fun load(block: Block) {
        if (block !is MultiCrafter) return
        block.processList.allRecipe.forEach {
            it.drawer?.load(block) ?: return@forEach
        }
    }
}