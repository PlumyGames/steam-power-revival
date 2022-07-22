package steam.content

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import steam.steam

object SteamStat {
    val steam = StatCat("steam".steam)
    val maxPressure = steam("max-pressure")
    val steamCapacity = steam("steam-capacity")
}

operator fun StatCat.invoke(name: String) = Stat(name.steam, this)