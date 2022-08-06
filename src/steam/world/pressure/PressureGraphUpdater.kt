package steam.world.pressure

import mindustry.gen.Groups
import steam.utils.EntityMixin

class PressureGraphUpdater: EntityMixin() {
    var graph: PressureGraph? = null
    override fun update() {
        graph?.update()
    }

    override fun add() {
        if(!this.added){
            Groups.all.add(this)
            this.added = true
        }
    }

    override fun remove() {
        if(this.added){
            Groups.all.remove(this)
            this.added = false
        }
    }

    companion object{
        fun create() = PressureGraphUpdater()
    }
}