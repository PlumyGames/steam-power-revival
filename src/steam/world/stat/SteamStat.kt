package steam.world.stat

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import steam.steam

object SteamStat {
    val steam = StatCat("steam".steam)
    val temp = StatCat("temp".steam)
    val pressure = StatCat("pressure".steam)

    val maxPressure = steam("max-pressure")
    val steamCapacity = steam("steam-capacity")

    val tempConvert = temp("heat-convert")
    val tempLose = temp("heat-lose")

    val pressureCapacity = pressure("pressure-capacity")
    val pressureProduce = pressure("pressure-produce")
    val pressureConsume = pressure("pressure-consume")
}

operator fun StatCat.invoke(name: String) = Stat(name.steam, this)