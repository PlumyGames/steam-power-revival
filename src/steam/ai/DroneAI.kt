package steam.ai

import mindustry.entities.units.AIController
import mindustry.gen.PosTeam
import mindustry.gen.Unit
import steam.ai.formation.Formation

class DroneAI : AIController() {
    var owner: Unit? = null
    var group = UnitGroup()
    var formation: Formation? = null

    override fun updateUnit() {
        if (!owner!!.isValid) unit.destroy()
        super.updateUnit()
    }

    override fun updateMovement() {
        val owner = owner ?: return
        val tar = PosTeam.create()
        tar.set(owner.aimX, owner.aimY)
        target = tar

        if (owner.isShooting && unit.type.canAttack) {
            if (unit.type.circleTarget) circleAttack(unit.type.range)
            else {
                moveTo(target, unit.type.range * 0.8f)
                unit.lookAt(target)
            }
        } else formation?.move(group, owner, owner.rotation)
    }
}

class UnitGroup {
    val units = ArrayList<Unit>()
}