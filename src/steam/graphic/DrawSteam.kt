package steam.graphic

import arc.graphics.Blending
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import mindustry.gen.Building
import mindustry.world.draw.DrawBlock

import steam.world.module.IPressureContainer
class DrawSteam : DrawBlock() {
    var color = R.C.steam
    var alpha = 0.4f
    var particles = 30
    var particleLife = 140f
    var particleRad = 7f
    var particleSize = 3f
    var fadeMargin = 0.4f
    var rotateScl = 3f
    var particleInterp: Interp = Interp.PowIn(1.5f)
    var particleSizeInterp: Interp = Interp.slope
    var blending: Blending = Blending.normal
    override fun draw(build: Building) {
        if (build !is IPressureContainer) return
        val steam = build.steamProportion
        if (steam > 0f) {
            val a = alpha * steam
            Draw.blend(blending)
            Draw.color(color)
            val base = Time.time / particleLife
            rand.setSeed(build.id.toLong())
            for (i in 0 until particles) {
                val fout = (rand.random(2f) + base) % 1f
                val fin = 1f - fout
                val angle: Float = rand.random(360f) + Time.time / rotateScl % 360f
                val len: Float = particleRad * particleInterp.apply(fout)
                Draw.alpha(a * (1f - Mathf.curve(fout, 1f - fadeMargin)))
                Fill.circle(
                    build.x + Angles.trnsx(angle, len),
                    build.y + Angles.trnsy(angle, len),
                    particleSize * particleSizeInterp.apply(fout) * steam
                )
            }
            Draw.blend()
            Draw.reset()
        }
    }
}