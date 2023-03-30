package steam.content

import arc.func.Prov
import arc.math.geom.Rect
import arc.math.geom.Vec2
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.abilities.RepairFieldAbility
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.part.HoverPart
import mindustry.entities.part.RegionPart
import mindustry.gen.*
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.type.unit.TankUnitType
import steam.ai.DroneAI
import steam.ai.formation.PositionFormation
import steam.entities.abilities.UnitConstructionAbility
import steam.entities.bullets.ConeBulletType
import steam.entities.bullets.ForceFieldBulletType
import steam.entities.bullets.SteamBaseBulletType
import steam.entities.units.SentryEntity
import steam.type.SentryUnitType

object SteamUnitTypes {
    //drone
    lateinit var alphaCombatDrone: UnitType
    lateinit var alphaSupportDrone: UnitType

    //mech
    lateinit var epsilon: UnitType
    lateinit var tau: UnitType

    //tanks
    lateinit var defender: UnitType

    //sentries
    lateinit var sprayer: SentryUnitType
    lateinit var buster: SentryUnitType
    lateinit var striker: SentryUnitType

    fun alphaCombatDrone() {
        alphaCombatDrone = UnitType("alpha-combat-drone").apply {
            constructor = Prov { UnitEntity.create() }
            aiController = Prov { DroneAI() }
            health = 75f
            speed = 3.2f
            drag = 0.014f
            flying = true
            hitSize = 7f
            engineOffset = 3f
            range = 180f
            circleTarget = true
            trailLength = 7
            rotateSpeed = 12.5f
            trailScl = 0.7f
            accel = 0.3f
            useUnitCap = false
            playerControllable = false
            logicControllable = false
            weapons.addAll(
                Weapon().apply {
                    reload = 13f
                    x = 0f
                    mirror = false
                    ejectEffect = Fx.casing1
                    shootCone = 10f
                    bullet = BasicBulletType(2.5f, 9.0f).apply {
                        buildingDamageMultiplier = 0.1f
                        width = 7f
                        height = 9f
                        lifetime = 60.0f
                    }
                },
                Weapon().apply {
                    reload = 60f
                    x = 0f
                    baseRotation = 180f
                    shootCone = 360f
                    inaccuracy = 10f
                    bullet = SteamBaseBulletType().apply {
                        vectorHoming = true
                        buildingDamageMultiplier = 0.1f
                        recoil = 2.5f
                        trailColor = Pal.bulletYellowBack
                        backColor = Pal.bulletYellowBack
                        frontColor = Pal.bulletYellow
                        shrinkY = 0f
                        width = 8f
                        keepVelocity = false
                        height = 8f
                        hitSound = Sounds.explosion
                        trailLength = 5
                        homingRange = 135f
                        lifetime = 120f
                        speed = 4.85f
                        rangeOverride = 110f

                        hitEffect = Fx.explosion.also { despawnEffect = it; smokeEffect = it }
                        damage = 3f
                        splashDamage = 8f
                        splashDamageRadius = 20f

                        sprite = "missile"
                    }
                }
            )
        }
    }

    fun alphaSupportDrone() {
        alphaSupportDrone = UnitType("alpha-builder-drone").apply {
            constructor = Prov { UnitEntity.create() }
            aiController = Prov { DroneAI() }
            health = 75f
            speed = 3.2f
            drag = 0.014f
            flying = true
            hitSize = 7f
            engineOffset = 3f
            range = 110f
            trailLength = 7
            rotateSpeed = 12.5f
            trailScl = 0.7f
            accel = 0.3f
            buildSpeed = 1.2f
            useUnitCap = false
            playerControllable = false
            logicControllable = false
        }
    }

    fun epsilon() {
        epsilon = UnitType("epsilon").apply {
            constructor = Prov { MechUnit.create() }
            speed = 1.6f
            hitSize = 11f
            health = 450f
            rotateSpeed = 7f
            canBoost = true
            boostMultiplier = 1.3f
            mineWalls = true
            mineSpeed = 10f
            mineTier = 2
            itemCapacity = 35
            legMoveSpace = 1.6f
            buildSpeed = 1.2f

            abilities.add(
                UnitConstructionAbility().apply {
                    constructTime = 90f
                    spawnUnits = arrayOf(alphaCombatDrone, alphaSupportDrone)
                    formation = PositionFormation().apply {
                        positions = arrayOf(Vec2(16f, 0f), Vec2(-16f, 0f))
                    }
                }
            )

            weapons.add(Weapon().apply {
                x = 5f
                y = 3f
                reload = 30f

                shoot.apply {
                    shots = 3
                    shotDelay = 2f
                }

                bullet = BasicBulletType(4.5f, 7f).apply {
                    lifetime = 30f
                    width = 8f
                    height = 12f
                }
            })
        }
    }

