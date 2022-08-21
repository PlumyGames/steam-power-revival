package steam.world.researching

import arc.func.Prov
import mindustry.gen.Building
import mindustry.world.Block

class ResearchLab(name: String) : Block(name) {
    var researchSpeed = 1f
    var researchTier = 1

    init {
        update = true
        solid = true
        buildType = Prov { LabBuild() }
    }
    inner class LabBuild : Building() {

    }
}