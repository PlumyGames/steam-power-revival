package steam.world.stat

import mindustry.world.meta.StatUnit

typealias SUnit = SteamStatUnit
object SteamStatUnit {
    val celsius = StatUnit("celsius")
    val celsiusSecond = StatUnit("celsius-second")

    val atm = StatUnit("atm")
    val atmSecond = StatUnit("atm-second")
}