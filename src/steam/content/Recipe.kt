package steam.content

import mindustry.content.Liquids
import steam.gen.OreGenerator
import steam.utils.plus
import steam.world.crafting.MultiCrafter.Recipe

fun ArrayList<Recipe>.oreRecipe(hardness: Int, time: Float, slag: Float) {
    for ((raw, ore) in OreGenerator.all) {
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