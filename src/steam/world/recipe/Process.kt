package steam.world.recipe

import arc.Core
import arc.graphics.g2d.TextureRegion
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block
import plumy.core.assets.EmptyTR

class Process(
    val recipes: ArrayList<CrafterRecipe> = arrayListOf(),
    val name: String = "",
    val group: Int = 0, //group of process, for displaying stats in groups
    var customIcon: Boolean = false
) {
    var icon = EmptyTR

    lateinit var allConsumer: List<RecipeConsume>
    lateinit var allOutItems: List<Item>
    lateinit var allOutLiquids: List<Liquid>

    //todo rework icon & add icon generation
    fun icon(): TextureRegion {
        return if (customIcon) icon else recipes[0].mainOut.uiIcon
    }

    fun initialize(block: Block) {
        recipes.forEach { it.initialize(block) }
        allConsumer = recipes.flatMap { it.consumer.toList() }
        allOutItems = recipes.flatMap { it.allOutItems }
        allOutLiquids = recipes.flatMap { it.allOutLiquids }
    }

    fun load(block: Block) {
        icon = Core.atlas.find(name)
    }
}