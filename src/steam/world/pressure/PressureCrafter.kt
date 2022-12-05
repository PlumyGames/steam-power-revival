package steam.world.pressure

import arc.func.Prov
import arc.math.Mathf
import arc.struct.IntSeq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.blocks.production.GenericCrafter

class PressureCrafter(name: String) : GenericCrafter(name), IPressureConsumerBlock {
    override var pressureCapacity: Pressure = 0.5f
    override var pressureConsumption: Pressure = 4f
    var maxEfficiency = 4f

    init {
        buildType = Prov { PressureCrafterBuild() }
    }

    override fun setBars() {
        super.setBars()
        addPressureBar<PressureCrafterBuild>()
        addPressureRequiredBar<PressureCrafterBuild>(pressureConsumption)
    }

    inner class PressureCrafterBuild : GenericCrafterBuild(), IPressureConsumer {
        override var flash: Float = 0f
        override var pressureRequired: Pressure = this@PressureCrafter.pressureConsumption
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureCrafter.pressureCapacity
        override val pressureWarmupSpeed = warmupSpeed
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

        override fun updateTile() {
            super.updateTile()
            pressureRequired = if (enabled)  pressureConsumption else 0f
        }

        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.writePressureNode()
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            read.readPressureNode()
        }

        override fun efficiencyScale(): Float {
            return Mathf.clamp(graph.currentPressure, 0f, maxEfficiency)
        }
    }
}