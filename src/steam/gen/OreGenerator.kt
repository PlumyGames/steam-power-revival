package steam.gen

import arc.Core.bundle
import arc.math.Rand
import mindustry.Vars
import mindustry.type.Item
import mindustry.world.blocks.environment.OreBlock
import steam.SteamMod
import steam.steam

private typealias RawItem = Item

object OreGenerator {
    val all = HashMap<RawItem, GeneratedOre>()
    val blacklist = HashSet<RawItem>()
    fun generateAll() {
        val steamMod = SteamMod.mod
        for (ore in Item.getAllOres().toList().distinctBy {
            it.name
        }.filter {
            val mod = it.minfo.mod
            !it.isHidden && (mod == null || mod == steamMod) && it !in blacklist
        }) {
            val generated = generate(ore)
            all[ore] = generated
        }
    }

    fun replaceAll() {
        Vars.content.blocks().toList().filterIsInstance<OreBlock>().forEach {
            val original: Item? = it.itemDrop
            if (original != null) {
                val ore = all[original]
                if (ore != null)
                    it.itemDrop = ore
            }
        }
    }

    fun generate(ore: RawItem): GeneratedOre {
        val generated = GeneratedOre(ore)
        return generated
    }
}

object OreIconGenerator {
    // only generate 32x32 at present
    val bakery: IBakery = IconMaker(32, 32)
    val rand = Rand()
    fun load(){

    }
    fun generate(ore: GeneratedOre) {
        rand.setSeed(ore.name.hashCode().toLong())

    }
}

class GeneratedOre(
    original: Item,
) : Item("oregen-${original.name}") {
    init {
        localizedName = "${original.localizedName} ${bundle["ore".steam]}"
        color = original.color
        flammability = original.flammability
        explosiveness = original.explosiveness
        hardness = original.hardness
        charge = original.charge
        radioactivity = original.radioactivity
        cost = original.cost
    }
/*
    override fun loadIcon() {

    }*/
}