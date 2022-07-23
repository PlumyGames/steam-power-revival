package steam.world.module

import arc.math.Interp
import mindustry.gen.Buildingc
import steam.Var

typealias Celsius = Float

val Float.celsius: Celsius
    get() = this
val Double.celsius: Celsius
    get() = this.toFloat()

interface ITemperatureBlock : Buildingc {
    var temp: Celsius

    companion object {
        fun ITemperatureBlock.warmupImpl() = if (temp >= Var.SteamWarmupTemp) {
            Interp.smoother.apply(((temp - Var.SteamWarmupTemp) / Var.SteamWarmupTempSpan).coerceIn(0f, 1f))
        } else 0f
    }
}