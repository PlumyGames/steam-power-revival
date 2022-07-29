package steam.world.distribution

import mindustry.world.blocks.distribution.Conveyor

//conveyor that use power
class ElectricConveyor(name: String) : Conveyor(name) {
    init {
        hasPower = true
        consumesPower = true
        conductivePower = true
    }
    inner class ElectricConveyorBuild : ConveyorBuild() {
        override fun shouldConsume(): Boolean {
            return enabled && items.any()
        }
        override fun efficiency(): Float {
            return if(items.any()) super.efficiency() else 0f
        }
    }
}