package steam.ai.formation

import arc.math.geom.Vec2
import mindustry.gen.Posc
import mindustry.gen.Unit

abstract class Formation {
    var vec = Vec2()

    abstract fun move(unit: Unit, index: Int, to: Posc, rotation: Float)
}