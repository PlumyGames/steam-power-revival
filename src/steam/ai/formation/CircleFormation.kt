package steam.ai.formation

import mindustry.gen.Posc
import mindustry.gen.Unit

class CircleFormation : Formation() {
    var distance = 32f
    var speed = 2.5f
    var inverse = false

    override fun move(unit: Unit, index: Int, to: Posc, rotation: Float) {
        vec.set(to).sub(unit)

        if (vec.len() < distance)
            vec.rotate((distance - vec.len()) / distance * if (inverse) -180f else 180f)

        vec.setLength(speed)

        unit.moveAt(vec)
    }
}