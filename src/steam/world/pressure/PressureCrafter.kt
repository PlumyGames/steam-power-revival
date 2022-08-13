package steam.world.pressure

import arc.func.Prov
import arc.math.Mathf
import arc.struct.IntSeq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.blocks.production.GenericCrafter

class PressureCrafter(name: String) : GenericCrafter(name) {
    var pressureCapacity: Pressure = 0.5f
    var pressureRequired: Pressure = 4f
    var maxEfficiency = 4f

    init {
        buildType = Prov { PressureCrafterBuild() }
    }

    override fun setBars() {
        super.setBars()
        addPressureBar<PressureCrafterBuild>()
        addPressureRequiredBar<PressureCrafterBuild>(pressureRequired)
    }

    inner class PressureCrafterBuild : GenericCrafterBuild(), IPressureConsumer {
        override var flash: Float = 0f
        override var pressureRequired: Pressure = this@PressureCrafter.pressureRequired
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
            updatePressure()
            super.updateTile()
        }

        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun updatePressure() {
            super.updatePressure()
            pressureRequired = if (efficiency > 0f) (this@PressureCrafter.pressureRequired * efficiency) else 0f
        }

        override fun write(write: Writes) {
            super.write(write)
            write.writePressureNode()
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            read.readPressureNode()
        }

        override fun updateEfficiencyMultiplier() {
            val eff = Mathf.clamp(graph.currentPressure, 0f, maxEfficiency)
            efficiency *= eff
            potentialEfficiency *= eff
        }
    }
}