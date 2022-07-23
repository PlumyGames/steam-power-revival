package steam.world.heating

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Strings
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.ui.LiquidDisplay
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.consumers.ConsumeLiquidFlammable
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Stat
import steam.utils.addTable
import kotlin.math.max

class FluidCombustor(name: String) : Block(name) {
    companion object {
        var visualMaxOutput = -1f
    }

    var heatConvertFactor = 3.5f //base heat generate
    var warmupRate = 0.15f
    var warmupSpeed = 0.019f
    var drawer: DrawBlock = DrawDefault()
    var minFlammabilityReq = 0.3f
    var amount = 0.1f //amount consumed per tick
    lateinit var flammableFilter: ConsumeLiquidFlammable

    init {
        update = true
        hasLiquids = true
        sync = true
        flags = EnumSet.of(BlockFlag.factory)
        rotateDraw = false
        rotate = true
        rotateDraw = false
        canOverdrive = false
        drawArrow = true
        buildType = Prov { CombustorBuild() }
    }

    fun toHeat(flammability: Float) = flammability * heatConvertFactor
    override fun init() {
        if (visualMaxOutput < 0f && heatConvertFactor >= 0f) {
            for (liquid in Vars.content.liquids()) {
                visualMaxOutput = max(visualMaxOutput, toHeat(liquid.flammability))
            }
        }
        flammableFilter = consume(ConsumeLiquidFlammable(minFlammabilityReq))
        super.init()
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    inner class CombustorBuild : Building(), HeatBlock {
        /** Serialized*/
        var heat = 0f
        /** Serialized*/
        var warmup = 0f
        /** Serialized */
        var curFlammability = 0f
        override fun updateEfficiencyMultiplier() {
            curFlammability = flammableFilter.efficiencyMultiplier(this)
        }

        override fun updateTile() {
            if (efficiency > 0f) {
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed)
                val targetHeat = toHeat(curFlammability)
                heat = Mathf.approachDelta(heat, targetHeat * efficiency, warmupRate * delta())
            } else {
                // cool down
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
                heat = Mathf.approachDelta(heat, 0f, warmupRate * delta())
            }
        }

        override fun heat() = heat
        override fun heatFrac() = heat / visualMaxOutput
        override fun warmup() = warmup
        override fun draw() {
            drawer.draw(this)
        }

        override fun drawLight() {
            super.drawLight()
            drawer.drawLight(this)
        }
    }

    override fun setBars() {
        super.setBars()
        addBar<CombustorBuild>("heat") {
            Bar("bar.heat", Pal.lightOrange, it::heatFrac)
        }
    }

    override fun setStats() {
        super.setStats()
        stats.remove(Stat.input)
        stats.add(Stat.input) { stat ->
            stat.row()
            Vars.content.liquids().each<Liquid>(flammableFilter.filter) {
                stat.addTable {
                    background(Tex.whiteui)
                    setColor(Pal.darkestGray)
                    addTable {
                        add(LiquidDisplay(it, amount * 60f, true)).row()
                        add("${Strings.autoFixed(toHeat(it.flammability), 1)} ${Core.bundle["unit.heatunits"]}")
                    }.grow().pad(10f)
                }.growX().pad(5f)
            }
        }
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        drawer.drawPlan(this, plan, list)
    }

    override fun getRegionsToOutline(out: Seq<TextureRegion>) {
        drawer.getRegionsToOutline(this, out)
    }

    override fun icons(): Array<TextureRegion> = drawer.finalIcons(this)
}