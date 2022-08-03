package steam.content

import mindustry.content.Blocks

object ContentsLoader {
    fun load() {
        SteamItems.apply {
            stone()
            glass()
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
            kiln()
            conduit()
            alpha()
            beta()
            sand()
            stone()
        }
    }

    fun loadAfterOreGenerated() {
        SteamBlocks.apply {
            blastFurnace()
            crystalizer()
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