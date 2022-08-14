package steam.world.drawer

import arc.graphics.Blending
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import mindustry.Vars
import mindustry.gen.Building
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawParticles
import plumy.core.math.Progress
import steam.R
import steam.world.pressure.IPressureConsumer
import steam.world.pressure.IPressureNode
import steam.world.pressure.IPressureProducer
import steam.world.pressure.pressureFact

fun DrawSteamInside() = DrawParticles().apply {
    color = R.C.steam
    alpha = 0.3f
    particleSize = 2.5f
    particles = 8
    particleRad = 4f
    particleLife = 80f
    reverse = true
    particleSizeInterp = Interp.one
}

open class DrawSteamLeaking : DrawBlock() {
    var color = R.C.steam
    var alpha = 0.4f
    var particles = if (Vars.mobile) 15 else 30
    var particleLife = 140f
    var particleRad = 7f
    var particleSize = 3f
    var fadeMargin = 0.4f
    var rotateScl = 3f
    var particleInterp: Interp = Interp.PowIn(1.5f)
    var particleSizeInterp: Interp = Interp.slope
    var blending: Blending = Blending.normal
    open fun getSteamFact(build: Building): Progress = if (build is IPressureNode) build.pressureFact else 0f
    override fun draw(build: Building) {
        val steam = getSteamFact(build)
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

    companion object {
        operator fun invoke(config: DrawSteamLeaking.() -> Unit) = DrawSteamLeaking().apply(config)
    }
}

class DrawPressureOutput(
    var visualMaxProduced: Float = 10f,
) : DrawSteamLeaking() {
    override fun getSteamFact(build: Building): Progress =
        if (build is IPressureProducer) build.pressureProduced / visualMaxProduced else 0f

    companion object {
        operator fun invoke(
            visualMaxProduced: Float = 10f,
            config: DrawPressureOutput.() -> Unit,
        ) = DrawPressureOutput(visualMaxProduced).apply(config)
    }
}

class DrawPressureInput(
    var visualMaxRequired: Float = 10f,
) : DrawSteamLeaking() {
    override fun getSteamFact(build: Building): Progress =
        if (build is IPressureConsumer) build.pressureRequired / visualMaxRequired else 0f

    companion object {
        operator fun invoke(
            visualMaxRequired: Float = 10f,
            config: DrawPressureInput.() -> Unit,
        ) = DrawPressureInput(visualMaxRequired).apply(config)
    }
}
