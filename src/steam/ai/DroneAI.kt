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
    var tar = PosTeam.create()

    override fun updateUnit() {
        if (!owner!!.isValid) unit.destroy()
        super.updateUnit()
    }

    override fun updateMovement() {
        val owner = owner ?: return
        tar.set(owner.aimX, owner.aimY)

        if (owner.isShooting && unit.type.canAttack) {
            if (unit.type.circleTarget) circleAttack(unit.type.range)
            else {
                moveTo(tar, unit.type.range * 0.8f)
                unit.lookAt(tar)
            }
        } else if (unit.canBuild() && owner.activelyBuilding()) {
            unit.plans.clear()
            unit.plans.addFirst(owner.buildPlan())
            moveTo(unit.buildPlan().tile(), unit.type.buildRange - 20f)
        } else {
            formation?.move(unit, group.units.indexOf(unit), owner, owner.rotation + 90f)
        }
    }

    override fun shouldShoot(): Boolean {
        return owner != null && owner!!.isShooting
    }

    override fun target(x: Float, y: Float, range: Float, air: Boolean, ground: Boolean): Teamc {
        return tar
    }

    override fun invalid(target: Teamc): Boolean {
        return false
    }

    override fun retarget(): Boolean {
        return true
    }
}

class UnitGroup {
    val units = ArrayList<Unit>()
}