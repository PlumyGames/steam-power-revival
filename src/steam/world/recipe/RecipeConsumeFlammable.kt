package steam.world.recipe

import arc.func.Boolf
import mindustry.gen.Building
import mindustry.type.Item

open class RecipeConsumeFlammable(
    var minFlammability: Float,
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
}
