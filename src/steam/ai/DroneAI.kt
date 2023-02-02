package steam.ai

import mindustry.entities.units.AIController
import mindustry.gen.Entityc
import mindustry.gen.Unit
import java.util.ArrayList

class DroneAI : AIController() {
    var owner: Entityc? = null
    var group = UnitGroup()

    override fun updateUnit() {
        if (owner == null) return unit.destroy()
        super.updateUnit()
    }

    override fun updateMovement() {

    }
}

class UnitGroup {
    val units = ArrayList<Unit>()
}