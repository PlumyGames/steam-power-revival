package steam.world.module

import arc.math.Interp
import mindustry.gen.Buildingc
import steam.Var.SteamWarmupTemp
import steam.Var.SteamWarmupTempSpan

interface ITemperatureBlock : Buildingc {
    var temp: Float
    companion object{
        fun ITemperatureBlock.warmupImpl() = if (temp >= SteamWarmupTemp) {
            Interp.smoother.apply(((temp - SteamWarmupTemp) / SteamWarmupTempSpan).coerceIn(0f, 1f))
        } else 0f
    }
}