package steam.ai.formation

import arc.math.geom.Vec2
import mindustry.gen.Posc
import steam.ai.UnitGroup

class PositionFormation : Formation() {
    var positions = arrayOf<Vec2>()

    override fun move(group: UnitGroup, to: Posc, rotation: Float) {
        //todo make it move to the front
        group.units.forEachIndexed { i, u ->
            vec.set(to).sub(u).add(positions[i].x, positions[i].y).limit(u.speed())
            u.moveAt(vec)
        }
    }
}