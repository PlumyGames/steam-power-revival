package steam.world.heating

import arc.func.Prov
import arc.math.Mathf
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault

class ItemBurner(name: String) : Block(name) {
    var maxVisualOutput = 10f
    var minFlammabilityReq = 0.3f
    var warmupRate = 0.15f
    var warmupSpeed = 0.019f
    var heatingTimeFactor = 60f
    var heatConvertFactor = 60f
    lateinit var flammableFilter: ConsumeItemFlammable
    var drawer: DrawBlock = DrawDefault()

    init {
        update = true
        hasItems = true
        buildType = Prov { BurnerBuild() }
    }

    override fun init() {
        flammableFilter = consume(ConsumeItemFlammable(minFlammabilityReq))
        super.init()
    }

    inner class BurnerBuild : Building(), HeatBlock {
        /** Serialized*/
        var heat = 0f
        /** Serialized*/
        var warmup = 0f
        /** Serialized*/
        var heatingTime = 0f
        /** Serialized*/
        var curFlammability = 0f
        /** Serialized*/
        var targetHeatingTime = 0f
        override fun updateEfficiencyMultiplier() {
            curFlammability = flammableFilter.efficiencyMultiplier(this)
            targetHeatingTime = toHeatingTime(curFlammability)
        }

        fun toHeatingTime(flammability: Float) = flammability * heatingTimeFactor
        fun toHeat(flammability: Float) = flammability * heatConvertFactor
        override fun update() {
            if (efficiency > 0f) {
                heatingTime += delta()
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed)
                if (targetHeatingTime > 0f && heatingTime >= targetHeatingTime) {
                    // if the item is burnt out, try to consume next
                    consumeFuel()
                }
                heat = Mathf.approachDelta(heat, toHeat(curFlammability) * efficiency, warmupRate * delta())
            } else {
                heatingTime = 0f
                // cool down
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
                heat = Mathf.approachDelta(heat, 0f, warmupRate * delta())
            }
        }

        fun consumeFuel() {
            consume()
        }

        fun warmupTarget() = 1f
        override fun heat() = heat
        override fun heatFrac() = heat / maxVisualOutput
    }
}