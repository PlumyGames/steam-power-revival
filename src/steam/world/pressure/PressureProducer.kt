package steam.world.pressure

import arc.math.Mathf
import arc.struct.IntSeq
import mindustry.world.blocks.production.GenericCrafter

open class PressureProducer(name: String) : GenericCrafter(name) {
    var pressureCapacity: Pressure = 0.5f
    var pressureOutput: Pressure = 10f
    override fun setBars() {
        super.setBars()
        addPressureBar<PressureProducerBuild>()
        addPressureProducedBar<PressureProducerBuild>(pressureOutput)
    }

    open inner class PressureProducerBuild : GenericCrafterBuild(), IPressureProducer {
        override var pressureProduced: Pressure = 0f
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureProducer.pressureCapacity
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
        open fun updatePressureProduced(){
            pressureProduced = if (efficiency > 0f) {
                Mathf.approachDelta(pressureProduced, pressureOutput * efficiency, warmupSpeed)
            } else {
                Mathf.approachDelta(pressureProduced, 0f, warmupSpeed)
            }
        }
        open fun updatePressure() {
            val targetPressure = graph.currentPressure
            currentPressure = (if (targetPressure > 0f)
                Mathf.approachDelta(currentPressure, targetPressure, warmupSpeed)
            else
                Mathf.approachDelta(currentPressure, 0f, warmupSpeed))
        }

        override fun updateTile() {
            updatePressureProduced()
            updatePressure()
        }
    }
}