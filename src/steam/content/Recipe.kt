package steam.content

import mindustry.content.Items
import mindustry.content.Liquids
import plumy.dsl.plus
import steam.gen.OreGenerator
import steam.world.crafting.MultiCrafter
import steam.world.crafting.MultiCrafter.Recipe

class OreProcess(
    hardness: Int,
    time: Float,
    slag: Float,
    inAmount: Int = 1,
    outAmount: Int = 1,
    requireFuel: Boolean = false,
    fuelEfficiency: Float = 1f,
    minFlammability: Float = 1f
) : MultiCrafter.Process() {
    init {
        for ((raw, ore) in OreGenerator.rawOres) {
            if (raw.radioactivity <= 0f && raw.hardness <= hardness) recipes.addAll(
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
                        arrayOf(Items.pyratite+1),
                        required = false
                    )
                )
            )
        }
    }
}