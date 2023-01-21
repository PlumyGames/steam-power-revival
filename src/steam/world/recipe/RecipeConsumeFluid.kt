package steam.world.recipe

import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.type.LiquidStack
import mindustry.ui.LiquidDisplay
import mindustry.world.Block

class RecipeConsumeFluid(
    vararg liquid: LiquidStack
) : RecipeConsume() {
    var liquids = liquid.asList()

    override fun initialize(block: Block) {
        block.hasLiquids = true
        liquids.forEach {
            block.liquidFilter[it.liquid.id.toInt()] = true
        }
    }

    override fun trigger(build: Building) {
        liquids.forEach {
            build.liquids.remove(it.liquid, it.amount * build.edelta())
        }
    }

    override fun valid(build: Building): Boolean {
        return liquids.all {
            build.edelta() * build.efficiencyScale() * it.amount <= build.liquids.get(it.liquid)
        }
    }

    override fun displayTable(table: Table, recipe: CrafterRecipe) {
        liquids.forEach {
            table.add(LiquidDisplay(it.liquid, it.amount * 60f, true))
        }
    }
}