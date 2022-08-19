package steam.content

import mindustry.content.Liquids
import plumy.dsl.plus
import steam.gen.OreGenerator
import steam.world.crafting.MultiCrafter.Recipe

fun ArrayList<Recipe>.oreRecipe(hardness: Int, time: Float, slag: Float, inAmount: Int = 1, outAmount: Int = 1) {
    for ((raw, ore) in OreGenerator.rawOres) {
        if (raw.radioactivity <= 0f && raw.hardness <= hardness)
            this.add(
                Recipe(
                    craftTime = time,
                    inItem = arrayOf(ore + inAmount),
                    outItem = arrayOf(raw + outAmount),
                    outLiquid = arrayOf(Liquids.slag + slag)
                )
            )
    }
}

fun ArrayList<Recipe>.orePowderRecipe(hardness: Int, time: Float, inAmount: Int = 2, outAmount: Int = 3) {
    for ((raw, ore) in OreGenerator.powders) {
        if (raw.radioactivity <= 0f && raw.hardness <= hardness)
            this.add(
                Recipe(
                    craftTime = time,
                    inItem = arrayOf(ore + inAmount),
                    outItem = arrayOf(raw + outAmount),
                )
            )
    }
}