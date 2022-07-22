package steam.world.module

import arc.math.Interp
import mindustry.gen.Buildingc
import steam.Var

interface ITemperatureBlock : Buildingc {
    var temp: Float
    companion object {
        fun ITemperatureBlock.warmupImpl() = if (temp >= Var.SteamWarmupTemp) {
            Interp.smoother.apply(((temp - Var.SteamWarmupTemp) / Var.SteamWarmupTempSpan).coerceIn(0f, 1f))
        } else 0f
    }
}