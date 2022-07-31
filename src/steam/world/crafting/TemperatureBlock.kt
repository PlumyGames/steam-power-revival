package steam.world.crafting

import arc.Core
import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.util.Strings
import arc.util.Tmp
import mindustry.gen.Building
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatConsumer
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import steam.R
import steam.world.module.Celsius100
import steam.world.module.ITemperatureBlock
import steam.world.module.ITemperatureBlock.Companion.warmupImpl
import steam.world.module.celsius

open class TemperatureBlock(name: String) : Block(name) {
    var heatRequirement = 10f
    var minRequired = Celsius100
    var tempCap = 350f.celsius
    var convertSpeed = 2f //convert x2
    var coolDownSpeed = 0.025f / 60f //lose 2.5% each second
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
            //25f is base temp
            temp = Mathf.lerpDelta(temp, 25f, coolDownSpeed * convertSpeed)
            temp += (heat * convertSpeed * delta()) / 60
            if(temp > tempCap) kill() //explode when overheat
        }
        override fun warmup() = warmupImpl()
        override fun draw() {
            drawer.draw(this)
        }
    }
    override fun setBars() {
        super.setBars()
        addBar<TemperatureBuild>("temp") { Bar(
            { Core.bundle.format("bar.temp", Strings.autoFixed(it.temp, 1)) },
            { Tmp.c1.set(R.C.burnerFlame).lerp(Color.orange, it.temp / tempCap) },
            { it.temp / minRequired }
        )
        }
    }
}