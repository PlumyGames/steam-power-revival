package steam.world.crafting

import arc.func.Prov

class HeatRegulator(name: String) : TemperatureBlock(name) {
    init {
        solid = true
        update = true
        buildType = Prov { HeatRegulatorBuild() }
    }
    inner class HeatRegulatorBuild : TemperatureBuild() {

    }
}