package steam.content

import mindustry.type.Liquid
import steam.graphic.R

object SteamFluids {
    lateinit var steam: Liquid
    fun steam() {
        steam = Liquid("steam").apply {
            color = R.C.steam
        }
    }
}