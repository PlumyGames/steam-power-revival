package steam.world.stat

import mindustry.world.meta.StatUnit

typealias SUnit = SteamStatUnit
object SteamStatUnit {
    val celsius = StatUnit("unit.celsius")
    val celsiusSecond = StatUnit("unit.celsiusSecond")

    val atm = StatUnit("unit.atm")
    val atmSecond = StatUnit("unit.atmSecond")
}