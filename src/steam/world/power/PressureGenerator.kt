package steam.world.power

import arc.func.Prov
import arc.math.Mathf
import arc.struct.IntSeq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.blocks.power.PowerGenerator
import steam.world.pressure.*

class PressureGenerator(name: String) : PowerGenerator(name) {
    var pressureRequired = 2.5f
    var pressureCapacity = 0.5f
    var maxEfficiency = 2f
    var warmupSpeed = 0.15f

    init {
        update = true
        solid = true
        buildType = Prov { PressureGeneratorBuild() }
    }

    override fun setBars() {
        super.setBars()
        addPressureBar<PressureGeneratorBuild>()
        addPressureRequiredBar<PressureGeneratorBuild>(pressureRequired)
    }

    inner class PressureGeneratorBuild : GeneratorBuild(), IPressureConsumer {
        override var flash: Float = 0f
        override var pressureRequired: Pressure = this@PressureGenerator.pressureRequired
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureGenerator.pressureCapacity
        override val pressureWarmupSpeed = warmupSpeed
        var totalProgress = 0f

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

            updatePressure()
            productionEfficiency = Mathf.clamp(graph.currentPressure, 0f, maxEfficiency) * efficiency
            totalProgress += efficiency * productionEfficiency
        }

        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun updatePressure() {
            super.updatePressure()
            pressureRequired = if (efficiency > 0f) pressureRequired * efficiency else 0f
        }

        override fun write(write: Writes) {
            super.write(write)
            write.writePressureNode()
        }

        override fun totalProgress() = totalProgress

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            read.readPressureNode()
        }

        override fun warmup() = Mathf.clamp(productionEfficiency)
    }
}