package steam.content

import arc.func.Prov
import mindustry.content.Fx
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.abilities.RepairFieldAbility
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.part.HoverPart
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.ElevationMoveUnit
import mindustry.gen.MechUnit
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.type.Weapon
import steam.entities.bullets.ConeBulletType
import tvakot.entities.bullet.VectorHomingBulletType

object SteamUnitTypes {
    lateinit var epsilon: UnitType
    lateinit var tau: UnitType
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
            legForwardScl = 2.1f
            buildSpeed = 1.2f

            weapons.add(Weapon("steam-phase-gun").apply {
                reload = 30f
                shoot = ShootSpread(2, 5f)
                baseRotation = -35f
                shootCone = 360f
                x = 23 / 4f
                y = -6 / 4f
                ejectEffect = Fx.casing1

                bullet = BasicBulletType(4.5f, 0f).apply {
                    keepVelocity = false
                    width = 9f
                    height = 12f
                    lifetime = 30f
                    drag = 0.1f
                    fragBullets = 1
                    fragRandomSpread = 0f
                    fragVelocityMin = 1f
                    trailLength = 7
                    trailColor = backColor
                    homingDelay = 27f
                    homingRange = 60 * 5.5f
                    homingPower = 6f
                    buildingDamageMultiplier = 0.01f
                    rangeOverride = 60 * 5.5f

                    fragBullet = BasicBulletType(5.5f, 15f).apply {
                        keepVelocity = false
                        width = 9f
                        height = 12f
                        lifetime = 60f
                        trailLength = 7
                        trailColor = backColor
                        hitColor = backColor
                        hitEffect = Fx.hitSquaresColor
                        buildingDamageMultiplier = 0.01f
                    }
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
                    }
                },
                Weapon().apply {
                    x = 3.75f
                    y = 5f
                    shootY = 0f
                    range = 110f
                    shootSound = Sounds.missile
                    reload = 15f
                    bullet = VectorHomingBulletType().apply {
                        backColor = Pal.heal
                        trailColor = Pal.heal
                        shrinkY = 0f
                        width = 8f
                        keepVelocity = false
                        height = 8f
                        hitSound = Sounds.explosion
                        trailLength = 5
                        homingRange = 110f
                        homingPower = 0.055f
                        lifetime = 120f
                        speed = 1.85f
                        healAmount = 25f
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
}