package steam.content

import mindustry.world.meta.Attribute

object SteamAttribute {
    lateinit var stone: Attribute

    fun load() {
        stone = Attribute.add("stone")
    }
}