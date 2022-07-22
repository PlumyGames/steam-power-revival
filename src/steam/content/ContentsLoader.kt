package steam.content

import mindustry.content.Blocks

object ContentsLoader {
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