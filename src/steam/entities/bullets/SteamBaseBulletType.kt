package steam.entities.bullets

import arc.util.Time
import arc.util.Tmp
import mindustry.entities.Units
import mindustry.entities.bullet.BasicBulletType
import mindustry.gen.Bullet
import mindustry.gen.Teamc

open class SteamBaseBulletType : BasicBulletType() {
    var targetRange = 110f
    var rally = true
    var vectorHoming = false
    var homingMultiplier = 0.85f

    override fun updateHoming(b: Bullet) {
        if(vectorHoming) b.vel.add(Tmp.v2.trns(b.angleTo(target(b)), Time.delta * homingMultiplier)).limit(b.type.speed * Time.delta)
        super.updateHoming(b)
    }
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