package steam.content

import arc.math.Interp
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.blocks.production.Pump
import mindustry.world.blocks.production.SolidPump
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BuildVisibility
import mindustry.world.meta.Env
import steam.R
import steam.UndebugOnly
import steam.utils.plus
import steam.world.crafting.TemperatureCrafter
import steam.world.drawer.DrawReservoir
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
    //liquid
    lateinit var reservoir: Block
    lateinit var well: Block
    //sandbox
    lateinit var heatAccumulator: HeatAccumulator
    fun boiler() {
        boiler = TemperatureCrafter("boiler").apply {
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
            consumeLiquid(Liquids.water, 0.2f)
            outputFluid = LiquidStack(SteamFluids.steam, 0.2f)
            drawer = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(Liquids.water),
                DrawParticles().apply {
                    color = R.C.steam
                    alpha = 0.3f
                    particleSize = 2.5f
                    particles = 8
                    particleRad = 4f
                    particleLife = 80f
                    reverse = true
                    particleSizeInterp = Interp.one
                }, DrawLiquidTile(SteamFluids.steam, 0f), DrawDefault(),
                DrawHeatInput().apply { heatColor = R.C.burnerFlame }
            )
        }
    }

    fun burner() {
        burner = ItemBurner("burner").apply {
            squareSprite = false
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 30, Items.copper + 15
                )
            }
            size = 1
            drawer = DrawMulti(
                DrawRegion("-bottom"),
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
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 60, Items.copper + 30, Items.metaglass + 25
                )
            }
            heatConvertFactor = 8f
            size = 2
            health = 350
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidRegion(),
                DrawDefault(),
                DrawHeatOutput().apply { heatColor = R.C.burnerFlame },
                DrawWarmupRegion()
            )
        }
    }
    fun reservoir() {
        reservoir = Pump("reservoir").apply {
            liquidCapacity = 80f
            squareSprite = false
            pumpAmount = 0.05f
            size = 2
            health = 350
            category = Category.liquid
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 80
                )
            }
            drawer = DrawMulti(DrawDefault(), DrawReservoir(null), DrawRegion("-top"))
        }
    }
    fun well() {
        well = SolidPump("well").apply {
            liquidCapacity = 80f
            pumpAmount = 0.05f
            size = 2
            health = 220
            category = Category.production
            buildVisibility = BuildVisibility.shown
            attribute = Attribute.water
            envRequired = envRequired or Env.groundWater
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 80
                )
            }
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
