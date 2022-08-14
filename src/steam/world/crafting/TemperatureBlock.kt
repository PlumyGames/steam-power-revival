package steam.world.crafting

import arc.Core
import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.util.Strings
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatConsumer
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import plumy.core.Serialized
import plumy.core.assets.TRs
import steam.R
import steam.world.temp.Celsius100
import steam.world.temp.ITemperatureBlock
import steam.world.temp.ITemperatureBlock.Companion.warmupImpl
import steam.world.temp.celsius

open class TemperatureBlock(name: String) : Block(name) {
    var heatRequirement = 10f
    var minRequired = Celsius100
    var tempCap = 350f.celsius
    var convertSpeed = 2f //convert x2
    var coolDownSpeed = 0.025f / 60f //lose 2.5% each second
    var drawer: DrawBlock = DrawDefault()
    var hasTemp = true

    init {
        update = true
        solid = true
        sync = true
        buildType = Prov { TemperatureBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun icons(): TRs = drawer.finalIcons(this)
    open inner class TemperatureBuild : Building(), ITemperatureBlock, HeatConsumer {
        @Serialized
        override var temp = 25f.celsius
        override val tempCap get() = this@TemperatureBlock.tempCap
        override var flash = 0f
        var sideHeat = FloatArray(4)
        @Serialized
        var heat = 0f
        override fun sideHeat() = sideHeat
        override fun heatRequirement() = heatRequirement
        override fun updateTile() {
            if (hasTemp) {
                heat = calculateHeat(sideHeat)
                //25f is base temp
                temp = Mathf.lerpDelta(temp, 25f, coolDownSpeed * convertSpeed)
                temp += (heat * convertSpeed * delta()) / 60
                if (temp > tempCap) kill() //explode when overheat
            }
        }

        override fun warmup() = warmupImpl()
        override fun draw() {
            drawer.draw(this)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(temp)
            write.f(heat)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            temp = read.f()
            heat = read.f()
        }
    }

    override fun setBars() {
        super.setBars()
        if (hasTemp) addBar<TemperatureBuild>("temp") {
            Bar(
                { Core.bundle.format("bar.temp", Strings.autoFixed(it.temp, 1)) },
                { Tmp.c1.set(R.C.burnerFlame).lerp(Color.orange, it.temp / tempCap) },
                { it.temp / minRequired }
            )
        }
    }
}