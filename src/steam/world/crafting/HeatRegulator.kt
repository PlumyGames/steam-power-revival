package steam.world.crafting

import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.struct.Seq
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Pal
import plumy.core.arc.retain
import plumy.core.math.lerp
import plumy.world.config
import steam.utils.addTable

class HeatRegulator(name: String) : TemperatureBlock(name) {
    var ventSpeed = 1.5f

    init {
        solid = true
        update = true
        configurable = true
        saveConfig = true
        buildType = Prov { HeatRegulatorBuild() }
    }

    override fun init() {
        super.init()
        config<HeatRegulatorBuild, Float> {
            this.ventAmount = it
        }
    }

    inner class HeatRegulatorBuild : TemperatureBuild() {
        var ventAmount = 25f
        var venting = Seq<Building>()
        var totalProgress = 0f
        var warmup = 0f
        override fun updateTile() {
            super.updateTile()
            val speed = ventSpeed * edelta()
            venting.forEach {
                if (!it.isValid) {
                    venting.remove(it)
                    return@forEach
                }
                if (it is TemperatureBuild) {
                    if (it.temp > ventAmount) {
                        it.temp -= speed
                        temp += speed
                        warmup = warmup.lerp(efficiency, 0.1f)
                    }
                }
            }
            if (!canVent()) warmup = warmup.lerp(0f, 0.1f)
            totalProgress += warmup
        }

        fun canVent() = venting.size > 0
        override fun shouldConsume() = enabled && canVent()
        override fun totalProgress() = totalProgress
        override fun warmup() = warmup
        override fun config() = ventAmount
        override fun buildConfiguration(table: Table) {
            table.addTable {
                background(Tex.whiteui)
                setColor(Pal.darkestGray)
                slider(25f, tempCap, 1f, ventAmount, false) { configure(it) }.row()
                label { ventAmount.toString() }
            }.pad(10f).grow()
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            venting.set(proximity)
            venting.retain { it is TemperatureBuild }
        }
    }
}