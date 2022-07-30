package steam.world.distribution

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Time
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
            Draw.rect(regions[(Time.time.toInt() / 60) % 16], x, y)
        }
    }
}