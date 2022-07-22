package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import arc.util.Log
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatConsumer
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import steam.world.module.ISteamContainer.Companion.warmupImpl
import steam.world.module.ITemperatureBlock

class TemperatureBlock(name: String) : Block(name) {
    var heatRequirement = 10f
    var convertionRateMul = 25f
    var convertSpeed = 0.01f //lose 1% each second
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

    inner class TemperatureBuild : Building(), ITemperatureBlock, HeatConsumer {
        override var temp = 0f
        var sideHeat = FloatArray(4)
        var heat = 0f
        override fun sideHeat() = sideHeat
        override fun heatRequirement() = heatRequirement
        override fun updateTile() {
            heat = calculateHeat(sideHeat)
            temp += heat * convertionRateMul * delta()
            temp = Mathf.lerpDelta(temp, 0f, convertSpeed / 60)
            Log.info(temp)
        }

        override fun warmup() = warmupImpl()
        override fun draw() {
            drawer.draw(this)
        }
    }
}