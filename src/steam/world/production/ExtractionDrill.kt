package steam.world.production

import arc.Core
import arc.func.Prov
import arc.math.geom.Point2
import mindustry.gen.Building
import mindustry.world.Block
import plumy.core.assets.EmptyTR

//WIP
class ExtractionDrill(name: String) : Block(name) {
    var extractSpeed = 75f
    var drillOffset = 2

    var headRegion = EmptyTR
    var rotatorRegion = EmptyTR
    var frameRegion = EmptyTR

    init {
        update = true
        solid = true
        rotate = true
        rotateDraw = false
        buildType = Prov { ExtractionDrillBuild() }
    }

    override fun load() {
        super.load()
        headRegion = Core.atlas.find("$name-head")
        rotatorRegion = Core.atlas.find("$name-rotator")
        frameRegion = Core.atlas.find("$name-frame")
    }

    inner class ExtractionDrillBuild : Building() {
        var target = Point2()
    }
}