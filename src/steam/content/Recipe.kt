package steam.content

import mindustry.content.Liquids
import plumy.dsl.plus
import steam.gen.OreGenerator
import steam.world.crafting.MultiCrafter.Recipe

fun ArrayList<Recipe>.oreRecipe(hardness: Int, time: Float, slag: Float) {
    for ((raw, ore) in OreGenerator.rawOres) {
        if (raw.radioactivity <= 0f && raw.hardness <= hardness)
            this.add(
                Recipe(
                    craftTime = time,
                    inItem = arrayOf(ore + 1),
                    outItem = arrayOf(raw + 1),
                    outLiquid = arrayOf(Liquids.slag + slag)
                )
            )
    }
}