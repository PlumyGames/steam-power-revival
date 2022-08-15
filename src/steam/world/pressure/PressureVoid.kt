package steam.world.pressure

import arc.func.Prov
import arc.scene.ui.Label
import arc.scene.ui.Slider
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Tex
import mindustry.graphics.Pal
import plumy.world.config

class PressureVoid(name: String) : PressureBlock(name) {
    var maxRequirement = 10f

    init {
        configurable = true
        saveConfig = true
        buildType = Prov { PressureVoidBuild() }
    }

    override fun init() {
        super.init()
        config<PressureVoidBuild, Float> {
            pressureRequired = it
        }
    }

    override fun setBars() {
        super.setBars()
        addPressureRequiredBar<PressureVoidBuild>(maxRequirement)
    }

    inner class PressureVoidBuild : PressureBuild(), IPressureConsumer {
        override var pressureRequired = 0f
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val pressureCapacity: Pressure = 0.5f
        override val links = IntSeq()
        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun config() = pressureRequired
        override fun buildConfiguration(table: Table) {
            table.bottom()
            table.apply {
                background = Tex.whitePane
                setColor(Pal.gray)
            }
            table.add(
                Stack(
                    Slider(0f, maxRequirement, maxRequirement / 20f, false).apply {
                        value = pressureRequired
                        moved { configure(it) }
                    },
                    Table().apply {
                        add(Label { "$pressureRequired" })
                        defaults().center()
                    }
                )
            ).width(250f).row()
            table.defaults().growX()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(pressureRequired)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            pressureRequired = read.f()
        }
    }
}