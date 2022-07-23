package steam.content

import mindustry.Vars
import mindustry.content.Items
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.*
import mindustry.world.meta.BuildVisibility
import steam.UndebugOnly
import steam.graphic.DrawSteam
import steam.graphic.R
import steam.utils.plus
import steam.world.crafting.Boiler
import steam.world.effect.HeatAccumulator
import steam.world.heating.FluidCombustor
import steam.world.heating.ItemBurner

object SteamBlocks {
    //should be listed all at once
    //crafting
    lateinit var boiler: Block
    //crafting - heating
    lateinit var burner: ItemBurner
    lateinit var fluidBurner: FluidCombustor
    //sandbox
    lateinit var heatAccumulator: HeatAccumulator
    fun boiler() {
        boiler = Boiler("boiler").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    Items.copper + 20
                )
            }
            liquidCapacity = 200f
            size = 2
            hasLiquids = true
            drawer = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(),
                DrawSteam().apply {
                    particleRad = Vars.tilesize * size * 1.5f
                })
        }
    }

    fun burner() {
        burner = ItemBurner("burner").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 30, Items.copper + 15
                )
            }
            size = 1
            drawer = DrawMulti(
                DrawDefault(),
                DrawHeatOutput().apply { heatColor = R.C.burnerFlame },
                DrawWarmupRegion()
            )
            heatConvertFactor = 8f
            heatingTimeFactor = 90f
            health = 90
            regionRotated1 = 1
        }
    }

    fun fluidBurner() {
        fluidBurner = FluidCombustor("liquid-burner").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.sandboxOnly
            heatConvertFactor = 8f
            size = 2
            health = 350
            drawer = DrawMulti(
                DrawLiquidRegion(),
                DrawDefault(),
                DrawHeatOutput().apply { heatColor = R.C.burnerFlame },
                DrawWarmupRegion()
            )
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
