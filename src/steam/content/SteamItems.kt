package steam.content

import arc.graphics.Color
import mindustry.type.Item

object SteamItems {
    lateinit var stone: Item
    lateinit var glass: Item
    lateinit var iron: Item
    lateinit var quartz: Item
    lateinit var steel: Item
    lateinit var sulfur: Item
    lateinit var depletedThorium: Item

    fun stone() {
        stone = Item("stone").apply {
            color = Color.valueOf("686b7b")
        }
    }
    fun glass() {
        glass = Item("glass").apply {
            color = Color.valueOf("ececec")
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
    fun sulfur() {
        sulfur = Item("sulfur").apply {
            color = Color.valueOf("d3b458")
        }
    }
    fun steel() {
        steel = Item("steel").apply {
            color = Color.valueOf("7f838a")
            cost = 5f
        }
    }
    fun depletedThorium() {
        depletedThorium = Item("depleted-thorium").apply {
            color = Color.valueOf("6b4474")
            radioactivity = 0.25f
        }
    }
}