    fun tau() {
        tau = UnitType("tau").apply {
            constructor = Prov { ElevationMoveUnit.create() }

            hovering = true
            shadowElevation = 0.2f
            drag = 0.05f
            speed = 4.2f
            rotateSpeed = 3f
            accel = 0.05f
            health = 550f
            armor = 9f
            hitSize = 16.5f
            engineOffset = 6.25f
            engineColor = Pal.heal
            engineSize = 4f
            itemCapacity = 0
            useEngineElevation = false
            buildSpeed = 2.5f
            mineSpeed = 8f
            mineTier = 4
            itemCapacity = 80

            abilities.add(
                MoveEffectAbility(0f, -6.75f, Pal.heal, Fx.missileTrailShort, 3f),
                RepairFieldAbility(65f, 155f, 85f)
            )

            parts.add(
                HoverPart().apply {
                    x = 5.25f
                    y = -4.25f
                    mirror = true
                    radius = 5.5f
                    phase = 35f
                    stroke = 2.2f
                    layerOffset = -0.001f
                    color = Pal.heal
                }
            )

            weapons.addAll(
                Weapon().apply {
                    x = 0f
                    y = 3.25f
                    shootCone = 360f
                    shootY = 0f
                    alwaysContinuous = true
                    range = 65f
                    mirror = false
                    shootSound = Sounds.tractorbeam
                    bullet = ConeBulletType {
                        damage = 5f
                        collidesTeam = true
                        healAmount = 35f
                        layer = Layer.buildBeam
                        hitEffect = Fx.none
                        length = 70f
                        status = StatusEffects.electrified
                    }
                },
                Weapon().apply {
                    x = 3.75f
                    y = 5f
                    shootY = 0f
                    range = 110f
                    shootSound = Sounds.missile
                    reload = 15f
                    bullet = SteamBaseBulletType().apply {
                        vectorHoming = true
                        backColor = Pal.heal
                        trailColor = Pal.heal
                        shrinkY = 0f
                        width = 8f
                        keepVelocity = false
                        height = 8f
                        hitSound = Sounds.explosion
                        trailLength = 5
                        homingRange = 110f
                        lifetime = 160f
                        speed = 4.85f
                        healAmount = 12f
                        collidesTeam = true
                        hitSound = Sounds.none
                        shootEffect = Fx.shootHeal

                        hitEffect = Fx.hitLaser.also { despawnEffect = it; smokeEffect = it }
                        damage = 10f
                    }
                }
            )
        }
    }

    fun defender() {
        defender = TankUnitType("defender").apply {
            constructor = Prov { TankUnit.create() }
            hitSize = 18f
            rotateSpeed = 2.4f
            treadPullOffset = 5
            health = 900f
            armor = 4f
            itemCapacity = 0
            treadRects = arrayOf(Rect(-30f, -35f, 10f, 70f))
            outlineColor = Pal.darkerMetal

            weapons.add(
                Weapon("steam-defender-projector").apply {
                    x = 0f
                    y = -1.25f
                    shootCone = 360f
                    shootY = 0f
                    alwaysContinuous = true
                    range = 65f
                    mirror = false
                    shootSound = Sounds.tractorbeam
                    heatColor = Pal.accent
                    recoil = 0f
                    rotate = true
                    alwaysShooting = true
                    rotateSpeed = 12f

                    bullet = ForceFieldBulletType {
                        chargeSpeed = 1.4f
                        maxCapacity = 80f
                        damage = 0f
                        length = 70f
                        rad = 80f
                    }
                }
            )
        }
    }

    fun sprayer() {
        sprayer = SentryUnitType("sprayer").apply {
            constructor = Prov { SentryEntity() }
            speed = 0f
            rotateSpeed = 6.5f
            armor = 2f
            health = 120f
            faceTarget = true
            hitSize = 8f
            useUnitCap = false
            shadowElevation = 0.1f

            weapons.add(Weapon().apply {
                flipSprite = true
                top = false
                reload = 3.5f
                range = 126f
                rotate = false
                shootY = 1f
                x = 1.5f
                shootY = 4.5f
                inaccuracy = 3f
                ejectEffect = Fx.casing1

                bullet = BasicBulletType(5.4f, 10f, "steam-sharp-shell").apply {
                    width = 5.5f
                    height = 10.5f
                    lifetime = 25f
                    keepVelocity = false
                    recoil = 0.22f
                    trailLength = 2
                    trailWidth = 1.25f
                }
            })
        }
    }

    fun buster() {
        buster = SentryUnitType("buster").apply {
            constructor = Prov { SentryEntity() }
            speed = 0f
            rotateSpeed = 4.5f
            armor = 4f
            health = 180f
            faceTarget = true
            hitSize = 8f
            useUnitCap = false
            drawBody = false
            drawCell = false
            parts.add(RegionPart("-overlay"))
            shadowElevation = 0.1f

            weapons.addAll(
                Weapon("steam-buster-barrel").apply {
                    top = false
                    reload = 38f
                    range = 100f
                    x = 0f
                    mirror = false
                    shoot.shots = 6
                    inaccuracy = 20f
                    ejectEffect = Fx.casing2

                    bullet = BasicBulletType(5f, 14f).apply {
                        width = 8f
                        height = 10f
                        lifetime = 20f
                        keepVelocity = false
                        knockback = 3.2f
                        velocityRnd = 0.15f
                        status = StatusEffects.slow
                        trailColor = Pal.bulletYellowBack
                        trailLength = 3
                        recoil = 0.1f
                    }
                }
            )
        }
    }

    fun striker() {
        striker = SentryUnitType("striker").apply {
            constructor = Prov { SentryEntity() }
            speed = 0f
            rotateSpeed = 5f
            armor = 5.5f
            health = 260f
            faceTarget = true
            hitSize = 10f
            useUnitCap = false
            drawBody = false
            parts.add(RegionPart("-overlay"))
            shadowElevation = 0.1f

            weapons.addAll(
                Weapon("steam-striker-barrel").apply {
                    top = false
                    reload = 60f
                    range = 140f
                    x = 0f
                    mirror = false
                    shoot.shots = 5
                    inaccuracy = 10f
                    ejectEffect = Fx.casing2

                    bullet = BasicBulletType(7f, 28f).apply {
                        width = 8f
                        height = 12f
                        lifetime = 20f
                        keepVelocity = false
                        knockback = 2f
                        velocityRnd = 0.15f
                        trailColor = Pal.bulletYellowBack
                        trailLength = 5
                        recoil = 0.2f
                    }
                }
            )
        }
    }
}