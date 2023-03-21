package steam.type

import arc.Core
import arc.graphics.g2d.Draw
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.type.UnitType
import plumy.core.assets.EmptyTR
import steam.entities.units.SentryEntity
import steam.steam

//todo implement multi-bullet weapon
class SentryUnitType(name: String) : UnitType(name) {
    var supportRegion = EmptyTR
    var dragForce = .2f
    var baseLayer = Layer.blockUnder

    override fun load() {
        super.load()
        supportRegion = Core.atlas.find("$name-support")
        if (!supportRegion.found()) supportRegion = Core.atlas.find("sentry-base".steam)
    }

    override fun draw(unit: Unit) {
        super.draw(unit)
        val u = unit as SentryEntity
        Draw.z(baseLayer)
        Draw.rect(supportRegion, u.originX, u.originY)
    }
}