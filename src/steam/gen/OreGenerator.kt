package steam.gen

import arc.graphics.Pixmap
import arc.graphics.Texture
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
    val crushedOres = HashMap<OreItem, OreCrushed>()
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
            val crushedOre = generateCrushedOre(ore)
            crushedOres[ore] = crushedOre
            all += crushedOre
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

    fun generateCrushedOre(ore: OreItem): OreCrushed {
        return OreCrushed(ore)
    }
}

object OreIconGenerator {
    // only generate 32x32 at present
    val bakery = StackIconBakery(32, 32).apply {
        postProcessors.add(AntiAliasingLayerProcessor)
    }
    val rand = Rand()
    var baseNumber = 1
    var crushedOreNumber = 1
    var baseTextures = ArrayList<Pixmap>()
    var patchTextures = ArrayList<Pixmap>()
    var crushedOreTextures = ArrayList<Pixmap>()
    var alpha = 0.48f
    fun base(index: Int) = "/sprites/template/ore-base$index.png"
    fun patch(index: Int) = "/sprites/template/ore-patch$index.png"
    fun crushedOre(index: Int) = "/sprites/template/crushed-ore$index.png"
    fun loadPixmap(internalName: String) = Res.load(name = internalName).use { it.readAsPixmap() }

    fun load() {
        for (i in 0 until baseNumber) {
            baseTextures += loadPixmap(base(i))
            patchTextures += loadPixmap(patch(i))
        }
        for (i in 0 until crushedOreNumber) {
            crushedOreTextures += loadPixmap(crushedOre(i))
        }
    }

    fun generate(ore: RawOre): TextureRegion {
        rand.setSeed(ore.name.hashCode().toLong())
        val layer = rand.random(0, baseTextures.size - 1)
        val baseLayer = Layer(baseTextures[layer].toLayerBuffer())
        val patchLayer = Layer(patchTextures[layer].toLayerBuffer()) {
            +TintBlendLayerProcessor(ore.color.cpy().a(alpha))
        }
        val baked = bakery.bake(baseLayer, patchLayer)
        return TextureRegion(Texture(baked.createPixmap()))
    }

    fun generate(ore: OreCrushed): TextureRegion {
        rand.setSeed(ore.name.hashCode().toLong())
        val crushedOreLayer = Layer(crushedOreTextures[rand.random(0, crushedOreTextures.size - 1)].toLayerBuffer()) {
            +TintBlendLayerProcessor(ore.color.cpy().a(alpha))
        }
        val baked = bakery.bake(crushedOreLayer)
        return TextureRegion(Texture(baked.createPixmap()))
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
        radioactivity = original.radioactivity * 0.3f
        cost = original.cost * 0.8f
        healthScaling = original.healthScaling * 0.5f
    }

    override fun loadIcon() {
        val icon = OreIconGenerator.generate(this)
        fullIcon = icon
        uiIcon = icon
    }
}

class OreCrushed(
    original: Item,
) : Item("crushed-${original.name}-ore") {
    init {
        localizedName = "${"crushed".steam.bundle} ${original.localizedName} ${"ore".steam.bundle}"
        color = original.color
        flammability = original.flammability
        explosiveness = original.explosiveness
        hardness = original.hardness
        charge = original.charge
        radioactivity = original.radioactivity * 0.65f
        cost = original.cost * 0.3f
        healthScaling = original.healthScaling * 0.3f
    }

    override fun loadIcon() {
        val icon = OreIconGenerator.generate(this)
        fullIcon = icon
        uiIcon = icon
    }
}