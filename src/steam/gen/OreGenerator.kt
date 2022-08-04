package steam.gen

import arc.Core.bundle
import arc.graphics.Pixmap
import arc.graphics.g2d.TextureRegion
import arc.math.Rand
import mindustry.Vars
import mindustry.type.Item
import mindustry.world.blocks.environment.OreBlock
import plumy.texture.*
import steam.Res
import steam.SteamMod
import steam.steam

private typealias RawItem = Item

object OreGenerator {
    val all = HashMap<RawItem, GeneratedOre>()
    val blacklist = HashSet<RawItem>()
    val extra = HashSet<RawItem>()
    fun generateAll() {
        val steamMod = SteamMod.mod
        for (ore in Item.getAllOres().toList().distinctBy {
            it.name
        }.filter {
            val mod = it.minfo.mod
            !it.isHidden && (mod == null || mod == steamMod) && it !in blacklist
        } + extra) {
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
    val bakery: IBakery = StackIconMaker(32, 32)
    val rand = Rand()
    var baseNumber = 1
    var patchNumber = 1
    var baseTextures = ArrayList<Pixmap>()
    var patchTextures = ArrayList<Pixmap>()
    var alpha = 0.662f
    fun base(index: Int) = "/sprites/template/ore-base$index.png"
    fun patch(index: Int) = "/sprites/template/ore-patch$index.png"
    fun loadPixmap(internalName: String) = Res.load(name = internalName).use { it.toPixmap() }
    fun load() {
        for (i in 0 until baseNumber) {
            baseTextures += loadPixmap(base(i))
        }
        for (i in 0 until patchNumber) {
            patchTextures += loadPixmap(patch(i))
        }
    }

    val baseLayerProcess = PlainLayerProcessor()
    fun generate(ore: GeneratedOre): TextureRegion {
        rand.setSeed(ore.name.hashCode().toLong())
        val baseLayer = RawPixmapModelLayer(baseTextures[rand.random(0, baseTextures.size - 1)])
        val patchLayer = RawPixmapModelLayer(patchTextures[rand.random(0, patchTextures.size - 1)])
        baseLayer += baseLayerProcess
        patchLayer += TintLayerProcessor(ore.color.cpy().a(alpha))
        val baked = bakery.bake(baseLayer, patchLayer)
        return baked.toTextureRegion()
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

    override fun loadIcon() {
        val icon = OreIconGenerator.generate(this)
        fullIcon = icon
        uiIcon = icon
    }
}