package steam.content

import arc.func.Prov
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
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ContinuousTurret
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
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
import plumy.dsl.*
import steam.R
import steam.UndebugOnly
import steam.entities.bullets.ConeBulletType
import steam.gen.OreGenerator
import steam.world.crafting.*
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
import steam.world.power.PressureGenerator
import steam.world.pressure.PressureCranker
import steam.world.pressure.PressureProducer
import steam.world.pressure.PressureSource
import steam.world.pressure.PressureVoid
import steam.world.temp.DrawOverheat
import steam.world.temp.celsius

object SteamBlocks {
    //should be listed all at once
    //turret
    lateinit var rifle: Block
    lateinit var frostbite: Block
    //drill - production
    lateinit var stoneExcavator: Block
    lateinit var well: Block
    lateinit var quartzExtractor: Block
    lateinit var sporePlanter: Block
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
    lateinit var heatRegulator: HeatRegulator
    //liquid
    lateinit var reservoir: Block
    //power
    lateinit var turbine: Block
    lateinit var pneumaticEngine: Block
    //pressure
    lateinit var pressureNode: Block
    lateinit var pressureBridge: Block
    lateinit var pressureCranker: Block
    //effect
    lateinit var coreFragment: Block
    lateinit var mechPad: Block
    lateinit var menderTurret: Block
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
                    SteamItems.stone + 40, Items.copper + 35, SteamItems.iron + 10
                )
            }
            consumeLiquid(SteamFluids.steam, 0.05f)
            shoot = ShootMulti(ShootSpread(5, 8f), ShootAlternate(4f))
            velocityRnd = 0.2f
            shootY = 6.75f
            size = 2
            drawTurret {
                regionPart("-barrel-l") {
                    moveY = -1.5f
                    progress = PartProgress.recoil
                    under = true
                }
                regionPart("-barrel-r") {
                    moveY = -1.5f
                    progress = PartProgress.recoil.delay(0.5f)
                    under = true
                }
            }
            addAmmo(SteamItems.stone, BasicBulletType(3.5f, 3f).apply {
                width = 7f
                height = 9f
                lifetime = 60f
                ammoMultiplier = 3f
                reloadMultiplier = 2.0f
            })
            addAmmo(
                Items.copper,
                BasicBulletType(4.5f, 6f).apply {
                    width = 7f
                    height = 9f
                    lifetime = 60f
                    trailColor = backColor
                    trailLength = 6
                },
            )
            addAmmo(
                Items.graphite,
                BasicBulletType(5.5f, 12f).apply {
                    width = 9f
                    height = 12f
                    reloadMultiplier = 0.6f
                    ammoMultiplier = 3f
                    lifetime = 60f
                    trailColor = backColor
                    trailLength = 7
                    rangeChange = 18f
                },
            )
            addAmmo(Items.coal, BasicBulletType(4.5f, 8f).apply {
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
            })
            limitRange()
        }
    }

    fun frostbite() {
        frostbite = PowerTurret("frostbite").apply {
            requirements(
                Category.turret,
                arrayOf(
                    SteamItems.iron + 20, SteamItems.stone + 25, Items.graphite + 15, Items.lead + 35
                )
            )

            buildType = Prov { object : PowerTurret.PowerTurretBuild() {
                override fun baseReloadSpeed(): Float {
                    return efficiency * warmup()
                }
            }}

            size = 2
            health = 840
            consumePower(3.5f)
            linearWarmup = true
            shootWarmupSpeed = 0.005f
            shootCone = 15f
            inaccuracy = 15f
            recoil = 0f
            drawer = DrawTurret().apply {
                parts.addAll(
                    RegionPart("-barrel").apply {
                        progress = PartProgress.recoil
                        y = 15 / 4f
                        moveY = -1f
                        heatColor = Pal.lancerLaser
                    },
                    RegionPart("-part").apply {
                        mirror = true
                        under = true
                        progress = PartProgress.warmup
                        heatProgress = PartProgress.warmup
                        heatColor = Pal.lancerLaser
                        y = -21 / 4f
                        x = 21 / 4f
                        moveX = 0.5f
                        moveY = -0.5f
                    }
                )
            }
            shootType = BasicBulletType(5.5f, 22f, "circle-bullet").apply {
                lifetime = 60f
                hitColor = Pal.lancerLaser.also { heatColor = it.cpy().a(0.4f); backColor = it; trailColor = it } //heh
                width = 5f.also { height = it; trailWidth = it / 2f }
                shrinkY = 0f
                hitEffect = Fx.hitLaserColor
                despawnEffect = Fx.hitLancer
                smokeEffect = Fx.colorSpark.also { shootEffect = it }
                homingPower = 0.075f
                trailLength = 7
                despawnHit = true
                status = StatusEffects.freezing
                pierceArmor = true
            }
            shootY = 2f
            reload = 6.5f
            range = 140f
            limitRange(3f)
        }
    }

    fun sporePlanter() {
        sporePlanter = AttributeCrafter("planter").apply {
            requirements(
                Category.production,
                arrayOf(
                    OreGenerator.rawOres[Items.copper]!! + 40,
                    OreGenerator.rawOres[Items.lead]!! + 25
                )
            )
            size = 2
            health = 200
            floating = true

            envRequired = envRequired or Env.spores
            attribute = SteamAttribute.sporeGrow

            drawMulti {
                +DrawCultivator()
                +DrawDefault()
            }

            baseEfficiency = 0f
            maxBoost = 2f
            craftTime = 240f
            outputItem = Items.sporePod + 1
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
            drawMulti {
                +DrawDefault()
                +DrawRegion("-rotator").apply {
                    rotateSpeed = 3f; spinSprite = true
                }
                +DrawRegion("-top")
            }
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
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawLiquidRegion(Liquids.water)
                +DrawSteamInside()
                +DrawLiquidTile(SteamFluids.steam)
                +DrawDefault()
                +DrawHeatInput().apply { heatColor = R.C.burnerFlame }
                +DrawOverheat()
            }
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
            health = 800
            liquidCapacity = 200f
            size = 3
            hasLiquids = true
            squareSprite = false
            maxEfficiency = 3f
            tempCap = 750f.celsius
            consumeLiquid(Liquids.water, 0.2f)
            outputFluid = LiquidStack(SteamFluids.steam, 0.2f)
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawLiquidTile(Liquids.water, 1f)
                +DrawSteamInside(1.4f)
                +DrawLiquidTile(SteamFluids.steam, 1f)
                +DrawDefault()
                +DrawHeatInput().apply { heatColor = R.C.burnerFlame }
                +DrawOverheat()
            }
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
            drawer = DrawMulti {
                +DrawDefault()
                +DrawGlowRegion().apply { color = R.C.burnerFlame }
                +DrawWarmupRegion().apply { color = R.C.burnerFlame; sinMag = 0.2f }
            }
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
            recipes.orePowderRecipe(4, 100f, 0.03f)
            addRecipe(45f, inItem = arrayOf(Items.sand + 1), outItem = arrayOf(SteamItems.glass + 1))
            addRecipe(80f * 0.625f, inItem = arrayOf(Items.scrap + 1), outLiquid = arrayOf(Liquids.slag + 0.16f))
            addRecipe(
                260f,
                inItem = arrayOf(SteamItems.iron + 2, Items.coal + 3),
                outItem = arrayOf(SteamItems.steel + 1),
                outLiquid = arrayOf(Liquids.slag + 0.04f)
            )
            drawer = DrawMulti {
                +DrawDefault()
                +DrawLiquidWarmup(Liquids.slag)
                +DrawRegion("-top1")
                +DrawGlowRegion().apply { color = R.C.burnerFlame }
                +DrawWarmupRegion().apply { color = R.C.burnerFlame; sinMag = 0.2f }
            }
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
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawLiquidTile(Liquids.slag)
                +DrawDefault()
            }
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
            consumeItem(OreGenerator.rawOres[Items.thorium])
            consumeLiquid(SteamFluids.acid, 0.1f)
            consumePower(1.2f)
            results = arrayOf(SteamItems.depletedThorium + 10, Items.thorium + 1)
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawBlurSpin("-rotor", 5f).apply {
                    blurThresh = 0.99f
                }
                +DrawDefault()
            }
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
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawDefault()
                +DrawHeatOutput().apply { heatColor = R.C.burnerFlame }
                +DrawWarmupRegion()
            }
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
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawLiquidRegion()
                +DrawDefault()
                +DrawHeatOutput().apply { heatColor = R.C.burnerFlame }
                +DrawWarmupRegion()
            }
        }
    }

    fun heatRegulator() {
        heatRegulator = HeatRegulator("heat-regulator").apply {
            size = 2
            health = 530
            category = Category.crafting
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.iron + 25, SteamItems.steel + 10, Items.metaglass + 15, Items.graphite + 25
                )
            }
            drawer = DrawMulti {
                +DrawRegion("-bottom")
                +DrawBlurSpin("-blade", 12f)
                +DrawDefault()
            }
            consumePower(2.1f)
            coolDownSpeed = 0.2f / 60f
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
            drawMulti {
                +DrawDefault()
                +DrawReservoir(null)
                +DrawRegion("-top")
            }
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
            powerProduction = 3.5f
            warmupSpeed = 0.02f
            drawMulti {
                +DrawRegion("-bottom")
                +DrawParticles().apply {
                    color = R.C.steam
                    alpha = 0.9f
                    particleSize = 3f
                    particles = 12
                    rotateScl = 0.7f
                    particleRad = 6f
                    particleLife = 80f
                    reverse = true
                    particleSizeInterp = Interp.exp5Out
                }
                +DrawBlurSpin("-rotor", 12f).apply { blurThresh = 0.95f }
                +DrawDefault()
                +DrawRegion("-top1")
            }
        }
    }

    fun pneumaticEngine() {
        pneumaticEngine = PressureGenerator("pneumatic-engine").apply {
            category = Category.power
            buildVisibility = BuildVisibility.shown
            UndebugOnly {
                requirements = arrayOf(
                    SteamItems.steel + 15, Items.titanium + 35, Items.silicon + 25
                )
            }
            size = 2
            squareSprite = false
            powerProduction = 3f
            drawMulti{
                +DrawRegion("-bottom")
                +DrawBuilding().apply {
                    for ((i, d) in Geometry.d8edge.withIndex()) {
                        parts.add(RegionPart("-piston$i").apply {
                            x = 4.25f * d.x
                            y = 4.25f * d.y
                            moveX = d.x.toFloat()
                            moveY = d.y.toFloat()
                            progress = PartProgress.constant(0f).absin(15f, 1f).mul(PartProgress.warmup)
                            outline = false
                        })
                    }
                }
                +DrawDefault()
            }
        }
    }

    fun stoneExcavator() {
        stoneExcavator = AttributeCrafter("stone-excavator").apply {
            requirements(
                Category.production,
                arrayOf(
                    OreGenerator.rawOres[Items.copper]!! + 35,
                    OreGenerator.rawOres[Items.lead]!! + 25
                )
            )
            attribute = SteamAttribute.stone
            maxBoost = 4f
            size = 2
            health = 500
            squareSprite = false
            updateEffect = Fx.coalSmeltsmoke
            updateEffectChance = 0.09f
            consume(ConsumeItemFlammable(1f))
            outputItem = SteamItems.stone + 3
            craftTime = 200f
            drawMulti {
                +DrawDefault()
                +DrawGlowRegion().apply { color = R.C.burnerFlame }
                +DrawWarmupRegion().apply { color = R.C.burnerFlame; sinMag = 0.2f }
            }
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

    fun pressureCranker() {
        pressureCranker = PressureCranker("crank-pressurizer").apply {
            requirements(
                Category.crafting,
                arrayOf(SteamItems.stone + 20, Items.lead + 15)
            )
            drawer = DrawMulti {
                +DrawDefault()
                +DrawRegion("-cranker").apply { rotateSpeed = 5f; spinSprite = true }
                +DrawRegion("-top")
            }
            health = 120
            generateTime = 240f
            loopSound = Sounds.grinding
        }
    }

    fun pressurizer() {
        pressurizer = PressureProducer("pressurizer").apply {
            requirements(
                Category.crafting,
                arrayOf(SteamItems.stone + 35, SteamItems.iron + 25, Items.copper + 30, Items.graphite + 15)
            )
            consumeLiquid(SteamFluids.steam, 0.1f)
            pressureOutput = 5f
            size = 2
            craftTime = 45f
            squareSprite = false
            drawMulti{
                +DrawRegion("-bottom")
                +DrawLiquidTile(SteamFluids.steam, 1f)
                +DrawSteamInside()
                +DrawBuilding().apply {
                    for (i in Mathf.signs) {
                        parts.add(RegionPart("-piston").apply {
                            x = 2f * i
                            y = 4f * i
                            moveY = -8f * i
                            progress = PartProgress.constant(0f).absin(craftTime / 3f, 1f).mul(PartProgress.warmup)
                            outline = false
                        })
                    }
                }
                +DrawDefault()
            }
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

    fun menderTurret() {
        menderTurret = ContinuousTurret("healer").apply {
            category = Category.effect
            requirements = arrayOf(
                SteamItems.stone + 40,
                Items.silicon + 20,
                SteamItems.iron + 25,
            )
            health = 400
            range = 110f
            heatColor = Pal.heal.cpy().a(0.4f)
            shootWarmupSpeed = 0.07f
            shootCone = 360f
            rotateSpeed = 2f
            targetAir = false
            targetGround = false
            targetHealing = true
            recoil = 0f
            consumePower(1.2f)
            shootY = 1.5f
            shootType = ConeBulletType {
                damage = 0.1f
                collidesTeam = true
                healAmount = 20f
                layer = Layer.buildBeam
                hitEffect = Fx.none
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
