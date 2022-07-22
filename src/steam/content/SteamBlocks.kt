package steam.content

import mindustry.Vars
import mindustry.content.Items
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.*
import mindustry.world.meta.BuildVisibility
import steam.graphic.R
import steam.UndebugOnly
import steam.utils.plus
import steam.world.crafting.TemperatureBlock
import steam.world.effect.HeatAccumulator
import steam.world.heating.ItemBurner

object SteamBlocks {
    //should be listed all at once
    lateinit var boiler: Block
    lateinit var burner: ItemBurner
    lateinit var heatAccumulator: HeatAccumulator
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
            drawer = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(),
                DrawParticles().apply {
                    color = R.C.steam
                    alpha = 0.4f
                    particleSize = 3f
                    particles = 24
                    particleRad = Vars.tilesize * size * 1.5f
                    reverse = true
                    particleLife = 140f
                })
        }
    }

    fun burner() {
        burner = ItemBurner("burner").apply {
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
                DrawHeatOutput().apply { heatColor = R.C.burnerFlame },
                DrawWarmupRegion()
            )
            heatConvertFactor = 5f
            heatingTimeFactor = 90f
        }
    }

    fun heatAccumulator() {
        heatAccumulator = HeatAccumulator("heat-accumulator").apply {
            category = Category.effect
            buildVisibility = BuildVisibility.sandboxOnly
            size = 4
        }
    }
}
