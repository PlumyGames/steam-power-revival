package steam.content

import arc.graphics.Color
import mindustry.type.Item

object SteamItems {
    lateinit var stone: Item
    lateinit var glass: Item
    lateinit var iron: Item
    lateinit var bauxite: Item
    lateinit var aluminium: Item
    lateinit var quartz: Item
    lateinit var steel: Item
    lateinit var salt: Item
    lateinit var sulfur: Item
    lateinit var depletedThorium: Item

    //chemicals

    lateinit var sodiumHydroxide: Item

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

    fun bauxite() {
        bauxite = Item("bauxite").apply {
            color = Color.valueOf("a36d64")
            hardness = 3
        }
    }

    fun aluminium() {
        aluminium = Item("aluminium").apply {
            color = Color.valueOf("afb0ba")
        }
    }

    fun quartz() {
        quartz = Item("quartz").apply {
            color = Color.valueOf("c9b6ab")
        }
    }

    fun salt() {
        salt = Item("salt").apply {
            color = Color.valueOf("d0d0d0")
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

    fun sodiumHydroxide() {
        sodiumHydroxide = Item("sodium-hydroxide").apply {
            color = Color.valueOf("8dd1b3")
        }
    }
}