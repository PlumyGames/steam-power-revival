package steam.world.distribution

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Geometry
import arc.util.Eachable
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.entities.units.BuildPlan
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import steam.DebugOnly
import steam.utils.sheet
import steam.world.pressure.IPressureNode
import steam.world.pressure.PressureBlock

class PressurePipe(name: String) : PressureBlock(name) {
    lateinit var regions: Array<TextureRegion>
    lateinit var blendRegion: TextureRegion
    override fun load() {
        super.load()
        regions = "$name-tile".sheet(size * 32, size * 32)
        blendRegion = Core.atlas.find("$name-blend")
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan>) {
        var drawIndex = 0
        val scl = tilesize * plan.animScale

        for (i in 0..3) {
            val pt = Geometry.d4((4 - i) % 4).cpy().add(plan.x, plan.y)
            if (world.build(pt.x, pt.y) is IPressureNode) {
                drawIndex += 1 shl i
            } else {
                val f = booleanArrayOf(false)
                list.each { p ->
                    if (!f[0] && p.x == pt.x && p.y == pt.y) {
                        f[0] = true
                    }
                }
                if (f[0]) {
                    drawIndex += 1 shl i
                }
            }
        }
        Draw.rect(regions[drawIndex], plan.drawx(), plan.drawy(), scl, scl)
    }

    inner class PressurePipeBuild : PressureBuild() {
        var drawIndex = 0
        override fun onProximityUpdate() {
            super.onProximityUpdate()
            drawIndex = 0
            for (i in 0 until 4) {
                if (nearby((4 - i) % 4) is IPressureNode) drawIndex += 1 shl i
            }
        }

        override fun drawSelect() {
            super.drawSelect()
            DebugOnly {
                graph.all.forEach {
                    Drawf.square(it.x, it.y, it.block().size * tilesize / 2.5f, 0f)
                }
            }
        }

        override fun draw() {
            Draw.rect(regions[drawIndex], x, y)

            //improvement maybe required
            Draw.z(Layer.blockUnder)
            for(j in 0 until 4) {
                val i = nearby(j) ?: continue
                if(i is IPressureNode && i !is PressurePipeBuild && !i.block.squareSprite)
                    Draw.rect(blendRegion,
                    x + Geometry.d4[j].x * tilesize,
                    y + Geometry.d4[j].y * tilesize)
            }
            Draw.z()
        }
    }
}