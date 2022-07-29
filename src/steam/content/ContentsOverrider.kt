package steam.content

import mindustry.content.Blocks.*
import mindustry.world.blocks.distribution.Conveyor
import mindustry.world.blocks.production.Drill
import mindustry.world.consumers.ConsumeLiquid

object ContentsOverrider {
    fun mechanicalDrill() {
        (mechanicalDrill as Drill).apply {
            removeConsumer(findConsumer { it is ConsumeLiquid })
            consumeLiquid(SteamFluids.steam, 0.05f)
            drillTime = 300f
            liquidBoostIntensity = 1f
        }
    }
    fun pneumaticDrill() {
        (pneumaticDrill as Drill).apply {
            removeConsumer(findConsumer { it is ConsumeLiquid })
            consumeLiquid(SteamFluids.steam, 0.05f)
            drillTime = 200f
            liquidBoostIntensity = 1f
        }
    }

    fun conveyor(){
        (conveyor as Conveyor).apply {
            hasPower = true
            consumesPower = true
            conductivePower = true
            consumePower(1f / 60f)
        }
    }
}