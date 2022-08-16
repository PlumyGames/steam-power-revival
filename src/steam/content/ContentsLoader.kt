package steam.content

import mindustry.content.Blocks
import plumy.world.EntityRegistry
import steam.world.mech.MechPad
import steam.world.pressure.PressureGraphUpdater

object ContentsLoader {
    fun load() {
        SteamItems.apply {
            stone()
            glass()
            iron()
            quartz()
            steel()
            depletedThorium()
        }
        SteamFluids.apply {
            steam()
            acid()
        }
        SteamUnitTypes.apply {
            epsilon()
        }
        SteamBlocks.apply {
            rifle()
            quartzExtractor()
            boiler()
            industrialBoiler()
            burner()
            fluidBurner()
            heatRegulator()
            reservoir()
            turbine()
            pneumaticEngine()
            well()
            pressurizer()
            pressureNode()
            pressureBridge()
            coreFragment()
            mechPad()
            heatAccumulator()
            pressureSource()
            pressureVoid()
            ironOre()
        }
        ContentsOverrider.apply {
            mechanicalDrill()
            pneumaticDrill()
            conveyor()
            siliconSmelter()
            kiln()
            graphitePress()
            conduit()
            alpha()
            beta()
            sand()
            stone()
            tsunami()
            items()
        }
        EntityRegistry.apply {
            register<PressureGraphUpdater>(::PressureGraphUpdater)
        }
    }

    fun loadAfterOreGenerated() {
        SteamBlocks.apply {
            blastFurnace()
            advancedFurnace()
            crystallizer()
            thermalCentrifuge()
        }
    }

    fun resisterEvents() {
        MechPad.registerTapEvent()
    }

    fun unlockForDebug() {
        Blocks.heatRedirector.requirements = emptyArray()
        Blocks.electricHeater.requirements = emptyArray()
        Blocks.phaseHeater.requirements = emptyArray()
        Blocks.slagHeater.requirements = emptyArray()
        Blocks.atmosphericConcentrator.requirements = emptyArray()
    }
}