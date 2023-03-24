package steam.entities.units

import arc.util.Time
import arc.util.Tmp
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
        Tmp.v1.set(originX, originY).sub(this).limit(dst(originX, originY) * sentry.dragForce * Time.delta)
        vel.add(Tmp.v1)
        super.update()
    }
}