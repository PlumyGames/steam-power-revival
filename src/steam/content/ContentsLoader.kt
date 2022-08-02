package steam.content

import mindustry.content.Blocks

object ContentsLoader {
    fun load() {
        SteamItems.apply {
            stone()
            iron()
            quartz()
        }
        SteamFluids.apply {
            steam()
        }
        SteamUnitTypes.apply {
            epsilon()
        }
        SteamBlocks.apply {
            rifle()
            quartzExtractor()
            boiler()
            blastFurnace()
            burner()
            fluidBurner()
            reservoir()
            well()
            pressureNode()
            coreFragment()
            mechPad()
            heatAccumulator()
            ironOre()
        }
        ContentsOverrider.apply {
            mechanicalDrill()
            pneumaticDrill()
            conveyor()
            siliconSmelter()
            alpha()
            beta()
            sand()
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