package steam.world.crafting

import arc.Core.bundle
import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.util.Strings.autoFixed
import arc.util.Tmp
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.ui.Bar
import steam.content.SteamFluids
import steam.R
import steam.world.module.Celsius100
import steam.world.module.IPressureContainer
import steam.world.module.celsius
import kotlin.math.min

class Boiler(name: String) : TemperatureBlock(name) {
    /**
     * How much temp is lost in evaporation
     */
    val evaporationTempLose = 0.1f.celsius
    val overheatMax: Float
        get() = tempCap - Celsius100
    val steamCapacity: Float
        get() = liquidCapacity
    var warmupSpeed = 0.1f
    var boilingRate = Celsius100
    /**
     * How much water will be converted to steam per tick
     */
    var evaporationSpeed = 1f
    val water = Liquids.water
    val steam = SteamFluids.steam

    init {
        hasLiquids = true
        buildType = Prov { BoilerBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    inner class BoilerBuild : TemperatureBuild(), IPressureContainer {
        override val steamAmount: Float
            get() = liquids[steam]
        override val steamProportion: Float
            get() = steamAmount / steamCapacity
        val restRoomForSteam: Float
            get() = (steamCapacity - liquids[steam]).coerceAtLeast(0f)
        val overheat: Float
            get() = (temp - Celsius100).coerceAtLeast(0f) / overheatMax
        var warmup = 0f

        override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
            return liquid == water && liquids[water] < liquidCapacity
        }

        override fun updateTile() {
            super.updateTile()
            if (efficiency > 0 && temp >= boilingRate) {
                val amount = (edelta() * evaporationSpeed * (1f + overheat))
                    .coerceAtMost(min(liquids[water], restRoomForSteam))
                if (amount > 0.0001f) {
                    liquids.remove(water, amount)
                    liquids.add(steam, amount)
                    temp -= amount * evaporationTempLose * (1f + Mathf.pow(2f, 1f + overheat))
                    warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed)
                } else warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed)
            }
        }

        override fun warmup() = warmup
    }
    override fun setBars() {
        super.setBars()
        removeBar("liquid")
        addLiquidBar(water)
        addLiquidBar(steam)
        addBar<BoilerBuild>("temp") { Bar(
            { bundle.format("bar.temp", autoFixed(it.temp, 1)) },
            { Tmp.c1.set(Color.orange).lerp(R.C.burnerFlame, it.temp / tempCap).cpy() },
            { it.temp / boilingRate }
        )}
    }
}