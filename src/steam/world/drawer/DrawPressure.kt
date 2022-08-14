package steam.world.drawer

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.util.Time
import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import plumy.core.WhenNotPaused
import plumy.core.assets.EmptyTR
import plumy.core.math.Progress
import steam.world.pressure.IPressureNode
import steam.world.pressure.pressureFact

class DrawOverpressure(
    var suffix: String = "-lights",
    ) : DrawBlock() {
    /** temp threshold at which lights start flashing  */
    var threshold: Progress = 0.46f
    var lightsRegion = EmptyTR
    override fun load(block: Block) {
        lightsRegion = Core.atlas.find("${block.name}$suffix")
    }

    override fun draw(build: Building) {
        (build as? IPressureNode)?.apply {
            val progress = pressureFact
            if (progress > threshold) {
                WhenNotPaused {
                    flash += (1f + (progress - threshold) / (1f - threshold) * 5.4f) * Time.delta
                }
                Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9f, 1f))
                Draw.alpha(0.3f)
                Draw.rect(lightsRegion, x, y)
            }
        }
    }
}
