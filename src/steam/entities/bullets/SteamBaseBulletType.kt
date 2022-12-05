package tvakot.entities.bullet

import mindustry.entities.Units
import mindustry.entities.bullet.BasicBulletType
import mindustry.gen.Bullet
import mindustry.gen.Teamc

open class SteamBaseBulletType : BasicBulletType() {
    var targetRange = 110f
    var rally = true

    fun target(b: Bullet): Teamc? {
        var target: Teamc? = if (heals()) {
            Units.closestTarget(null, b.x, b.y, targetRange,
                { e -> e.checkTarget(collidesAir, collidesGround) && e.team !== b.team && !b.hasCollided(e.id) })
                { t -> collidesGround && (t.team !== b.team || t.damaged()) && !b.hasCollided(t.id) }
        } else {
            Units.closestTarget( b.team,  b.x,  b.y, targetRange,
                { e -> e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) })
                { t -> collidesGround && !b.hasCollided(t.id) }
        }
        if(target == null && rally) target = b.owner as Teamc?
        return target
    }
}