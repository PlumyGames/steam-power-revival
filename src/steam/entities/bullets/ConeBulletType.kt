package steam.entities.bullets

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.gen.Bullet
import mindustry.gen.Healthc
import mindustry.graphics.Pal

class ConeBulletType : BulletType() {
    var color = Pal.heal
    var damageInterval = 20f
    var coneAmt = 50
    var rad = 20f
    var length = 110f
    var lengthInterp = Interp.linear

    init {
        removeAfterPierce = false
        speed = 0f
        despawnEffect = Fx.none
        shootEffect = Fx.none
        lifetime = 30f
        impact = true
        keepVelocity = false
        collides = false
        hittable = false
        pierce = true
        absorbable = false
        optimalLifeFract = 0.5f
    }

    override fun draw(b: Bullet) {
        super.draw(b)

        val curLen = currentLength(b)

        Draw.z(layer)
        Draw.color(color)

        Fill.arc(b.x, b.y, curLen, rad / 360f, b.rotation() - rad / 2f, coneAmt)

        Draw.reset()
    }

    fun currentLength(b: Bullet): Float {
        return length * lengthInterp.apply(b.fslope())
    }

    fun applyDamage(b: Bullet) = b.run {
        val curLen = currentLength(this)

        Units.nearbyEnemies(team, x, y, curLen) {
            tryHit(this, it)
        }
        Units.nearbyBuildings(x, y, curLen) {
            tryHit(this, it)
        }
    }

    fun tryHit(b: Bullet, t: Healthc) = b.run {
        val ang = rotation()
        val angToTarget = angleTo(t)
        if(Angles.within(ang, angToTarget, rad / 2f))
            Damage.collidePoint(this, team, hitEffect, t.x, t.y)
    }

    override fun update(b: Bullet) = b.run {
        super.update(b)
        if(timer(0, damageInterval)) {
            applyDamage(this)
        }
    }
}