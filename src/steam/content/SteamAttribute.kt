package steam.content

import mindustry.world.meta.Attribute

object SteamAttribute {
    lateinit var stone: Attribute
    lateinit var sporeGrow: Attribute //places that can grow spores

    fun load() {
        stone = Attribute.add("stone")
        sporeGrow = Attribute.add("spore")
    }
}