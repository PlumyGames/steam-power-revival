package steam.content

import mindustry.content.Items
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import steam.world.crafting.TemperatureBlock

object SteamBlocks {
    lateinit var boiler: Block

    fun load() {
        boiler = TemperatureBlock("boiler").apply {
            requirements(
                Category.crafting,
                ItemStack.with(Items.copper, 20)
            )
            size = 2
        }
    }
}