package steam.world.crafting.recipe

import mindustry.type.Item
import mindustry.type.Liquid

class Process(
    val recipes: ArrayList<Recipe> = arrayListOf(),
    val name: String = "",
    val group: Int = -1 //group of process, for displaying stats in groups
) {
    lateinit var allInItems: List<Item>
    lateinit var allOutItems: List<Item>
    lateinit var allInLiquids: List<Liquid>
    lateinit var allOutLiquids: List<Liquid>

    fun initialize() {
        allInItems = recipes.flatMap { it.allInItems }
        allOutItems = recipes.flatMap { it.allOutItems }
        allInLiquids = recipes.flatMap { it.allInLiquids }
        allOutLiquids = recipes.flatMap { it.allOutLiquids }
    }
}