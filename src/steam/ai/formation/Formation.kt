package steam.ai.formation

import arc.math.geom.Vec2
import mindustry.gen.Posc
import steam.ai.UnitGroup

abstract class Formation {
    var vec = Vec2()

    abstract fun move(group: UnitGroup, to: Posc, rotation: Float)
}