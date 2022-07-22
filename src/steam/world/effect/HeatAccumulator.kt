package steam.world.effect

import arc.Core.bundle
import arc.func.Prov
import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatConsumer

class HeatAccumulator(name: String) : Block(name) {
    var measuringRange = 3.5E4f

    init {
        update = true
        solid = true
        configurable = true
        buildType = Prov { AccumulatorBuild() }
    }

    override fun setBars() {
        super.setBars()
        addBar<AccumulatorBuild>("heat") {
            Bar({ "${bundle["bar.heat"]} ${it.total}" }, { Pal.lightOrange }) { it.total / measuringRange }
        }
    }

    inner class AccumulatorBuild : Building(), HeatConsumer {
        var total = 0f
        var sideHeat = FloatArray(4)
        override fun updateTile() {
            total += calculateHeat(sideHeat) * delta()
        }

        override fun buildConfiguration(table: Table) {
            table.button(Icon.trash) {
                total = 0f
            }
        }

        override fun sideHeat() = sideHeat
        override fun heatRequirement() = Float.MAX_VALUE
    }
}