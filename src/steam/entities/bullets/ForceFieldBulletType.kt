package steam.entities.bullets

import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.util.Time
import mindustry.gen.Bullet
import mindustry.gen.Groups
import mindustry.graphics.Layer
import plumy.core.math.lerp

class ForceFieldBulletType : ConeBulletType() {
    var maxCapacity = 60f
    var chargeSpeed = 1f

    init {
        layer = Layer.shields
    }

    protected var ent: Bullet? = null
    protected val shieldConsumer = Cons { b: Bullet ->
        val ent = ent ?: return@Cons
        val ang = ent.rotation()
        val angToTarget = ent.angleTo(b)
        if (b.team != ent.team && b.type.absorbable
            && Angles.within(ang, angToTarget, rad / 2f)
            && ent.dst(b) <= currentLength(ent)
        ) {
            val dat = (ent.data as ProjectorBulletEntry)
            if (b.damage() > dat.charge)
                b.damage -= dat.charge
            else
                b.absorb()
            dat.charge -= b.damage

            hitEffect.at(b)
        }
    }

    override fun drawColor(b: Bullet) {
        Draw.color(b.team.color)
    }

    override fun init(b: Bullet) {
        super.init(b)
        b.data = ProjectorBulletEntry(0f, 0f)
    }

    override fun update(b: Bullet) {
        super.update(b)
        val dat = b.data as ProjectorBulletEntry
        dat.charge += chargeSpeed * Time.delta
        dat.charge = dat.charge.coerceAtMost(maxCapacity)
        dat.lerpLength = dat.lerpLength.lerp(dat.charge, 0.33f)
        deflectBullet(b)
    }

    fun deflectBullet(b: Bullet) {
        val length = currentLength(b)
        ent = b

        Groups.bullet.intersect(b.x - length, b.y - length, length * 2f, length * 2f, shieldConsumer)
    }

    override fun currentLength(b: Bullet): Float {
        val dat = b.data as ProjectorBulletEntry
        return length * lengthInterp.apply(b.fslope()) * dat.lerpLength / maxCapacity
    }

    data class ProjectorBulletEntry (
        var lerpLength: Float,
        var charge: Float
    )

    companion object {
        inline operator fun invoke(config: ForceFieldBulletType.() -> Unit) =
            ForceFieldBulletType().apply(config)
    }
}