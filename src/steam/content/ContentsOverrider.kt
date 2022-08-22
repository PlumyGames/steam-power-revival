package steam.content

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp.pow2Out
import arc.math.geom.Geometry
import mindustry.content.Blocks.*
import mindustry.content.Items
import mindustry.content.UnitTypes.alpha
import mindustry.content.UnitTypes.beta
import mindustry.entities.bullet.LiquidBulletType
import mindustry.entities.part.DrawPart
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.UnitType
import mindustry.world.blocks.defense.turrets.LiquidTurret
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquid
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import plumy.dsl.NewEffect
import plumy.dsl.addAmmo
import plumy.dsl.drawMulti
import plumy.dsl.plus
import steam.utils.hide
import steam.world.distribution.ElectricConveyor
import steam.world.drawer.DrawBuilding
import steam.world.drawer.regionPart
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
        conveyor.hide()
        titaniumConveyor.hide()
        plastaniumConveyor.hide()
        armoredConveyor.hide()
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
            consumeItems(Items.lead + 1, SteamItems.quartz + 1)
        }
    }

    fun graphitePress() {
        graphitePress.hide()
        graphitePress = PressureCrafter("graphite-compressor").apply {
            maxEfficiency = 1.5f
            requirements(
                Category.crafting,
                arrayOf(
                    SteamItems.stone + 40,
                    Items.copper + 32,
                    Items.lead + 25,
                )
            )
            size = 2
            consumeItem(Items.coal, 5)
            outputItem = Items.graphite + 2
            craftTime = 190f
            pressureRequired = 2f
            drawMulti {
                +DrawRegion("-bottom")
                +DrawBuilding().apply {
                    for (i in 0 until 4) {
                        regionPart("-piston-$i") {
                            outline = false
                            val xd = Geometry.d8edge[i].x
                            val yd = Geometry.d8edge[i].y
                            x = xd * (22f / 4f)
                            y = yd * (22f / 4f)
                            moveX = xd * -1.25f
                            moveY = yd * -1.25f
                            //bad?
                            progress = DrawPart.PartProgress.reload.curve(pow2Out)
                        }
                    }
                }
                +DrawDefault()
            }
            craftEffect = NewEffect(60f) {
                Draw.color(this.color)
                for (i in 0 until 4) {
                    Angles.randLenVectors(
                        this.id.toLong() + i, 8, 10f * this.finpow(), 45f + i * 90f, 10f
                    ) { x, y ->
                        Draw.alpha(fout())
                        Fill.circle(
                            this.x + x + (3.75f * Geometry.d8edge[i].x),
                            this.y + y + (3.75f * Geometry.d8edge[i].y),
                            this.fin() * 3f
                        )
                    }
                }
            }
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
            attributes.set(SteamAttribute.stone, 0.6f)
        }
        basalt.apply {
            itemDrop = SteamItems.stone
            attributes.set(SteamAttribute.stone, 0.6f)
        }
        craters.apply {
            itemDrop = SteamItems.stone
            attributes.set(SteamAttribute.stone, 0.6f)
        }
        charr.apply {
            itemDrop = SteamItems.stone
            attributes.set(SteamAttribute.stone, 0.6f)
        }
    }

    fun tsunami() {
        (tsunami as LiquidTurret).apply {
            addAmmo(SteamFluids.acid, LiquidBulletType(SteamFluids.acid).apply {
                lifetime = 49f
                speed = 4f
                knockback = 1.3f
                puddleSize = 8f
                orbSize = 4f
                drag = 0.001f
                ammoMultiplier = 0.4f
                statusDuration = 60f * 4f
                damage = 2f
                layer = Layer.bullet - 2f
            })
        }
    }

    fun mender() {
        mender.hide()
    }

    fun items() {
        Items.titanium.hardness = 4
        Items.thorium.hardness = 5
    }

    fun water() {
        water.attributes.set(SteamAttribute.sporeGrow, 0.25f)
        deepwater.attributes.set(SteamAttribute.sporeGrow, 0.25f)

        sandWater.attributes.set(SteamAttribute.sporeGrow, 0.175f)
        darksandWater.attributes.set(SteamAttribute.sporeGrow, 0.175f)

        taintedWater.attributes.set(SteamAttribute.sporeGrow, 1f)
        darksandTaintedWater.attributes.set(SteamAttribute.sporeGrow, 1f)
        deepTaintedWater.attributes.set(SteamAttribute.sporeGrow, 1f)
    }
}