package steam.world.recipe

import arc.Core
import arc.Core.bundle
import arc.func.Boolf
import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.type.Item
import steam.utils.addTable

open class RecipeConsumeFlammable(
    var minFlammability: Float = 1f,
    var threshold: Float = 1f
) : RecipeConsumeItemFilter() {
    init {
        filter = Boolf { item: Item -> item.flammability >= minFlammability }
    }

    override fun efficiencyBonus(build: Building): Float {
        val item = getConsumed(build)
        return if (item != null) item.flammability - threshold else 0f
    }

    override fun valid(build: Building): Boolean {
        val item = getConsumed(build)
        return if (item != null) item.flammability >= minFlammability else false
    }

    override fun displayTable(table: Table, recipe: CrafterRecipe) {
        table.addTable{
            image(Core.atlas.find("status-burning")).padRight(5f)
            add(bundle["stat.consumeFuel"])
        }
    }
}
