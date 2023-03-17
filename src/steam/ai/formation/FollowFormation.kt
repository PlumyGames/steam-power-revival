package steam.ai.formation

import mindustry.gen.Posc
import steam.ai.UnitGroup

class FollowFormation : Formation() {
    var distance = 32f

    override fun move(group: UnitGroup, to: Posc, rotation: Float) {
        group.units.forEach {
            vec.set(to).sub(it).limit(to.dst(it) - distance)
            it.movePref(vec)
        }
    }
}