package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import mindustry.content.Fx
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
    lateinit var recipeList: RecipeList
    var warmupSpeed = 0.1f
    var craftEffect = Fx.smeltsmoke

    class Recipe(
        val craftTime: Float,
        val inItem: Array<ItemStack> = emptyArray(),
        val outItem: Array<ItemStack> = emptyArray(),
    ) {
        val allInItems = inItem.map { it.item }
        val allOutItems = outItem.map { it.item }
        val allItems = (allInItems + allOutItems).distinct()
    }

    class RecipeList(
        val recipes: List<Recipe>,
    ) {
        constructor(vararg recipes: Recipe) : this(recipes.toList())

        val allInItems = recipes.flatMap { it.allInItems }
        val allOutItems = recipes.flatMap { it.allOutItems }
        val allItems = (allInItems + allOutItems).distinct()
    }

    init {
        solid = true
        update = true
        hasItems = true
        configurable = true
        saveConfig = true
        buildType = Prov { MultiCrafterBuild() }

        config(java.lang.Integer::class.java) { tile: MultiCrafterBuild, i ->
            if (!configurable) return@config
            val new = i.toInt()
            if (tile.curRecipeIdx != new) {
                tile.curRecipeIdx = if (new < 0) -1 else new.coerceIn(0, recipes.size - 1)
                tile.progress = 0f
            }
        }
    }

    override fun init() {
        super.init()
        recipeList = RecipeList(recipes)
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

            if (!configurable) updateRecipe()
            if (enabledRecipe) {
                if (canCraft()) {
                    if (progress >= 1f) {
                        craft()
                    } else progress += getProgressIncrease(currentRecipe.craftTime) * warmup
                    totalProgress += edelta()
                    warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed)
                }
                dumpOutputs()
            } else warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed)
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return this.items.get(item) < this.getMaximumAccepted(item) && recipeList.allInItems.contains(item)
        }

        fun dumpOutputs() {
            if (!configurable) for (recipe in recipes) {
                for (output in recipe.outItem) dump(output.item)
            } else for (output in currentRecipe.outItem) {
                dump(output.item)
            }
        }

        fun craft() {
            items.remove(currentRecipe.inItem)

            for (output in currentRecipe.outItem) {
                for (i in 0 until output.amount) {
                    offload(output.item)
                }
            }

            if (wasVisible) craftEffect.at(this)

            progress %= 1f
        }

        fun canCraft(): Boolean {
            return items.has(currentRecipe.inItem)
        }

        override fun progress() = progress
        override fun warmup() = warmup
        override fun totalProgress() = totalProgress
    }
}