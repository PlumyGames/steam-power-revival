package steam.entities.units

import arc.util.Time
import mindustry.gen.UnitEntity
import steam.type.SentryUnitType

class SentryEntity : UnitEntity() {
    var originX = 0f
    var originY = 0f

    override fun add() {
        if (!isAdded) {
            originX = x
            originY = y
        }
        super.add()
    }

    override fun update() {
        val sentry = type as? SentryUnitType ?: return super.update()
        vel.set(originX, originY).sub(this).limit(dst(originX, originY) * sentry.dragForce * Time.delta)
        super.update()
    }
}