package steam.entities.bullets

import arc.util.Time
import arc.util.Tmp
import mindustry.gen.Bullet

class VectorHomingBulletType : SteamBaseBulletType() {
    var homingMultiplier = 0.85f

    override fun update(b: Bullet) {
        super.update(b)
        b.vel.add(Tmp.v2.trns(b.angleTo(target(b)), Time.delta * homingMultiplier)).limit(b.type.speed * Time.delta)
    }
}