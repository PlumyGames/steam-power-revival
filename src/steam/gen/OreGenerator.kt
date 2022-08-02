package steam.gen

import arc.Core.bundle
import mindustry.type.Item
import steam.steam

object OreGenerator {
    val all = ArrayList<GeneratedOre>()
    val blacklist = HashSet<Item>()
    fun generateAll() {
        for (ore in Item.getAllOres().toList().distinctBy {
            it.name
        }.filter {
            !it.isHidden && it.minfo.mod == null && it !in blacklist
        }) {
            val generated = generate(ore)
            all.add(generated)
        }
    }

    fun generate(ore: Item): GeneratedOre {
        val generated = GeneratedOre(ore)
        return generated
    }
}

class GeneratedOre(
    val original: Item,
) : Item("ore-${original.name}") {
    init {
        localizedName = "${original.localizedName} ${bundle["ore".steam]}"
    }
}