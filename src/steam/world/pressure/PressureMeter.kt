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
                { Pal.redDust },
                { entity.graph.currentPressure }
            )
        }
    }
}