package steam.world.heating

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Eachable
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockFlag

class ItemBurner(name: String) : Block(name) {
    var maxVisualOutput = 10f
    var minFlammabilityReq = 0.3f
    var warmupRate = 0.15f
    var warmupSpeed = 0.019f
    var heatingTimeFactor = 60f //base consume time
    var heatConvertFactor = 60f //base heat generate
    lateinit var flammableFilter: ConsumeItemFlammable
    var drawer: DrawBlock = DrawDefault()

    init {
        update = true
        hasItems = true
        sync = true
        flags = EnumSet.of(BlockFlag.factory)
        rotateDraw = false
        rotate = true
        canOverdrive = false
        drawArrow = true
        buildType = Prov { BurnerBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun init() {
        flammableFilter = consume(ConsumeItemFlammable(minFlammabilityReq))
        super.init()
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        drawer.drawPlan(this,plan, list)
    }

    override fun getRegionsToOutline(out: Seq<TextureRegion>) {
        drawer.getRegionsToOutline(this,out)
    }

    override fun icons() = drawer.finalIcons(this)
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
        override fun updateTile() {
            if (efficiency > 0f) {
                heatingTime += delta()
                if (targetHeatingTime > 0f && heatingTime >= targetHeatingTime) {
                    // if the item is burnt out, try to consume next
                    consumeFuel()
                }
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed)
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

        override fun draw() {
            drawer.draw(this)
        }

        override fun drawLight() {
            super.drawLight()
            drawer.drawLight(this)
        }

        fun warmupTarget() = 1f
        override fun heat() = heat
        override fun heatFrac() = heat / maxVisualOutput

        override fun warmup() = warmup
    }
}