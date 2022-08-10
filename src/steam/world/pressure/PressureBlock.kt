package steam.world.pressure

import arc.math.Mathf
import arc.struct.IntSeq
import mindustry.gen.Building
import mindustry.world.Block
import steam.world.pressure.IPressureNode.Companion.pressureFact

open class PressureBlock(name: String) : Block(name) {
    var pressureCapacity: Pressure = 0.5f
    val warmupSpeed = 0.05f

    init {
        solid = true
        update = true
    }

    override fun setBars() {
        super.setBars()
        addPressureBar<PressureBuild>()
    }

    open inner class PressureBuild : Building(), IPressureNode {
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureBlock.pressureCapacity
        override fun updateTile() {
            val targetPressure = graph.currentPressure
            currentPressure = (if (targetPressure > 0f)
                Mathf.approachDelta(currentPressure, targetPressure, warmupSpeed)
            else
                Mathf.approachDelta(currentPressure, 0f, warmupSpeed))
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

        override fun warmup() = pressureFact
    }
}