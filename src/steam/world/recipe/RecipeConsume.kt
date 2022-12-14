package steam.world.recipe

import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.world.Block

abstract class RecipeConsume(
    val required: Boolean = true
) {
    //process efficiency factor
    open fun efficiencyBonus(build: Building): Float {
        return 0f
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