package steam.content

import mindustry.Vars
import mindustry.content.Blocks
import plumy.world.EntityRegistry
import steam.world.mech.MechPad
import steam.world.pressure.*
import steam.world.stat.SteamStat
import steam.world.stat.SteamStatUnit

object ContentsLoader {
    fun load() {
        SteamItems.apply {
            stone()
            glass()
            iron()
            quartz()
            sulfur()
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
            frostbite()
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
            extractor()
            pressureCranker()
            pressurizer()
            pressureNode()
            pressureBridge()
            coreFragment()
            crate()
            mechPad()
            menderTurret()
            heatAccumulator()
            pressureSource()
            pressureVoid()
            ironOre()
            sulfurCrystal()
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
            mender()
            items()
            water()
        }
        EntityRegistry.apply {
            register<PressureGraphUpdater>(::PressureGraphUpdater)
        }
    }

    fun loadAfterOreGenerated() {
        SteamBlocks.apply {
            sporePlanter()
            stoneExcavator()
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

    fun iterateStats() {
        Vars.content.blocks().forEach {
            when(it) {
                is IPressurizedBlock -> it.run{
                        stats.add(SteamStat.pressureCapacity, pressureCapacity, SteamStatUnit.atm)
                    if (it is IPressureConsumerBlock)
                        stats.add(SteamStat.pressureConsume, it.pressureRequired, SteamStatUnit.atmSecond)
                    if (it is IPressureProducerBlock)
                        stats.add(SteamStat.pressureProduce, it.pressureOutput, SteamStatUnit.atmSecond)
                }
            }
        }
    }
}