package steam.world.heating

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Eachable
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockFlag
import kotlin.math.max

class FluidCombustor(name: String) : Block(name) {
    companion object {
        var visualMaxOutput = -1f
    }

    var heatConvertFactor = 4f //base heat generate
    var drawer: DrawBlock = DrawDefault()

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
        super.init()
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    inner class CombustorBuild : Building(), HeatBlock {
        /** Serialized*/
        var heat = 0f
        override fun heat() = heat
        override fun heatFrac() = heat / visualMaxOutput
    }

    override fun setBars() {
        super.setBars()
        addBar<CombustorBuild>("heat") {
            Bar("bar.heat", Pal.lightOrange, it::heatFrac)
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