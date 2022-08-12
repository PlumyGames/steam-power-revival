package steam.content

import arc.math.geom.Geometry
import mindustry.content.Blocks.*
import mindustry.content.Items
import mindustry.content.UnitTypes.alpha
import mindustry.content.UnitTypes.beta
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.type.Category
import mindustry.type.UnitType
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquid
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import mindustry.world.meta.BuildVisibility
import steam.utils.plus
import steam.utils.sheet
import steam.world.distribution.ElectricConveyor
import steam.world.drawer.DrawBuilding
import steam.world.pressure.PressureCrafter

object ContentsOverrider {
    fun mechanicalDrill() {
        (mechanicalDrill as Drill).apply {
            removeConsumer(findConsumer { it is ConsumeLiquid })
            consumeLiquid(SteamFluids.steam, 0.05f)
            drillTime = 300f
            liquidBoostIntensity = 1f
        }
    }

    fun pneumaticDrill() {
        (pneumaticDrill as Drill).apply {
            removeConsumer(findConsumer { it is ConsumeLiquid })
            consumeLiquid(SteamFluids.steam, 0.05f)
            drillTime = 200f
            liquidBoostIntensity = 1f
        }
    }

    fun conveyor() {
        conveyor.buildVisibility = BuildVisibility.hidden
        titaniumConveyor.buildVisibility = BuildVisibility.hidden
        plastaniumConveyor.buildVisibility = BuildVisibility.hidden
        armoredConveyor.buildVisibility = BuildVisibility.hidden
        conveyor = ElectricConveyor("electric-conveyor").apply {
            requirements(Category.distribution, arrayOf(Items.copper + 1, Items.lead + 1, SteamItems.iron + 1), true)
            health = 60
            speed = 0.08f
            displayedSpeed = 10.5f
            buildCostMultiplier = 2f
            hasPower = true
            consumesPower = true
            conductivePower = true
            consumePower(0.01f)
        }
        //}
    }

    fun conduit() {
        conduit.apply {
            requirements = arrayOf(SteamItems.glass + 1)
        }
    }

    fun siliconSmelter() {
        (siliconSmelter as GenericCrafter).apply {
            removeConsumer(findConsumer { it is ConsumeItems })
            consumeItems(Items.coal + 1, SteamItems.quartz + 1)
        }
    }

    fun kiln() {
        (kiln as GenericCrafter).apply {
            removeConsumer(findConsumer { it is ConsumeItems })
            consumeItems(Items.lead + 1, SteamItems.quartz + 1, SteamItems.glass + 1)
        }
    }

    fun graphitePress() {
        graphitePress.buildVisibility = BuildVisibility.hidden
        graphitePress = PressureCrafter("graphite-compressor").apply {
            maxEfficiency = 1.5f
            requirements(
                Category.crafting,
                arrayOf(
                    SteamItems.stone + 40,
                    Items.copper + 32,
                    SteamItems.iron + 25,
                )
            )
            size = 2
            consumeItem(Items.coal, 5)
            outputItem = Items.graphite + 2
            craftTime = 100f
            pressureRequired = 2f
            drawer = DrawMulti(DrawRegion("-bottom"), DrawBuilding().apply {
                val regions = "$name-pistons".sheet(12, 12)
                for (i in 0 until 4) {
                    parts.add(RegionPart().apply {
                        region = regions[i]
                        outline = false
                        val xd = Geometry.d8edge[i].x
                        val yd = Geometry.d8edge[i].y
                        x = xd * (22f / 4f)
                        y = yd * (22f / 4f)
                        moveX = xd * -1.25f
                        moveY = yd * -1.25f
                        progress = DrawPart.PartProgress.reload
                    })
                }
            })
        }
    }

    fun alpha() {
        (alpha as UnitType).apply {
            mineTier = 2
        }
    }

    fun beta() {
        (beta as UnitType).apply {
            mineTier = 2
        }
    }

    fun sand() {
        sand.apply {
            attributes.set(Attribute.sand, 0.3f)
        }
        darksand.apply {
            attributes.set(Attribute.sand, 0.3f)
        }
        sandWater.apply {
            attributes.set(Attribute.sand, 0.55f)
        }
        darksandTaintedWater.apply {
            attributes.set(Attribute.sand, 0.55f)
        }
    }

    fun stone() {
        stone.apply {
            itemDrop = SteamItems.stone
        }
        basalt.apply {
            itemDrop = SteamItems.stone
        }
        craters.apply {
            itemDrop = SteamItems.stone
        }
        charr.apply {
            itemDrop = SteamItems.stone
        }
    }

    fun items() {
        Items.titanium.hardness = 4
        Items.thorium.hardness = 5
    }
}