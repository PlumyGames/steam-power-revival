package steam.world.pressure

import arc.func.Prov
import arc.scene.ui.Label
import arc.scene.ui.Slider
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import mindustry.gen.Tex
import mindustry.graphics.Pal
import plumy.world.config

class PressureSource(name: String) : PressureBlock(name) {
    var maxProduce = 10f

    init {
        configurable = true
        saveConfig = true
        buildType = Prov { PressureSourceBuild() }
    }

    override fun init() {
        super.init()
        config<PressureSourceBuild, Float> {
            pressureProduced = it
        }
    }

    override fun setBars() {
        super.setBars()
        addPressureProducedBar<PressureSourceBuild>(maxProduce)
    }

    inner class PressureSourceBuild : PressureBuild(), IPressureProducer {
        override var pressureProduced = 0f
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val pressureCapacity: Pressure = 0.5f
        override val links = IntSeq()
        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun config() = pressureProduced
        override fun buildConfiguration(table: Table) {
            table.bottom()
            table.apply {
                background = Tex.whitePane
                setColor(Pal.gray)
            }
            table.add(
                Stack(
                    Slider(0f, maxProduce, maxProduce / 20f, false).apply {
                        value = pressureProduced
                        moved { configure(it) }
                    },
                    Table().apply {
                        add(Label { "$pressureProduced" })
                        defaults().center()
                    }
                )
            ).width(250f).row()
            table.defaults().growX()
        }
    }
}