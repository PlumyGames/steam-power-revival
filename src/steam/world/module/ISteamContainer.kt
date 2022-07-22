package steam.world.module

import arc.math.Interp
import steam.Var

interface ISteamContainer : ITemperatureBlock {
    var steamAmount: Float

    companion object {
        fun ITemperatureBlock.warmupImpl() = if (temp >= Var.SteamWarmupTemp) {
            Interp.smoother.apply(((temp - Var.SteamWarmupTemp) / Var.SteamWarmupTempSpan).coerceIn(0f, 1f))
        } else 0f
    }
}