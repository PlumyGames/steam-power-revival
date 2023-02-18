package steam.entities.bullets

import arc.util.Time
import arc.util.Tmp
import mindustry.entities.Units
import mindustry.entities.bullet.BasicBulletType
import mindustry.gen.*
import mindustry.gen.Unit

open class SteamBaseBulletType : BasicBulletType() {
    var targetRange = 110f
    var rally = true
    var vectorHoming = false
    var followTarget = false
    var lockTarget = false
    var homingMultiplier = 0.7f

    var mineAmount = 3
    var mineTier = 0

    override fun updateHoming(b: Bullet) {
        if(vectorHoming) b.vel.add(Tmp.v2.trns(b.angleTo(target(b)), Time.delta * homingMultiplier)).limit(b.type.speed * Time.delta)
        super.updateHoming(b)
    }

    fun ownerTarget(owner: Entityc): PosTeam? {
        if (owner is Unit) {
            val posTeam = PosTeam.create()
            posTeam.set(owner.aimX, owner.aimY)
            return posTeam
        }
        return null
    }

    fun target(b: Bullet): Teamc? {
        if (lockTarget) return (b.data as? SteamBulletEntry)?.baseTarget

        val owner = b.owner
        if (followTarget) return ownerTarget(owner)

        var target: Teamc? = if (heals()) {
            Units.closestTarget(null, b.x, b.y, targetRange,
                { e -> e.checkTarget(collidesAir, collidesGround) && e.team !== b.team && !b.hasCollided(e.id) })
                { t -> collidesGround && (t.team !== b.team || t.damaged()) && !b.hasCollided(t.id) }
        } else {
            Units.closestTarget( b.team,  b.x,  b.y, targetRange,
                { e -> e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) })
                { t -> collidesGround && !b.hasCollided(t.id) }
        }
        if(target == null && rally) target = owner as Teamc?
        return target
    }

    override fun despawned(b: Bullet) {
        super.despawned(b)
        if(mineTier > 0) {
            val drop = b.tileOn().drop() ?: return
            (b.owner as? Itemsc)?.run {
                if (drop.hardness <= mineTier && acceptsItem(drop)) for (i in 0 until mineAmount.coerceAtMost(20))
                    Time.run(i / 3f) { Call.transferItemToUnit(drop, b.aimX, b.y, this) }
            }
        }
    }

    override fun init(b: Bullet) {
        super.init(b)
        b.data = SteamBulletEntry(ownerTarget(b.owner))
    }

    data class SteamBulletEntry (
        val baseTarget: Teamc?
    )
}