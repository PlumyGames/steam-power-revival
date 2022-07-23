package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import arc.util.Log
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatConsumer
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import steam.world.module.ITemperatureBlock.Companion.warmupImpl
import steam.world.module.ITemperatureBlock
import steam.world.module.celsius

open class TemperatureBlock(name: String) : Block(name) {
    var heatRequirement = 10f
    var tempCap = 240f.celsius
    var convertSpeed = 5f //convert speed x5
    var coolDownSpeed = 0.05f / 60f //lose 5% each second
    var drawer: DrawBlock = DrawDefault()

    init {
        update = true
        solid = true
        buildType = Prov { TemperatureBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    open inner class TemperatureBuild : Building(), ITemperatureBlock, HeatConsumer {
        override var temp = 0f.celsius
        var sideHeat = FloatArray(4)
        var heat = 0f
        override fun sideHeat() = sideHeat
        override fun heatRequirement() = heatRequirement
        override fun updateTile() {
            heat = calculateHeat(sideHeat)
            temp += (heat * convertSpeed * delta()) / 60
            temp = Mathf.lerpDelta(temp, 0f, coolDownSpeed * convertSpeed)
            Log.info(temp)
        }

        override fun warmup() = warmupImpl()
        override fun draw() {
            drawer.draw(this)
        }
    }
}