package steam.gen

import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.TextureRegion
import mindustry.Vars
import mindustry.type.Item
import mindustry.type.Liquid
import plumy.dsl.bundle
import plumy.texture.*
import steam.Res
import steam.steam

typealias FluidCapsule = Item

object FluidCapsuleGenerator {
    val capsules = HashMap<Liquid, FluidCapsule>()
    val blacklist = HashSet<Liquid>()

    fun generateAll() {

        val allLiquids = Vars.content.liquids().copy().filter {
            !it.isHidden && it !in blacklist
        }
        for (liquid in allLiquids) {
            val capsule = generateCapsule(liquid)
            capsules[liquid] = capsule
        }
    }

    fun generateCapsule(liquid: Liquid): CapsuleItem {
        return CapsuleItem(liquid)
    }
}

class CapsuleItem (
    val original: Liquid
) : Item("${original.name}-capsule") {
    init {
        localizedName = "${original.localizedName} ${"capsule".steam.bundle}"
        color = original.color
        flammability = original.flammability * 1.2f
        explosiveness = original.explosiveness * 1.2f
    }

    override fun loadIcon() {
        val icon = FluidCapsuleIconGenerator.generate(original)
        fullIcon = icon
        uiIcon = icon
    }
}

object FluidCapsuleIconGenerator {
    val bakery = StackIconBakery(32, 32).apply {
        postProcessors.add(AntiAliasingLayerProcessor)
    }

    val alpha = 0.5f

    lateinit var base: Pixmap
    lateinit var decal: Pixmap
    lateinit var liquid: Pixmap

    fun loadPixmap(internalName: String) = Res.load(name = internalName).use { it.readAsPixmap() }

    fun load() {
        base = loadPixmap("/sprites/template/capsule.png")
        decal = loadPixmap("/sprites/template/capsule-decal.png")
        liquid = loadPixmap("/sprites/template/capsule-liquid.png")
    }

    fun generate(liquid: Liquid): TextureRegion {
        val baseLayer = Layer(base.toLayerBuffer())
        val decalLayer = Layer(decal.toLayerBuffer()) {
            +TintBlendLayerProcessor(liquid.color.cpy().a(alpha))
        }
        val liquidLayer = Layer(FluidCapsuleIconGenerator.liquid.toLayerBuffer()) {
            +TintBlendLayerProcessor(liquid.color.cpy())
        }
        val baked = bakery.bake(baseLayer, decalLayer, liquidLayer)
        return TextureRegion(Texture(baked.createPixmap()))
    }
}