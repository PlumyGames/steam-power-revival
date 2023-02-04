package steam.ai.formation

import mindustry.gen.Posc
import steam.ai.UnitGroup

class CircleFormation : Formation() {
    var distance = 32f
    var speed = 2.5f
    var inverse = false

    override fun move(group: UnitGroup, to: Posc, rotation: Float) {
        //todo make it sync with other units
        group.units.forEachIndexed { i, u ->
            vec.set(to).sub(u)

            if (vec.len() < distance)
                vec.rotate((distance - vec.len()) / distance * if (inverse) -180f else 180f)

            vec.setLength(speed)

            u.moveAt(vec)
        }
    }
}