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
import plumy.core.math.Progress
import steam.R
import steam.content.SteamFluids
import steam.world.pressure.IPressureConsumer
import steam.world.pressure.IPressureNode
import steam.world.pressure.IPressureProducer
import steam.world.pressure.pressureFact

class DrawSteamInside(scl: Float = 1f) : DrawBlock() {
    var color = R.C.steam
    var alpha = 0.3f * scl
    var particleSize = 2f * scl
    var particles = (10 * scl).toInt()
    var particleRad = 8f * scl - particleSize
    var particleLife = 80f * scl
    var reverse = true
    var particleSizeInterp: Interp = Interp.one
    var fadeMargin = 0.4f
    var rotateScl = 3f
    var particleInterp: Interp = Interp.PowIn(1.5f)
    var blending: Blending = Blending.normal
    override fun draw(build: Building) {
        val steamProp = build.liquids[SteamFluids.steam] / build.block.liquidCapacity
        if (steamProp > 0f) {
            val a = alpha * steamProp
            Draw.blend(blending)
            Draw.color(color)
            val base = Time.time / particleLife
            rand.setSeed(build.id.toLong())
            for (i in 0 until particles) {
                var fin = (rand.random(2f) + base) % 1f
                if (reverse) fin = 1f - fin
                val fout = 1f - fin
                val angle = rand.random(360f) + Time.time / rotateScl % 360f
                val len = particleRad * particleInterp.apply(fout)
                Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)))
                Fill.circle(
                    build.x + Angles.trnsx(angle, len),
                    build.y + Angles.trnsy(angle, len),
                    particleSize * particleSizeInterp.apply(fin) * steamProp
                )
            }
            Draw.blend()
            Draw.reset()
        }
    }
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
