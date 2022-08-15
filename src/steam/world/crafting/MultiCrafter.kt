package steam.world.crafting

import arc.Core.bundle
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ButtonGroup
import arc.scene.ui.ImageButton
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.content.Fx
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.ui.ItemDisplay
import mindustry.ui.LiquidDisplay
import mindustry.ui.Styles
import mindustry.world.consumers.Consume
import mindustry.world.meta.Stat
import plumy.world.AddBar
import plumy.world.config
import steam.utils.addTable
import kotlin.math.min

/* todo
*  add liquid support
*  add temp support
*  add stats
*  better canCraft()
*  add booster support
*/

class MultiCrafter(name: String) : TemperatureBlock(name) {
    var recipes = ArrayList<Recipe>()
    lateinit var recipeList: RecipeList
    var warmupSpeed = 0.025f
    var craftEffect = Fx.smeltsmoke
    var craftTime = 100f

    class Recipe(
        val craftTime: Float,
        val inItem: Array<ItemStack> = emptyArray(),
        val outItem: Array<ItemStack> = emptyArray(),
        val inLiquid: LiquidStack? = null,
        val outLiquid: Array<LiquidStack> = emptyArray(),
        val booster: Consume? = null,
    ) {
        val allInItems = inItem.map { it.item }
        val allOutItems = outItem.map { it.item }
        val allOutLiquids = outLiquid.map { it.liquid }
        val allItems = (allInItems + allOutItems).distinct()
        val mainOut: UnlockableContent by lazy {
            (outItem.getOrNull(0)?.item ?: outLiquid.getOrNull(0)?.liquid) as UnlockableContent
        }
        val icon: TextureRegion by lazy { mainOut.uiIcon }
    }

    class RecipeList(
        recipes: List<Recipe>,
    ) {
        constructor(vararg recipes: Recipe) : this(recipes.toList())

        val allInItems = recipes.flatMap { it.allInItems }
        val allOutItems = recipes.flatMap { it.allOutItems }
        val allInLiquids = recipes.map { it.inLiquid }
        val allOutLiquids = recipes.flatMap { it.allOutLiquids }
        val allItems = (allInItems + allOutItems).distinct()
        val mainOut = recipes.map { it.mainOut }
    }

    init {
        solid = true
        update = true
        hasItems = true
        hasLiquids = true
        configurable = true
        saveConfig = true
        buildType = Prov { MultiCrafterBuild() }
        config<MultiCrafterBuild, Int> {
            val new = it
            if (curRecipeIdx != new) {
                curRecipeIdx = if (new < 0) -1 else new.coerceIn(0, recipes.size - 1)
                progress = 0f
                warmup = 0f
            }
        }
    }

    override fun init() {
        super.init()
        recipeList = RecipeList(recipes)
    }

    inner class MultiCrafterBuild : TemperatureBuild() {
        var baseProgress = 0f
        var progress = 0f
        var totalProgress = 0f
        var warmup = 0f
        var curRecipeIdx = -1
        val currentRecipe: Recipe
            get() = recipes[curRecipeIdx]
        val enabledRecipe: Boolean
            get() = curRecipeIdx >= 0

        override fun config() = curRecipeIdx
        fun recipeIdx(): Int {
            return recipes.indexOfFirst { items.has(it.inItem) }
        }

        override fun updateEfficiencyMultiplier() {
            efficiency *= if (curRecipeIdx >= 0) currentRecipe.booster?.efficiencyMultiplier(this) ?: 1f else 1f
            consumers.forEach { efficiency *= it.efficiencyMultiplier(this) }
        }

        override fun updateTile() {
            super.updateTile()
            if (!configurable) curRecipeIdx = recipeIdx()
            if (enabledRecipe && efficiency > 0f) {
                if (canCraft()) {
                    if (progress >= 1f) {
                        craft()
                    } else progress += getProgressIncrease(currentRecipe.craftTime) * warmup
                    if (baseProgress >= 1f) {
                        baseCraft()
                    } else baseProgress += getProgressIncrease(craftTime) * warmup

                    totalProgress += edelta()
                    warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed)
                    //continuously output based on efficiency
                    if (currentRecipe.outLiquid.isNotEmpty()) {
                        val inc = getProgressIncrease(1f)
                        for (output in currentRecipe.outLiquid) {
                            handleLiquid(
                                this,
                                output.liquid,
                                min(output.amount * inc, liquidCapacity - liquids[output.liquid])
                            )
                        }
                    }
                }
                dumpOutputs()
            } else warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return this.items.get(item) < this.getMaximumAccepted(item) && (recipeList.allInItems.contains(item) || block.consumesItem(item))
        }

