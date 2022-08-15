package steam.content

import mindustry.content.Blocks
import mindustry.type.Liquid
import steam.R
import steam.world.fluids.Acid

object SteamFluids {
    lateinit var steam: Liquid
    lateinit var acid: Acid
    fun steam() {
        steam = Liquid("steam").apply {
            color = R.C.steam
            gas = true
        }
    }

    fun acid() {
        acid = Acid("acid").apply {
            acidproof += Blocks.liquidSource
        }
    }
}