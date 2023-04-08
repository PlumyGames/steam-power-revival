package steam.type

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.struct.Seq
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.type.UnitType
import steam.entities.units.SentryEntity
import steam.steam

//todo implement multi-bullet weapon
class SentryUnitType(name: String) : UnitType(name) {
    var dragForce = .2f
    var baseLayer = Layer.blockUnder

    var supportRegion : TextureRegion? = null
    var supportOutlineRegion : TextureRegion? = null

    override fun load() {
        super.load()
        supportRegion = Core.atlas.find("$name-sentry-base")
        if (!Core.atlas.isFound(supportRegion)) {
            supportRegion = Core.atlas.find("sentry-base".steam)
            supportOutlineRegion = Core.atlas.find("sentry-base-outline".steam)
        } else supportOutlineRegion = Core.atlas.find("$name-sentry-outline")
    }

    override fun draw(unit: Unit) {
        super.draw(unit)
        val u = unit as SentryEntity
        Draw.z(baseLayer)
        drawBaseOutline(u)
        Draw.rect(supportRegion, u.originX, u.originY)
    }

    fun drawBaseOutline(u : SentryEntity) {
        if (!Core.atlas.isFound(supportOutlineRegion)) return

        applyColor(u)
        applyOutlineColor(u)
        Draw.rect(supportOutlineRegion, u.originX, u.originY)
        Draw.reset()
    }

    override fun getRegionsToOutline(out: Seq<TextureRegion>) {
        super.getRegionsToOutline(out)
        out.add(supportRegion)
    }
}