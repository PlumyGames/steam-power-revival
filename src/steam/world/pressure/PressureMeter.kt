package steam.world.pressure

import arc.Core.bundle
import arc.util.Strings
import mindustry.graphics.Pal
import mindustry.ui.Bar

class PressureMeter(name: String) : PressureBlock(name) {
    override fun setBars() {
        super.setBars()
        addBar("producing") { entity: PressureBuild ->
            Bar(
                { bundle.format("bar.pressure-total-produce",
                    Strings.autoFixed(entity.graph.producing, 1)) },
                { Pal.accent },
                { entity.graph.currentPressure }
            )
        }
        addBar("consuming") { entity: PressureBuild ->
            Bar(
                { bundle.format("bar.pressure-total-consume",
                    Strings.autoFixed(entity.graph.consuming, 1)) },
                { Pal.redderDust },
                { 1f - entity.graph.currentPressure }
            )
        }
        addBar("satisfaction") { entity: PressureBuild ->
            Bar(
                { bundle.format("bar.pressure-satisfaction",
                    Strings.autoFixed(entity.graph.currentPressure.coerceAtMost(1f) * 100f, 1)) },
                { Pal.redderDust.cpy().lerp(Pal.heal, entity.graph.currentPressure.coerceAtMost(1f)) },
                { 1f }
            )
        }
    }
}