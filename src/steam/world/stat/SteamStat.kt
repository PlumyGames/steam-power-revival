package steam.world.stat

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import steam.steam

object SteamStat {
    val steam = StatCat("steam".steam)
    val temp = StatCat("temp".steam)

    val maxPressure = steam("max-pressure")
    val steamCapacity = steam("steam-capacity")

    val tempConvert = temp("heat-convert")
    val tempLose = temp("heat-lose")
}

operator fun StatCat.invoke(name: String) = Stat(name.steam, this)