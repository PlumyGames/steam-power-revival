package steam.world.recipe

import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.type.ItemStack
import mindustry.ui.ItemDisplay
import mindustry.world.Block

class RecipeConsumeItem(
    vararg item: ItemStack
) : RecipeConsume() {
    var items = item.asList()

    override fun initialize(block: Block) {
        block.hasItems = true
        block.acceptsItems = true
        items.forEach {
            block.itemFilter[it.item.id.toInt()] = true
        }
    }

    override fun trigger(build: Building) {
        return build.items.remove(items)
    }

    override fun valid(build: Building): Boolean {
        return build.items.has(items)
    }

    override fun displayTable(table: Table, recipe: CrafterRecipe) {
        items.forEach {
            table.add(ItemDisplay(it.item, it.amount, recipe.craftTime, false).left())
        }
    }
}