        fun dumpOutputs() {
            if (!configurable) for (output in recipeList.allOutItems) dump(output)
            else for (output in currentRecipe.outItem) dump(output.item)

            if (!configurable) for (output in recipeList.allOutLiquids) dumpLiquid(output)
            else for (output in currentRecipe.outLiquid) dumpLiquid(output.liquid)
        }

        fun craft() {
            currentRecipe.booster?.trigger(this)
            items.remove(currentRecipe.inItem)

            for (output in currentRecipe.outItem) {
                for (i in 0 until output.amount) {
                    offload(output.item)
                }
            }

            if (wasVisible) craftEffect.at(this)

            progress %= 1f
        }

        fun baseCraft() {
            baseProgress %= 1f
            consume()
        }

        fun canCraft(): Boolean {
            return items.has(currentRecipe.inItem)
        }

        override fun progress() = progress
        override fun warmup() = warmup
        override fun totalProgress() = totalProgress
        override fun buildConfiguration(table: Table) {
            val group = ButtonGroup<ImageButton>()
            group.setMinCheckCount(0)
            val cont = Table()
            cont.defaults().size(40f)

            for ((i, recipe) in recipes.withIndex()) {
                val button = cont.button(Tex.whiteui, Styles.clearTogglei, 24f) {
                    deselect()
                }.group(group).tooltip(recipe.mainOut.localizedName).get()
                button.changed { if (i != curRecipeIdx) configure(i) else configure(-1) }
                button.style.imageUp = TextureRegionDrawable(recipe.mainOut.uiIcon)
                button.update { button.isChecked = enabledRecipe && currentRecipe.mainOut == recipe.mainOut }
            }
            val pane = ScrollPane(cont, Styles.smallPane)
            pane.setScrollingDisabled(true, false)

            pane.setScrollYForce(block.selectScroll)
            pane.update { block.selectScroll = pane.scrollY }

            table.add(pane).maxHeight(Scl.scl((40 * 5f)))
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(curRecipeIdx)
        }

        override fun read(read: Reads) {
            super.read(read)
            curRecipeIdx = read.i()
        }
    }

    override fun setBars() {
        super.setBars()
        AddBar<MultiCrafterBuild>("efficiency",
            { bundle.format("bar.efficiency", warmup * efficiency * 100f) },
            { Pal.lightOrange },
            { efficiency * warmup }
        )
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.output) { table ->
            table.row()
            recipes.forEach { r ->
                table.addTable {
                    background(Tex.whiteui)
                    setColor(Pal.darkestGray)
                    addTable {
                        r.inItem.forEach { add(ItemDisplay(it.item, it.amount, r.craftTime, false)).padRight(5f).padLeft(5f) }
                        image(Icon.right)
                        r.outItem.forEach { add(ItemDisplay(it.item, it.amount, r.craftTime, false)).padRight(5f).padLeft(5f) }
                        r.outLiquid.forEach { add(LiquidDisplay(it.liquid, it.amount * 60f, true)).padRight(5f).padLeft(5f) }
                    }.expandX().left().pad(10f)
                    add("${Strings.autoFixed(r.craftTime / 60f, 1)} ${bundle["unit.seconds"]}").color(Color.gray).right().padRight(10f)
                }.grow().padBottom(5f).row()
            }
        }
    }
}

fun MultiCrafter.addRecipe(
    craftTime: Float,
    inItem: Array<ItemStack> = emptyArray(),
    outItem: Array<ItemStack> = emptyArray(),
    inLiquid: LiquidStack? = null,
    outLiquid: Array<LiquidStack> = emptyArray(),
    booster: Consume? = null,
) {
    val recipe = MultiCrafter.Recipe(
        craftTime, inItem, outItem, inLiquid, outLiquid, booster
    )
    recipes.add(recipe)
}
