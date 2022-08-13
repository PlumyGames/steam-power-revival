package steam.world.distribution

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Geometry
import arc.util.Eachable
import arc.util.Tmp
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.entities.TargetPriority
import mindustry.entities.units.BuildPlan
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import plumy.core.math.clamp
import steam.DebugOnly
import steam.utils.sheet
import steam.world.pressure.IPressureNode
import steam.world.pressure.PressureBlock
import steam.world.pressure.drawWholeGraphForDebug

class PressurePipe(name: String) : PressureBlock(name) {
    lateinit var regions: Array<TextureRegion>
    lateinit var blendRegion: TextureRegion

    init {
        underBullets = true
        floating = true
        noUpdateDisabled = true
        conveyorPlacement = true
        canOverdrive = false
        priority = TargetPriority.transport
        buildType = Prov { PressurePipeBuild() }
    }

    override fun load() {
        super.load()
        regions = "$name-tile".sheet(size * 32, size * 32)
        blendRegion = Core.atlas.find("$name-blend")
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        drawPlanConfig(plan, list)
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
            drawWholeGraphForDebug()
        }

        override fun draw() {
            Draw.rect(regions[drawIndex], x, y)
            DebugOnly {
                Tmp.c1.set(Pal.redLight).a((currentPressure / graph.maxPressure).clamp)
                Draw.color(Tmp.c1)
                val radius = size * tilesize / 2.5f
                Lines.square(x, y, radius + 1f, rotation.toFloat())
                Draw.reset()
            }
            //improvement maybe required
            Draw.z(Layer.blockUnder)
            for (j in 0 until 4) {
                val i = nearby(j) ?: continue
                if (i is IPressureNode && i !is PressurePipeBuild && !i.block.squareSprite)
                    Draw.rect(
                        blendRegion,
                        x + Geometry.d4[j].x * tilesize,
                        y + Geometry.d4[j].y * tilesize,
                        j * 90f
                    )
            }
            Draw.z()
        }
    }
}