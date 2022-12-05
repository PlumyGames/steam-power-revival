package tvakot.entities.bullet

import arc.util.Time
import arc.util.Tmp
import mindustry.gen.Bullet

class VectorHomingBulletType : SteamBaseBulletType() {
    var homingMultiplier = 0.085f

    override fun update(b: Bullet) {
        b.vel.add(Tmp.v2.trns(b.angleTo(target(b)), homingMultiplier * Time.delta))
        super.update(b)
    }
}