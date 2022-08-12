package steam.world.pressure

import arc.func.Prov
import arc.math.Mathf
import arc.struct.IntSeq
import arc.util.Log
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
        override val pressureRequired: Pressure = this@PressureCrafter.pressureRequired
        override var graph: PressureGraph = PressureGraph()
        override var graphInitialized = false
        override var currentPressure: Pressure = 0f
        override val links = IntSeq()
        override val pressureCapacity: Pressure = this@PressureCrafter.pressureCapacity
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
            currentPressure = graph.currentPressure
            Log.info(efficiency())
            super.updateTile()
        }

        override fun updateEfficiencyMultiplier() {
            val eff = (currentPressure / pressureRequired).coerceIn(0f, maxEfficiency)
            efficiency *= eff
            potentialEfficiency *= eff
        }
    }
}