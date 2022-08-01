package steam.content

import arc.graphics.Color
import mindustry.type.Item

object SteamItems {
    lateinit var stone: Item
    lateinit var iron: Item
    lateinit var quartz: Item

    fun stone() {
        stone = Item("stone").apply {
            color = Color.valueOf("686b7b")
        }
    }
    fun iron() {
        iron = Item("iron").apply {
            color = Color.valueOf("bfbfbf")
            hardness = 3
        }
    }
    fun quartz() {
        quartz = Item("quartz").apply {
            color = Color.valueOf("c9b6ab")
        }
    }
}