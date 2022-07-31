package steam.content

import arc.func.Prov
import mindustry.content.Fx
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.MechUnit
import mindustry.type.UnitType
import mindustry.type.Weapon

object SteamUnitTypes {
    lateinit var epsilon: UnitType

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
                reload = 20f
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
                    homingRange = 270f
                    homingPower = 1f
                    buildingDamageMultiplier = 0.01f
                    rangeOverride = 270f

                    fragBullet = BasicBulletType(4.5f, 8f).apply {
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
}