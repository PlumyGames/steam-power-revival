package steam.content

import mindustry.content.Items
import mindustry.content.Liquids
import plumy.dsl.plus
import steam.gen.OreGenerator
import steam.world.crafting.MultiCrafter
import steam.world.crafting.recipe.Process
import steam.world.crafting.recipe.Recipe

fun MultiCrafter.oreRecipe (
    hardness: Int,
    time: Float,
    slag: Float,
    inAmount: Int = 1,
    outAmount: Int = 1,
    requireFuel: Boolean = false,
    fuelEfficiency: Float = 1f,
    minFlammability: Float = 1f
) {
    val grouped = ++groupSize
    for ((raw, ore) in OreGenerator.rawOres) {
        if (raw.radioactivity <= 0f && raw.hardness <= hardness) {
            processes.add(
                Process(
                    arrayListOf(
                        Recipe(
                            craftTime = time,
                            inItem = arrayOf(ore + inAmount),
                            outItem = arrayOf(raw + outAmount),
                            outLiquid = arrayOf(Liquids.slag + slag)
                        ),
                        //testing
                        //todo replace with actual fueling
                        if (raw != Items.copper) Recipe(
                            30f,
                            arrayOf(Items.titanium + 2, Items.plastanium + 1),
                            arrayOf(Items.pyratite + 1),
                            required = false
                        ) else Recipe(
                            30f,
                            arrayOf(Items.silicon + 1),
                            arrayOf(Items.graphite + 1, Items.metaglass + 2),
                            required = false
                        )
                    ),
                    raw.localizedName + " Ore Smelting",
                    grouped
                )
            )
        }
    }
}