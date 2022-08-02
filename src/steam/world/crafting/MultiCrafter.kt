package steam.world.crafting

import arc.math.Mathf
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack

/* todo
*  add liquid support
*  add temp support
*  add stats
*  better acceptItem
*/

class MultiCrafter(name: String) : TemperatureBlock(name) {
    var recipes = ArrayList<Recipe>()
    var warmupSpeed = 0.1f

    class Recipe(
        var craftTime: Float,
        var inItem: Array<ItemStack>?,
        var outItem: Array<ItemStack>?
    )

    init {
        solid = true
        update = true
        hasItems = true
        configurable = true
        saveConfig = true

        config(java.lang.Integer::class.java) { tile: MultiCrafterBuild, i: Integer ->
            if (!configurable) return@config
            val new = i.toInt()
            if (tile.curRecipeIdx != new) {
                tile.curRecipeIdx = if (new < 0) -1 else new.coerceIn(0, recipes.size - 1)
                tile.progress = 0f
            }
        }
    }

    inner class MultiCrafterBuild : TemperatureBuild() {
        var progress = 0f
        var totalProgress = 0f
        var warmup = 0f
        var curRecipeIdx = -1

        val currentRecipe: Recipe
            get() = recipes[curRecipeIdx]
        val enabledRecipe: Boolean
            get() = curRecipeIdx >= 0

        override fun config() = curRecipeIdx
        fun updateRecipe() {
            curRecipeIdx = recipes.indexOfFirst { items.has(it.inItem) }
        }

        override fun updateTile() {
            super.updateTile()

            if(!configurable) updateRecipe()
            if(enabledRecipe && canCraft()) {
                if(progress >= 1f) {
                    craft()
                }
                else progress += getProgressIncrease(currentRecipe.craftTime)
                totalProgress += edelta()
                warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed)
            } else warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed)
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return this.items.get(item) < this.getMaximumAccepted(item)
        }

        fun craft() {
            consume()

            if (currentRecipe.outItem != null) {
                for (output in currentRecipe.outItem!!) {
                    for (i in 0 until output.amount) {
                        offload(output.item)
                    }
                }
            }

            progress %= 1f
        }
        fun canCraft(): Boolean {
            return items.has(currentRecipe.inItem)
        }
    }
}