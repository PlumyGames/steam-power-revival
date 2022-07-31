package steam.content

import mindustry.content.Blocks

object ContentsLoader {
    fun load() {
        SteamItems.apply {
            stone()
        }
        SteamFluids.apply {
            steam()
        }
        SteamUnitTypes.apply {
            epsilon()
        }
        SteamBlocks.apply {
            boiler()
            burner()
            fluidBurner()
            reservoir()
            well()
            pressureNode()
            coreFragment()
            heatAccumulator()
        }
        ContentsOverrider.apply {
            mechanicalDrill()
            pneumaticDrill()
            conveyor()
            alpha()
            beta()
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