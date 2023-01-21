package steam.content

import arc.graphics.Color
import mindustry.content.Blocks
import mindustry.type.Liquid
import steam.R
import steam.world.fluids.Acid

object SteamFluids {
    lateinit var steam: Liquid
    lateinit var acid: Acid

    lateinit var chlorine: Liquid
    lateinit var oxygen: Liquid

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

    fun chlorine() {
        chlorine = Liquid("chlorine").apply {
            color = Color.valueOf("ffdf46")
            gas = true
        }
    }

    fun oxygen() {
        oxygen = Liquid("oxygen").apply {
            color = Color.valueOf("9bbcf1")
            gas = true
        }
    }
}