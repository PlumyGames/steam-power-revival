package steam.content

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Geometry
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
import mindustry.world.blocks.power.ConsumeGenerator
import mindustry.world.blocks.production.AttributeCrafter
import mindustry.world.blocks.production.Pump
import mindustry.world.blocks.production.SolidPump
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BuildVisibility
import mindustry.world.meta.Env
import steam.R
import steam.UndebugOnly
import steam.gen.OreGenerator
import steam.utils.NewEffect
import steam.utils.plus
import steam.world.crafting.MultiCrafter
import steam.world.crafting.Separator
import steam.world.crafting.TemperatureCrafter
import steam.world.crafting.addRecipe
import steam.world.distribution.PressureBridge
import steam.world.distribution.PressurePipe
import steam.world.drawer.DrawBuilding
import steam.world.drawer.DrawLiquidWarmup
import steam.world.drawer.DrawReservoir
import steam.world.drawer.DrawSteamInside
import steam.world.effect.HeatAccumulator
import steam.world.heating.FluidCombustor
import steam.world.heating.ItemBurner
import steam.world.mech.MechPad
import steam.world.pressure.PressureProducer
import steam.world.pressure.PressureSource
import steam.world.pressure.PressureVoid
import steam.world.temp.DrawOverheat
import steam.world.temp.celsius

