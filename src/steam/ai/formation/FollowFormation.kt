package steam.ai.formation

import mindustry.gen.Posc
import mindustry.gen.Unit

class FollowFormation : Formation() {
    var distance = 32f
    var smooth = 100f

    override fun move(unit: Unit, index: Int, to: Posc, rotation: Float) {
        val l = ((unit.dst(to) - distance) / smooth).coerceIn(-1f, 1f)
        vec.set(to).sub(unit).setLength(l * unit.speed())
        if (l < 0f) vec.setZero()
        unit.moveAt(vec)
    }
}