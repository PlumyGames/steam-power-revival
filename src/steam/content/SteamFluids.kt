package steam.content

import mindustry.type.Liquid

object SteamFluids {
    lateinit var steam: Liquid
    fun steam() {
        steam = Liquid("steam")
    }
}