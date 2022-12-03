package steam.content

import mindustry.content.Items
import mindustry.content.Liquids
import plumy.dsl.plus
import steam.gen.OreGenerator
import steam.world.crafting.MultiCrafter
import steam.world.crafting.MultiCrafter.Recipe

fun ArrayList<MultiCrafter.Process>.oreRecipe(
    hardness: Int,
    time: Float,
    slag: Float,
    inAmount: Int = 1,
    outAmount: Int = 1,
    requireFuel: Boolean = false,
    fuelEfficiency: Float = 1f,
    minFlammability: Float = 1f
) {
    for ((raw, ore) in OreGenerator.rawOres) {
        if (raw.radioactivity <= 0f && raw.hardness <= hardness)this.add(
            MultiCrafter.Process(
                arrayListOf(
                    Recipe(
                        craftTime = time,
                        inItem = arrayOf(ore + inAmount),
                        outItem = arrayOf(raw + outAmount),
                        outLiquid = arrayOf(Liquids.slag + slag)
                    ),
                    //testing
                    Recipe(
                        30f,
                        arrayOf(Items.titanium+1),
                        arrayOf(Items.pyratite+1)
                    )
                )
            )
        )
    }
}

fun ArrayList<Recipe>.orePowderRecipe(hardness: Int, time: Float, slag: Float, inAmount: Int = 3, outAmount: Int = 2) {
    for ((raw, ore) in OreGenerator.powders) {
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