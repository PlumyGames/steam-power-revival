package steam.world.pressure

import arc.func.Prov
import arc.struct.IntSeq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.world.Block
import steam.world.pressure.IPressureNode.Companion.pressureFact

open class PressureBlock(name: String) : Block(name) {
    var pressureCapacity: Pressure = 0.5f
    val warmupSpeed = 0.02f

    init {
        solid = true
        update = true
        buildType = Prov { PressureBuild() }
    }

    override fun setBars() {
        super.setBars()
        addPressureBar<PressureBuild>()
    }

    open inner class PressureBuild : Building(), IPressureNode {
        override var flash: Float = 0f
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureBlock.pressureCapacity
        override val pressureWarmupSpeed = warmupSpeed
        override fun updateTile() {
            updatePressure()
        }

        override fun created() {
            super.created()
            graph.initNode(this)
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            updateProximateLink()
        }

        override fun onProximityRemoved() {
            super.onProximityRemoved()
            removeFromGraph()
        }

        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun warmup() = pressureFact
        override fun write(write: Writes) {
            super.write(write)
            write.writePressureNode()
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            read.readPressureNode()
        }
    }
}