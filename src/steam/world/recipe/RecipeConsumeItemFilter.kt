package steam.world.recipe

import arc.func.Boolf
import mindustry.Vars.content
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.Block

open class RecipeConsumeItemFilter : RecipeConsume() {
    var filter = Boolf { _: Item -> false }
    override fun initialize(block: Block) {
        block.hasItems = true
        block.acceptsItems = true
        content.items().each(filter)
        { item: Item -> block.itemFilter[item.id.toInt()] = true }
    }

    override fun trigger(build: Building) {
        val item = getConsumed(build)
        if (item != null) build.items.remove(item, 1)
    }

    fun getConsumed(build: Building): Item? {
        return content.items().firstOrNull {
            build.items.has(it) && filter.get(it)
        }
    }
}