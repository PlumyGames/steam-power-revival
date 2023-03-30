package steam.ai.formation

import mindustry.gen.Posc
import mindustry.gen.Unit

class FollowFormation : Formation() {
    var distance = 32f

    override fun move(unit: Unit, index: Int, to: Posc, rotation: Float) {
        vec.set(to).sub(unit).limit(to.dst(unit) - distance)
        unit.movePref(vec)
    }
}