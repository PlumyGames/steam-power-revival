package steam.ai.formation

import arc.math.geom.Vec2
import mindustry.gen.Posc
import mindustry.gen.Unit

class PositionFormation : Formation() {
    var positions = arrayOf<Vec2>()

    override fun move(unit: Unit, index: Int, to: Posc, rotation: Float) {
        vec.set(positions[index].x, positions[index].y).rotate(rotation).add(to).sub(unit).limit(unit.speed())
        unit.moveAt(vec)
        unit.lookAt(rotation - 90f)
    }
}