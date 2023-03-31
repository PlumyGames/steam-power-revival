package steam.entities.abilities

import arc.Events
import arc.math.Angles
import arc.util.Time
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.abilities.Ability
import mindustry.game.EventType.UnitCreateEvent
import mindustry.gen.Unit
import mindustry.type.UnitType
import steam.ai.DroneAI
import steam.ai.UnitGroup
import steam.ai.formation.Formation

class UnitConstructionAbility : Ability() {
    var constructTime = 30f
    var spawnUnits = emptyArray<UnitType>()
    var spawnEffect = Fx.spawn
    var spawnX = 0f
    var spawnY = 0f
    var spawnRot = 0f
    var formation: Formation? = null

    protected var reload = 0f
    protected var unitGroup = UnitGroup()

    override fun copy(): Ability = super.copy().apply {
        unitGroup = UnitGroup()
    }

    override fun update(unit: Unit) {
        val units = unitGroup.units
        units.retainAll { it.isValid }
        if (units.size < spawnUnits.size && reload >= constructTime) {
            val x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX)
            val y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX)

            //select missing unit
            val u = spawnUnits.first { units.count { u -> it == u.type } < spawnUnits.count { u -> u == it } }.create(unit.team)
            u.set(x, y)
            u.rotation = unit.rotation + spawnRot

            Events.fire(UnitCreateEvent(u, null, unit))
            if (!Vars.net.client()) {
                u.add()
            }
            unitGroup.units.add(u)

            assignUnit(u, unit)
            spawnEffect.at(u)

            reload %= constructTime
        } else reload += Time.delta
    }

    fun assignUnit(u: Unit, owner: Unit) {
        val ai = u.controller() as? DroneAI ?: return

        ai.owner = owner
        ai.group = unitGroup
        ai.formation = formation
    }
}