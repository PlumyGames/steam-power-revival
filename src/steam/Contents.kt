package steam

import mindustry.content.Blocks
import steam.content.SteamBlocks

object Contents {
    fun load() {
        SteamBlocks.apply {
            boiler()
            burner()
        }
    }

    fun unlockForDebug() {
        Blocks.heatRedirector.requirements = emptyArray()
        Blocks.electricHeater.requirements = emptyArray()
        Blocks.phaseHeater.requirements = emptyArray()
        Blocks.slagHeater.requirements = emptyArray()
        Blocks.atmosphericConcentrator.requirements = emptyArray()
    }
}