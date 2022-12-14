package steam.content

import mindustry.content.Liquids
import plumy.dsl.plus
import steam.gen.OreGenerator
import steam.world.crafting.MultiCrafter
import steam.world.recipe.CrafterRecipe
import steam.world.recipe.Process
import steam.world.recipe.RecipeConsumeItem
import steam.world.recipe.RecipeConsumeFlammable

fun MultiCrafter.oreRecipe (
    hardness: Int,
    time: Float,
    slag: Float,
    inAmount: Int = 1,
    outAmount: Int = 1,
    requireFuel: Boolean = false,
    fuelTime: Float = 120f,
    minFlammability: Float = 1f
) {
    val grouped = ++groupSize
    for ((raw, ore) in OreGenerator.rawOres) {
        if (raw.radioactivity <= 0f && raw.hardness <= hardness) {
            val recipes = arrayListOf(
                CrafterRecipe(
                    time,
                    arrayOf(RecipeConsumeItem(ore + 1)),
                    outItem = arrayOf(raw + outAmount),
                    outLiquid = arrayOf(Liquids.slag + slag)
                )
            )
            if (requireFuel) recipes.add(
                CrafterRecipe(
                    fuelTime,
                    arrayOf(RecipeConsumeFlammable(minFlammability))
                )
            )
            processes.add(
                Process(
                    recipes,
                    raw.localizedName + " Ore Smelting",
                    grouped
                )
            )
        }
    }
}