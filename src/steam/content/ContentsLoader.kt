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
            bauxite()
            alumina()
            aluminium()
            quartz()
            salt()
            sodiumHydroxide()
            sulfur()
            steel()
            depletedThorium()
            circuitBroad()
        }
        SteamFluids.apply {
            steam()
            oxygen()
            chlorine()
            acid()
        }
        SteamUnitTypes.apply {
            alphaCombatDrone()
            alphaSupportDrone()
            epsilon()
            tau()
            defender()
            sprayer()
        }
        SteamBlocks.apply {
            rifle()
            frostbite()
            quartzExtractor()
            boiler()
            industrialBoiler()
            electrolysisPlant()
            burner()
            fluidBurner()
            heatRegulator()
            mixer()
            reservoir()
            flowgate()
            turbine()
            pneumaticEngine()
            pressureCranker()
            pressurizer()
            pressureNode()
            pressureMeter()
            pressureBridge()
            coreFragment()
            crate()
            mechPad()
            tauPad()
            healer()
            heatAccumulator()
            pressureSource()
            pressureVoid()
            oreIron()
            oreBauxite()
            sulfurCrystal()
            groundWater()
            groundOil()
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
            ore()
            water()
        }
        EntityRegistry.apply {
            register<PressureGraphUpdater>(::PressureGraphUpdater)
        }
    }

    fun loadAfterOreGenerated() {
        SteamBlocks.apply {
            sporePlanter()
            well()
            extractor()
            atmosphereConcentrator()
            stoneExcavator()
            blastFurnace()
            advancedFurnace()
            crystallizer() //loaded here, so it display next to furnaces on block selection
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
                        stats.add(SteamStat.pressureConsume, it.pressureConsumption, SteamStatUnit.atmSecond)
                    if (it is IPressureProducerBlock)
                        stats.add(SteamStat.pressureProduce, it.pressureOutput, SteamStatUnit.atmSecond)
                }
            }
        }
    }
}