object SteamBlocks {
    //should be listed all at once
    //turret
    lateinit var rifle: Block
    //drill - production
    lateinit var well: Block
    lateinit var quartzExtractor: Block
    //crafting
    lateinit var boiler: Block
    lateinit var industrialBoiler: Block
    lateinit var blastFurnace: MultiCrafter
    lateinit var advancedFurnace: MultiCrafter
    lateinit var crystallizer: Block
    lateinit var thermalCentrifuge: Block
    //crafting - heating
    lateinit var burner: ItemBurner
    lateinit var fluidBurner: FluidCombustor
    //liquid
    lateinit var reservoir: Block
    //power
    lateinit var turbine: Block
    //pressure
    lateinit var pressureNode: Block
    lateinit var pressureBridge: Block
    //effect
    lateinit var coreFragment: Block
    lateinit var mechPad: Block
    //sandbox
    lateinit var heatAccumulator: HeatAccumulator
    //env
    lateinit var oreIron: OreBlock
    lateinit var pressureSource: PressureSource
    lateinit var pressureVoid: PressureVoid
    lateinit var pressurizer: PressureProducer
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
            drawer = DrawMulti(
                DrawDefault(),
                DrawRegion("-rotator").apply {
                    rotateSpeed = 3f; spinSprite = true
                },
                DrawRegion("-top")
            )
        }
    }

    fun boiler() {
        boiler = TemperatureCrafter("boiler").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 50, Items.copper + 30, SteamItems.glass + 15
                )
            }
            liquidCapacity = 80f
            size = 2
            hasLiquids = true
            consumeLiquid(Liquids.water, 0.2f)
            outputFluid = LiquidStack(SteamFluids.steam, 0.2f)
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidRegion(Liquids.water),
                DrawSteamInside(),
                DrawLiquidTile(SteamFluids.steam),
                DrawDefault(),
                DrawHeatInput().apply { heatColor = R.C.burnerFlame },
                DrawOverheat()
            )
        }
    }

    fun industrialBoiler() {
        industrialBoiler = TemperatureCrafter("industrial-boiler").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.steel + 25, Items.titanium + 50, Items.metaglass + 15, Items.plastanium + 25
                )
            }
            liquidCapacity = 200f
            size = 3
            hasLiquids = true
            squareSprite = false
            maxEfficiency = 3f
            tempCap = 750f.celsius
            consumeLiquid(Liquids.water, 0.2f)
            outputFluid = LiquidStack(SteamFluids.steam, 0.2f)
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidTile(Liquids.water, 1f),
                DrawSteamInside(1.4f),
                DrawLiquidTile(SteamFluids.steam, 1f),
                DrawDefault(),
                DrawHeatInput().apply { heatColor = R.C.burnerFlame },
                DrawOverheat()
            )
            effect = NewEffect(60f) {
                Draw.color(Pal.darkerGray)
                for (i in 0 until 4) {
                    Angles.randLenVectors(
                        this.id.toLong() + i, 8, 16f * this.finpow(), 45f + i * 90f, 10f
                    ) { x, y ->
                        Draw.alpha(fout())
                        Fill.circle(this.x + x + (7f * Geometry.d8edge[i].x), this.y + y + (7f * Geometry.d8edge[i].y), this.fin() * 3f)
                    }
                }
            }
        }
    }

    fun blastFurnace() {
        blastFurnace = MultiCrafter("blast-furnace").apply {
            warmupSpeed = 0.012f
            size = 3
            health = 800
            hasTemp = false
            itemCapacity = 80
            configurable = false

            recipes.oreRecipe(3, 80f, 0.05f)
            addRecipe(60f, inItem = arrayOf(Items.sand + 1), outItem = arrayOf(SteamItems.glass + 1))
            addRecipe(80f, inItem = arrayOf(Items.scrap + 1), outLiquid = arrayOf(Liquids.slag + 0.1f))
            drawer = DrawMulti(
                DrawDefault(),
                DrawGlowRegion().apply { color = R.C.burnerFlame },
                DrawWarmupRegion().apply { color = R.C.burnerFlame; sinMag = 0.2f }
            )
            consume(ConsumeItemFlammable(1f))
            craftTime = 210f
            squareSprite = false
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 80
                )
            }
        }
    }

    fun advancedFurnace() {
        advancedFurnace = MultiCrafter("advanced-furnace").apply {
            warmupSpeed = 0.012f
            size = 3
            health = 1200
            hasTemp = false
            itemCapacity = 80
            configurable = true

            recipes.oreRecipe(4, 50f, 0.05f * 1.6f)
            addRecipe(45f, inItem = arrayOf(Items.sand + 1), outItem = arrayOf(SteamItems.glass + 1))
            addRecipe(80f * 0.625f, inItem = arrayOf(Items.scrap + 1), outLiquid = arrayOf(Liquids.slag + 0.16f))
            addRecipe(
                260f,
                inItem = arrayOf(SteamItems.iron + 2, Items.coal + 3),
                outItem = arrayOf(SteamItems.steel + 1),
                outLiquid = arrayOf(Liquids.slag + 0.04f)
            )
            drawer = DrawMulti(
                DrawDefault(),
                DrawLiquidWarmup(Liquids.slag),
                DrawRegion("-top1"),
                DrawGlowRegion().apply { color = R.C.burnerFlame },
                DrawWarmupRegion().apply { color = R.C.burnerFlame; sinMag = 0.2f }
            )
            consumePower(2.5f)
            craftTime = 210f
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    Items.copper + 90, Items.lead + 40, Items.graphite + 50, SteamItems.iron + 65
                )
            }
        }
    }

    fun crystallizer() {
        crystallizer = Separator("crystallizer").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 20, Items.copper + 5
                )
            }

            health = 90
            results = arrayOf(
                Items.copper + 5,
                Items.lead + 4,
                SteamItems.iron + 3,
            )
            craftTime = 100f
            craftFx = Fx.smeltsmoke
            consumeLiquid(Liquids.slag, 0.1f)
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidTile(Liquids.slag),
                DrawDefault(),
            )
        }
    }

    fun thermalCentrifuge() {
        thermalCentrifuge = Separator("thermal-centrifuge").apply {
            requirements(
                Category.crafting,
                arrayOf(
                    SteamItems.steel + 15,
                    Items.lead + 45,
                    SteamItems.iron + 25
                )
            )
            size = 2
            squareSprite = false
            health = 520
            craftTime = 50f
            consumeItem(OreGenerator.all[Items.thorium])
            consumeLiquid(SteamFluids.acid, 0.1f)
            consumePower(1.2f)
            results = arrayOf(SteamItems.depletedThorium + 10, Items.thorium + 1)
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawBlurSpin("-rotor", 5f).apply {
                    blurThresh = 0.99f
                },
                DrawDefault()
            )
            SteamFluids.acid.acidproof += this
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
                    SteamItems.stone + 80, SteamItems.glass + 10
                )
            }
            drawer = DrawMulti(
                DrawDefault(),
                DrawReservoir(null),
                DrawRegion("-top")
            )
        }
    }

    fun turbine() {
        turbine = ConsumeGenerator("turbine").apply {
            requirements(
                Category.power,
                arrayOf(SteamItems.iron + 30, Items.lead + 45, Items.graphite + 15, Items.silicon + 12)
            )
            size = 2
            health = 400
            consumeLiquid(SteamFluids.steam, 0.1f)
            liquidCapacity = 40f
            powerProduction = 3.1f
            warmupSpeed = 0.03f
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawParticles().apply {
                    color = R.C.steam
                    alpha = 0.9f
                    particleSize = 3f
                    particles = 12
                    rotateScl = 0.7f
                    particleRad = 6f
                    particleLife = 80f
                    reverse = true
                    particleSizeInterp = Interp.exp5Out
                },
                DrawBlurSpin("-rotor", 12f).apply { blurThresh = 0.95f },
                DrawDefault(),
                DrawRegion("-top1")
            )
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

    fun pressurizer() {
        pressurizer = PressureProducer("pressurizer").apply {
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            consumeLiquid(SteamFluids.steam, 0.1f)
            pressureOutput = 5f
            size = 2
            craftTime = 45f
            squareSprite = false
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidTile(SteamFluids.steam, 1f),
                DrawSteamInside(),
                DrawBuilding().apply {
                    for (i in Mathf.signs) {
                        parts.add(RegionPart("-piston").apply {
                            x = 2f * i
                            y = 4f * i
                            moveY = -8f * i
                            progress = PartProgress.constant(0f).absin(craftTime / 3f, 1f).mul(PartProgress.warmup)
                            outline = false
                        })
                    }
                },
                DrawDefault()
            )
        }
    }

    fun pressureNode() {
        pressureNode = PressurePipe("pressure-pipe").apply {
            health = 120
            category = Category.distribution
            buildVisibility = BuildVisibility.shown

            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.stone + 10, SteamItems.iron + 5, Items.graphite + 2
                )
            }
        }
    }

    fun pressureBridge() {
        pressureBridge = PressureBridge("pressure-bridge").apply {
            requirements(
                Category.distribution,
                arrayOf(Items.lead + 10, Items.graphite + 15, SteamItems.steel + 10)
            )
            health = 120
            squareSprite = false
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
    // sandbox only
    fun heatAccumulator() {
        heatAccumulator = HeatAccumulator("heat-accumulator").apply {
            category = Category.effect
            buildVisibility = BuildVisibility.sandboxOnly
            size = 4
        }
    }

    fun pressureSource() {
        pressureSource = PressureSource("pressure-source").apply {
            category = Category.effect
            buildVisibility = BuildVisibility.sandboxOnly
            size = 1
        }
    }

    fun pressureVoid() {
        pressureVoid = PressureVoid("pressure-void").apply {
            category = Category.effect
            buildVisibility = BuildVisibility.sandboxOnly
            size = 1
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
