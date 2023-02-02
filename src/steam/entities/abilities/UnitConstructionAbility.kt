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

class UnitConstructionAbility : Ability() {
    var constructTime = 30f
    var spawnUnits = emptyArray<UnitType>()
    var spawnEffect = Fx.none
    var spawnX = 0f
    var spawnY = 0f
    var spawnRot = 0f

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

            val u = spawnUnits[units.size].create(unit.team)
            u.set(x, y)
            u.rotation = unit.rotation + spawnRot

            Events.fire(UnitCreateEvent(u, null, unit))
            if (!Vars.net.client()) {
                u.add()
            }
            unitGroup.units.add(u)

            assignAI(u)
            spawnEffect.at(u)

            reload %= constructTime
        } else reload += Time.delta
    }

    fun assignAI(u: Unit) {
        (u.controller() as? DroneAI)?.group = unitGroup
    }
}