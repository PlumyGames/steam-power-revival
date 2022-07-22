package steam.world.crafting

import arc.math.Mathf
import arc.util.Log
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatConsumer
import steam.world.module.ITemperatureBlock

class TemperatureBlock(name: String) : Block(name) {
    var heatRequirement = 10f
    var convertionRateMul = 25f
    var convertSpeed = 0.05f

    init {
        update = true
        solid = true
    }

    inner class TemperatureBuild : Building(), ITemperatureBlock, HeatConsumer {
        override var temp = 0f
        var sideHeat = FloatArray(4)
        var heat = 0f

        override fun sideHeat() = sideHeat
        override fun heatRequirement() = heatRequirement

        override fun updateTile() {
            heat = calculateHeat(sideHeat)
            temp = Mathf.lerpDelta(temp, heat * convertionRateMul, convertSpeed)
            Log.info(temp)
        }
    }
}