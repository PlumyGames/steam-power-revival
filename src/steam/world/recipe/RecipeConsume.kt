package steam.world.recipe

import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.world.Block

abstract class RecipeConsume(
    val required: Boolean = true
) {
    //efficiency added when the recipe is active
    var bonusEfficiency = 0f

    //process efficiency factor
    fun efficiency(): Float {
        return 1f
    }
    //consume display
    open fun build(build: Building, table: Table) {}
    //stats display
    open fun displayTable(table: Table, recipe: CrafterRecipe) {}
    open fun initialize(block: Block) {}
    open fun trigger(build: Building) {}

    open fun valid(build: Building): Boolean {
        return true
    }
}