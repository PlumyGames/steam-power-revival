package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import mindustry.type.LiquidStack
import steam.world.module.Celsius100
import steam.world.module.IPressureContainer
import steam.world.module.celsius

class TemperatureCrafter(name: String) : TemperatureBlock(name) {
    //amount of temp lose per craft
    val craftTemp = 0.15f.celsius
    var warmupSpeed = 0.1f
    //in tick
    var craftTime = 1f
    val pressureCapacity: Float
        get() = liquidCapacity
    var minTemp = Celsius100
    var maxTemp = 400f.celsius

    lateinit var outputFluid: LiquidStack

    init {
        hasLiquids = true
        buildType = Prov { TempCrafterBuild() }
    }

    inner class TempCrafterBuild : TemperatureBuild(), IPressureContainer {
        override val pressureAmount: Float
            get() = liquids[outputFluid.liquid]
        override val pressureProportion: Float
            get() = pressureAmount / pressureCapacity
        var warmup = 0f

        override fun updateTile() {
            super.updateTile()
            if (efficiency > 0 && temp >= minRequired) {
                val amount = craftTime * edelta()
                if (amount > 0.001f) {
                    consume()
                    handleLiquid(this, outputFluid.liquid, amount * outputFluid.amount)
                    temp -= amount * craftTemp
                    warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed)
                } else warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed)
            }
        }
        override fun placed() {
            super.placed()
            temp = 25f
        }

        override fun shouldConsume(): Boolean {
            return pressureProportion <= 1f && enabled
        }
        override fun efficiency(): Float {
            return Mathf.clamp(temp, minTemp, maxTemp) / minRequired
        }
        override fun warmup() = warmup
    }

    override fun setBars() {
        super.setBars()
        addLiquidBar(outputFluid.liquid)
    }
}