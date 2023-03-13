package steam.ai.formation

import arc.math.geom.Vec2
import mindustry.gen.Posc
import steam.ai.UnitGroup

class PositionFormation : Formation() {
    var positions = arrayOf<Vec2>()

    override fun move(group: UnitGroup, to: Posc, rotation: Float) {
        group.units.forEachIndexed { i, u ->
            vec.set(positions[i].x, positions[i].y).rotate(rotation).add(to).sub(u).limit(u.speed())
            u.moveAt(vec)
            u.lookAt(rotation - 90f)
        }
    }
}