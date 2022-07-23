package steam.world.crafting

import arc.func.Prov
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.world.consumers.ConsumeLiquid
import steam.content.SteamFluids
import steam.world.module.ISteamContainer

class Boiler(name: String) : TemperatureBlock(name) {

    init {
        hasLiquids = true
        buildType = Prov { BoilerBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    inner class BoilerBuild : TemperatureBuild(), ISteamContainer {
        override var steamAmount: Float
            get() = TODO("Not yet implemented")
            set(value) {}

        override fun updateTile() {
            super.updateTile()
        }
    }

    class BoilerConsume(amount: Float) : ConsumeLiquid(Liquids.water, amount) {
        override fun update(build: Building) {
            val amount = amount * build.edelta()
            build.liquids.remove(liquid, amount)
            build.liquids.add(SteamFluids.steam, amount)
        }
    }
}