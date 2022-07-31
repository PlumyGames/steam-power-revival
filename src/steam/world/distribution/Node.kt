package steam.world.distribution

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Geometry
import arc.util.Eachable
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.world.Block
import steam.utils.sheet


class Node(name: String) : Block(name) {
    lateinit var regions: Array<TextureRegion>

    init {
        solid = true
        update = true
    }

    override fun load() {
        super.load()
        regions = "$name-tile".sheet(size * 32, size * 32)
    }

    override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan>) {
        var drawIndex = 0
        val scl = tilesize * plan.animScale

        for (i in 0..3) {
            val pt = Geometry.d4((4 - i) % 4).cpy().add(plan.x, plan.y)
            if (world.build(pt.x, pt.y) is NodeBuild) {
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

    inner class NodeBuild : Building() {
        var drawIndex = 0

        override fun onProximityUpdate() {
            super.onProximityUpdate()

            drawIndex = 0
            for(i in 0 until 4) {
                if(nearby((4 - i) % 4) is NodeBuild) drawIndex += 1 shl i
            }
        }

        override fun draw() {
            Draw.rect(regions[drawIndex], x, y)
        }
    }
}