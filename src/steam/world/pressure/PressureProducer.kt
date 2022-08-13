
package steam.world.pressure

import arc.math.Mathf
import arc.struct.IntSeq
import mindustry.world.blocks.production.GenericCrafter

open class PressureProducer(name: String) : GenericCrafter(name) {
    var pressureCapacity: Pressure = 0.5f
    var pressureOutput: Pressure = 10f
    override fun setBars() {
        super.setBars()
        addPressureProducedBar<PressureProducerBuild>(pressureOutput)
    }

    open inner class PressureProducerBuild : GenericCrafterBuild(), IPressureProducer {
        override var flash: Float = 0f
        override var pressureProduced: Pressure = 0f
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureProducer.pressureCapacity
        override val pressureWarmupSpeed = warmupSpeed
        var totalProduceTime = 0f
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

        open fun updatePressureProduced() {
            pressureProduced = if (efficiency > 0f) {
                Mathf.approachDelta(pressureProduced, pressureOutput * efficiency, warmupSpeed)
            } else {
                Mathf.approachDelta(pressureProduced, 0f, warmupSpeed)
            }
        }

        override fun drawSelect() {
            super.drawSelect()
            drawWholeGraphForDebug()
        }

        override fun updateTile() {
            updatePressureProduced()
            updatePressure()
            totalProduceTime += getProgressIncrease(craftTime)
            warmup = if (efficiency > 0) Mathf.lerpDelta(warmup, 1f, warmupSpeed)
            else Mathf.lerpDelta(warmup, 0f, warmupSpeed)
        }

        override fun progress() = totalProduceTime % craftTime
        override fun totalProgress() = totalProduceTime
        override fun warmup() = warmup
    }
}