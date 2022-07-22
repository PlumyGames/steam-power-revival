package steam.content

import mindustry.content.Items
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawHeatOutput
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.BuildVisibility
import steam.UndebugOnly
import steam.utils.invoke
import steam.utils.plus
import steam.world.crafting.TemperatureBlock
import steam.world.heating.ItemBurner

object SteamBlocks {
    lateinit var boiler: Block
    fun boiler() {
        boiler = TemperatureBlock("boiler").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    Items.copper + 20
                )
            }
            size = 2
        }
    }

    lateinit var burner: ItemBurner
    fun burner() {
        burner = ItemBurner("burner")() {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    Items.copper + 20
                )
            }
            size = 1
            drawer = DrawMulti(DrawDefault(), DrawHeatOutput())
        }
    }
}
