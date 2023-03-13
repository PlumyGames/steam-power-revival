package steam.ai

import mindustry.entities.units.AIController
import mindustry.gen.PosTeam
import mindustry.gen.Teamc
import mindustry.gen.Unit
import steam.ai.formation.Formation

class DroneAI : AIController() {
    var owner: Unit? = null
    var group = UnitGroup()
    var formation: Formation? = null
    var shoot = false
    var tar = PosTeam.create()

    override fun updateUnit() {
        if (!owner!!.isValid) unit.destroy()
        super.updateUnit()
    }

    override fun updateMovement() {
        val owner = owner ?: return
        tar.set(owner.aimX, owner.aimY)

        if (owner.isShooting && unit.type.canAttack) {
            target = tar
            shoot = true
            if (unit.type.circleTarget) circleAttack(unit.type.range)
            else {
                moveTo(target, unit.type.range * 0.8f)
                unit.lookAt(target)
            }
        } else {
            target = null
            shoot = false
            formation?.move(group, owner, owner.rotation + 90f)
        }
    }

    override fun shouldShoot(): Boolean {
        return shoot
    }

    override fun target(x: Float, y: Float, range: Float, air: Boolean, ground: Boolean): Teamc {
        return tar
    }
}

class UnitGroup {
    val units = ArrayList<Unit>()
}