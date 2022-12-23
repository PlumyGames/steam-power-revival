package steam.gen

import arc.graphics.Pixmap
import arc.graphics.g2d.TextureRegion
import arc.math.Rand
import mindustry.Vars
import mindustry.type.Item
import mindustry.world.blocks.environment.OreBlock
import plumy.dsl.bundle
import plumy.texture.*
import steam.Res
import steam.SteamMod
import steam.steam

private typealias OreItem = Item

object OreGenerator {
    val all = ArrayList<Item>()
    val rawOres = HashMap<OreItem, RawOre>()
    val powders = HashMap<OreItem, OrePowder>()
    val blacklist = HashSet<OreItem>()
    val extra = HashSet<OreItem>()
    fun generateAll() {
        val steamMod = SteamMod.mod
        val allOres = (Item.getAllOres().toList() + extra).filter {
            val mod = it.minfo.mod
            !it.isHidden && (mod == null || mod == steamMod) && it !in blacklist
        }.distinctBy { it.name }
        for (ore in allOres) {
            val rawOre = generateRawOre(ore)
            rawOres[ore] = rawOre
            all += rawOre
            val powder = generatePowder(ore)
            powders[ore] = powder
            all += powder
        }
    }

    fun replaceAll() {
        Vars.content.blocks().toList().filterIsInstance<OreBlock>().forEach {
            val original: Item? = it.itemDrop
            if (original != null) {
                val ore = rawOres[original]
                if (ore != null)
                    it.itemDrop = ore
            }
        }
    }

    fun generateRawOre(ore: OreItem): RawOre {
        return RawOre(ore)
    }

    fun generatePowder(ore: OreItem): OrePowder {
        return OrePowder(ore)
    }
}

object OreIconGenerator {
    // only generate 32x32 at present
    val bakery: IBakery = StackIconMaker(32, 32)
    val rand = Rand()
    var baseNumber = 1
    var patchNumber = 1
    var powderNumber = 1
    var baseTextures = ArrayList<Pixmap>()
    var patchTextures = ArrayList<Pixmap>()
    var powderTextures = ArrayList<Pixmap>()
    var alpha = 0.59f
    fun base(index: Int) = "/sprites/template/ore-base$index.png"
    fun patch(index: Int) = "/sprites/template/ore-patch$index.png"
    fun powder(index: Int) = "/sprites/template/ore-powder$index.png"
    fun loadPixmap(internalName: String) = Res.load(name = internalName).use { it.toPixmap() }
    fun load() {
        for (i in 0 until baseNumber) {
            baseTextures += loadPixmap(base(i))
        }
        for (i in 0 until patchNumber) {
            patchTextures += loadPixmap(patch(i))
        }
        for (i in 0 until powderNumber) {
            powderTextures += loadPixmap(powder(i))
        }
    }

    val baseLayerProcess = PlainLayerProcessor()
    fun generate(ore: RawOre): TextureRegion {
        rand.setSeed(ore.name.hashCode().toLong())
        val baseLayer = RawPixmapModelLayer(baseTextures[rand.random(0, baseTextures.size - 1)])
        val patchLayer = RawPixmapModelLayer(patchTextures[rand.random(0, patchTextures.size - 1)])
        baseLayer += baseLayerProcess
        patchLayer += TintBlendLayerProcessor(ore.color.cpy().a(alpha))
        val baked = bakery.bake(baseLayer, patchLayer)
        return baked.toTextureRegion()
    }

    fun generate(ore: OrePowder): TextureRegion {
        rand.setSeed(ore.name.hashCode().toLong())
        val powderLayer = RawPixmapModelLayer(powderTextures[rand.random(0, powderTextures.size - 1)])
        powderLayer += TintBlendLayerProcessor(ore.color.cpy().a(alpha))
        val baked = bakery.bake(powderLayer)
        return baked.toTextureRegion()
    }
}

class RawOre(
    original: Item,
) : Item("oregen-${original.name}") {
    init {
        localizedName = "${original.localizedName} ${"ore".steam.bundle}"
        color = original.color
        flammability = original.flammability
        explosiveness = original.explosiveness
        hardness = original.hardness
        charge = original.charge
        radioactivity = original.radioactivity
        cost = original.cost
        cost = original.cost * 0.8f
        healthScaling = original.healthScaling * 0.5f
    }

    override fun loadIcon() {
        val icon = OreIconGenerator.generate(this)
        fullIcon = icon
        uiIcon = icon
    }
}

class OrePowder(
    original: Item,
) : Item("powder-${original.name}") {
    init {
        localizedName = "${original.localizedName} ${"powder".steam.bundle}"
        color = original.color
        flammability = original.flammability
        explosiveness = original.explosiveness
        hardness = original.hardness
        charge = original.charge
        radioactivity = original.radioactivity
        cost = original.cost * 0.3f
        healthScaling = original.healthScaling * 0.3f
    }

    override fun loadIcon() {
        val icon = OreIconGenerator.generate(this)
        fullIcon = icon
        uiIcon = icon
    }
}