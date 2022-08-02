package steam.content

import arc.math.Interp
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootAlternate
import mindustry.entities.pattern.ShootMulti
import mindustry.entities.pattern.ShootSpread
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.production.AttributeCrafter
import mindustry.world.blocks.production.Pump
import mindustry.world.blocks.production.SolidPump
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BuildVisibility
import mindustry.world.meta.Env
import steam.R
import steam.UndebugOnly
import steam.gen.OreGenerator
import steam.utils.plus
import steam.world.crafting.MultiCrafter
import steam.world.crafting.TemperatureCrafter
import steam.world.distribution.Node
import steam.world.drawer.DrawReservoir
import steam.world.effect.HeatAccumulator
import steam.world.heating.FluidCombustor
import steam.world.heating.ItemBurner
import steam.world.mech.MechPad

object SteamBlocks {
    //should be listed all at once
    //turret
    lateinit var rifle: Block
    //drill - production
    lateinit var well: Block
    lateinit var quartzExtractor: Block
    //crafting
    lateinit var boiler: Block
    lateinit var blastFurnace: Block
    //crafting - heating
    lateinit var burner: ItemBurner
    lateinit var fluidBurner: FluidCombustor
    //liquid
    lateinit var reservoir: Block
    //pressure
    lateinit var pressureNode: Block
    //effect
    lateinit var coreFragment: Block
    lateinit var mechPad: Block
    //sandbox
    lateinit var heatAccumulator: HeatAccumulator
    //env
    lateinit var oreIron: OreBlock

    fun rifle() {
        rifle = ItemTurret("rifle").apply {
            reload = 25f
            health = 660
            category = Category.turret
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 40, Items.copper + 35, SteamItems.iron + 20
                )
            }
            consumeLiquid(SteamFluids.steam, 0.05f)
            shoot = ShootMulti(ShootSpread(5, 8f), ShootAlternate(4f))
            velocityRnd = 0.2f
            shootY = 6.75f
            size = 2

            drawer = DrawTurret().apply {
                parts.addAll(
                    RegionPart("-barrel-l").apply {
                        moveY = -1.5f
                        progress = PartProgress.recoil
                        under = true
                    },
                    RegionPart("-barrel-r").apply {
                        moveY = -1.5f
                        progress = PartProgress.recoil.delay(0.5f)
                        under = true
                    }
                )
            }

            ammo(
                SteamItems.stone, BasicBulletType(3.5f, 3f).apply {
                    width = 7f
                    height = 9f
                    lifetime = 60f
                    ammoMultiplier = 1f
                },
                Items.copper, BasicBulletType(4.5f, 6f).apply {
                    width = 7f
                    height = 9f
                    lifetime = 60f
                    trailColor = backColor
                    trailLength = 6
                },
                Items.graphite, BasicBulletType(5.5f, 12f).apply {
                    width = 9f
                    height = 12f
                    reloadMultiplier = 0.6f
                    ammoMultiplier = 3f
                    lifetime = 60f
                    trailColor = backColor
                    trailLength = 7
                    rangeChange = 18f
                },
                Items.coal, BasicBulletType(4.5f, 8f).apply {
                    width = 8f
                    height = 12f
                    lifetime = 60f
                    makeFire = true
                    status = StatusEffects.burning
                    statusDuration = 5 * 60f
                    backColor = Pal.lightOrange
                    frontColor = Pal.lightishOrange
                    trailColor = backColor
                    trailLength = 6
                }
            )
            limitRange()
        }
    }
    fun quartzExtractor() {
        quartzExtractor = AttributeCrafter("quartz-extractor").apply {
            category = Category.production
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 30, Items.copper + 20, SteamItems.iron + 25
                )
            }
            minEfficiency = 0.01f
            maxBoost = 4f
            updateEffect = Fx.coalSmeltsmoke
            updateEffectChance = 0.15f
            craftEffect = Fx.smeltsmoke
            size = 2
            health = 340
            attribute = Attribute.sand
            baseEfficiency = 0f
            consumeLiquid(SteamFluids.steam, 0.05f)
            craftTime = 240f
            outputItem = SteamItems.quartz + 3
            drawer = DrawMulti(DrawDefault(), DrawRegion("-rotator").apply { rotateSpeed = 3f; spinSprite = true }, DrawRegion("-top"))
        }
    }
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
    fun blastFurnace() {
        blastFurnace = MultiCrafter("blast-furnace").apply {
            warmupSpeed = 0.03f
            size = 3
            health = 800
            configurable = false
            itemCapacity = 80
            for (ore in OreGenerator.all) {
                if(ore.key.radioactivity <= 0f)
                recipes.add(MultiCrafter.Recipe(80f, arrayOf(ore.value + 1), arrayOf(ore.key + 1)))
            }
            squareSprite = false
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 90
                )
            }
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
            hasPower = false
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
    fun pressureNode() {
        pressureNode = Node("pressure-pipe").apply {
            health = 120
            category = Category.distribution
            buildVisibility = BuildVisibility.shown

            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 10, Items.copper + 5, SteamItems.iron + 5
                )
            }
        }
    }
    fun coreFragment() {
        coreFragment = CoreBlock("core-fragment").apply {
            size = 3
            isFirstTier = true
            itemCapacity = 3200
            health = 900
            armor = 5f
            unitCapModifier = 12
            unitType = SteamUnitTypes.epsilon

            category = Category.effect
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 1200, Items.copper + 1200, Items.lead + 500
                )
            }
        }
    }
    fun mechPad() {
        mechPad = MechPad("mech-pad").apply {
            size = 2
            health = 800

            mech = SteamUnitTypes.epsilon
            consumePower(1.2f)
            category = Category.effect
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 120, Items.copper + 80, Items.lead + 50, Items.graphite + 35
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
    //env
    fun ironOre() {
        oreIron = OreBlock(SteamItems.iron).apply {
            oreDefault = true
            oreThreshold = 0.864f
            oreScale = 24.904762f
        }
    }
}
