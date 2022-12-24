package steam.world.recipe

import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block

class Process(
    val recipes: ArrayList<CrafterRecipe> = arrayListOf(),
    val name: String = "",
    val group: Int = 0 //group of process, for displaying stats in groups
) {
    lateinit var allConsumer: List<RecipeConsume>
    lateinit var allOutItems: List<Item>
    lateinit var allOutLiquids: List<Liquid>

    fun initialize(block: Block) {
        recipes.forEach { it.initialize(block) }
        allConsumer = recipes.flatMap { it.consumer.toList() }
        allOutItems = recipes.flatMap { it.allOutItems }
        allOutLiquids = recipes.flatMap { it.allOutLiquids }
    }
}