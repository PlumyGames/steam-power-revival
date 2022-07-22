package steam.world.heating

import arc.Core.bundle
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Strings.autoFixed
import mindustry.Vars.content
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.ui.Bar
import mindustry.ui.ItemDisplay
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Stat
import steam.utils.addTable
import kotlin.math.max

class ItemBurner(name: String) : Block(name) {
    companion object {
        var visualMaxOutput = -1f
    }

    var minFlammabilityReq = 0.3f
    var warmupRate = 0.15f
    var warmupSpeed = 0.019f
    var heatingTimeFactor = 60f //base consume time
    var heatConvertFactor = 4f //base heat generate
    lateinit var flammableFilter: ConsumeItemFlammable
    var drawer: DrawBlock = DrawDefault()

    init {
        update = true
        hasItems = true
        sync = true
        flags = EnumSet.of(BlockFlag.factory)
        rotateDraw = false
        rotate = true
        rotateDraw = false
        canOverdrive = false
        drawArrow = true
        buildType = Prov { BurnerBuild() }
    }

    fun toHeatingTime(flammability: Float) = flammability * heatingTimeFactor
    fun toHeat(flammability: Float) = flammability * heatConvertFactor
    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun init() {
        if (visualMaxOutput < 0f && heatConvertFactor >= 0f) {
            for (item in content.items()) {
                visualMaxOutput = max(visualMaxOutput, toHeat(item.flammability))
            }
        }
        flammableFilter = consume(ConsumeItemFlammable(minFlammabilityReq))
        super.init()
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        drawer.drawPlan(this, plan, list)
    }

    override fun getRegionsToOutline(out: Seq<TextureRegion>) {
        drawer.getRegionsToOutline(this, out)
    }

    override fun icons(): Array<TextureRegion> = drawer.finalIcons(this)
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

        override fun updateTile() {
            if (efficiency > 0f) {
                heatingTime += delta()
                if (targetHeatingTime > 0f && heatingTime >= targetHeatingTime) {
                    // if the item is burnt out, try to consume next
                    consumeFuel()
                    heatingTime = 0f
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
        override fun heatFrac() = heat / visualMaxOutput
        override fun warmup() = warmup
    }

    override fun setBars() {
        super.setBars()
        addBar<BurnerBuild>("heat") {
            Bar("bar.heat", Pal.lightOrange, it::heatFrac)
        }
    }

    override fun setStats() {
        super.setStats()
        stats.remove(Stat.input)
        stats.add(Stat.input) { stat ->
            stat.row()
            content.items().each(flammableFilter.filter) { i: Item ->
                stat.addTable {
                    background(Tex.whiteui)
                    setColor(Pal.darkestGray)
                    addTable {
                        add(ItemDisplay(i, 1, toHeatingTime(i.flammability), false)).row()
                        add("${autoFixed(toHeat(i.flammability), 1)} ${bundle["unit.heatunits"]}").row()
                        add("${autoFixed(toHeatingTime(i.flammability) / 60f, 1)} ${bundle["unit.seconds"]}").color(Color.gray)
                    }.grow().pad(10f)
                }.growX().pad(5f)
            }
        }
    }
}