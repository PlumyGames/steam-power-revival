package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.type.Liquid
import steam.content.SteamFluids
import steam.world.module.Celsius100
import steam.world.module.ISteamContainer
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
    /**
     * How much water will be converted to steam per tick
     */
    val evaporationSpeed = 1f
    val water: Liquid = Liquids.water
    val steam = SteamFluids.steam

    init {
        hasLiquids = true
        buildType = Prov { BoilerBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    inner class BoilerBuild : TemperatureBuild(), ISteamContainer {
        override val steamAmount: Float
            get() = liquids[steam]
        override val steamProportion: Float
            get() = steamAmount / steamCapacity
        val restRoomForSteam: Float
            get() = (steamCapacity - liquids[steam]).coerceAtLeast(0f)
        val overheat: Float
            get() = (temp - Celsius100).coerceAtLeast(0f) / overheatMax

        override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
            return liquid == water && liquids[water] < liquidCapacity
        }

        override fun updateTile() {
            super.updateTile()
            if (efficiency > 0 && temp >= Celsius100) {
                val amount = (edelta() * evaporationSpeed * (1f + overheat))
                    .coerceAtMost(min(liquids[water], restRoomForSteam))
                if (amount > 0.0001f) {
                    liquids.remove(water, amount)
                    liquids.add(steam, amount)
                    temp -= amount * evaporationTempLose * (1f + Mathf.pow(2f, 1f + overheat))
                }
            }
        }
    }

    override fun setBars() {
        super.setBars()
        removeBar("liquid")
        addLiquidBar(water)
        addLiquidBar(steam)
    }
}