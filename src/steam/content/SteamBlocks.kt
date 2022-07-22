package steam.content

import arc.graphics.Color
import mindustry.content.Items
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.*
import mindustry.world.meta.BuildVisibility
import steam.UndebugOnly
import steam.utils.invoke
import steam.utils.plus
import steam.world.crafting.TemperatureBlock
import steam.world.heating.ItemBurner

object SteamBlocks {
    //should be listed all at once
    lateinit var boiler: Block
    lateinit var burner: ItemBurner

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
            hasLiquids = true
            drawer = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault())
        }
    }
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
            drawer = DrawMulti(
                DrawDefault(),
                DrawHeatOutput().apply { heatColor = Color.valueOf("ff9b59") },
                DrawWarmupRegion()
            )
            heatConvertFactor = 4f
            heatingTimeFactor = 90f
        }
    }
}
