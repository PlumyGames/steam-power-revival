package steam.world.crafting

import arc.Core.bundle
import arc.func.Prov
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ButtonGroup
import arc.scene.ui.ImageButton
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.content.Fx
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.Styles
import mindustry.world.meta.Stat
import plumy.dsl.AddBar
import plumy.dsl.config
import kotlin.math.min

/* todo
*  add liquid support
*  add temp support
*  add stats
*  better canCraft()
*  add booster support
* multi-processing
* rewrite the whole thing
*/

class MultiCrafter(name: String) : TemperatureBlock(name) {
    var processes = ArrayList<Process>()
    lateinit var recipeList: RecipeList
    var warmupSpeed = 0.025f
    var craftEffect = Fx.none

    open class Process(
        val recipes: ArrayList<Recipe> = arrayListOf(),
        val name: String = ""
    ) {
        lateinit var allInItems: List<Item>
        lateinit var allOutItems: List<Item>
        lateinit var allOutLiquids: List<Liquid>

        fun initialize() {
            allInItems = recipes.flatMap { it.allInItems }
            allOutItems = recipes.flatMap { it.allOutItems }
            allOutLiquids = recipes.flatMap { it.allOutLiquids }
        }
    }
    //recipe of a Process
    class Recipe(
        val craftTime: Float = 60f,
        val inItem: Array<ItemStack> = emptyArray(),
        val outItem: Array<ItemStack> = emptyArray(),
        val outLiquid: Array<LiquidStack> = emptyArray(),
        val required: Boolean = true //whether this recipe is required for the process
    ) {
        val allInItems = inItem.map { it.item }
        val allOutItems = outItem.map { it.item }
        val allOutLiquids = outLiquid.map { it.liquid }
        val allItems = (allInItems + allOutItems).distinct()
        val mainOut: UnlockableContent by lazy {
            (outItem.getOrNull(0)?.item ?: outLiquid.getOrNull(0)?.liquid) as UnlockableContent
        }
    }

    class RecipeList(
        process: List<Process>,
    ) {
        constructor(vararg recipes: Process) : this(recipes.toList())

        val allInItems = process.flatMap { it.allInItems }
        val allOutItems = process.flatMap { it.allOutItems }
        val allOutLiquids = process.flatMap { it.allOutLiquids }
        val allItems = (allInItems + allOutItems).distinct()
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
            if (curProcessIdx != new) {
                curProcessIdx = if (new < 0) -1 else new.coerceIn(0, processes.size - 1)
                progress = Array(processes.size) { 0f }
                warmup = 0f
            }
        }
    }

    override fun init() {
        super.init()
        processes.forEach {
            it.initialize()
        }
        recipeList = RecipeList(processes)
    }

    inner class MultiCrafterBuild : TemperatureBuild() {
        var progress = Array(processes.size) { 0f }
        var totalProgress = 0f
        var warmup = 0f
        var curProcessIdx = -1
        val currentProcess: Process
            get() = processes[curProcessIdx]
        val enabledRecipe: Boolean
            get() = curProcessIdx >= 0

        override fun config() = curProcessIdx
        fun recipeIdx(): Int {
            return processes.indexOfFirst {
                it.recipes.all { recipe ->
                    if (recipe.required) canCraftRecipe(recipe) else true
                }
            }
        }

        override fun updateTile() {
            super.updateTile()
            if (!configurable) curProcessIdx = recipeIdx()
            if (enabledRecipe && efficiency > 0f) {
                if (canCraft()){
                    currentProcess.recipes.forEachIndexed { i, r ->
                        if (canCraftRecipe(r)){
                            warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed)
                            progress[i] += getProgressIncrease(r.craftTime) * warmup
                            craftFluid(r)
                            if (progress[i] >= 1f) {
                                craft(r, i)
                            }
                        }
                    }
                    totalProgress += edelta()
                } else warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
                dumpOutputs()
            }
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            return this.items.get(item) < this.getMaximumAccepted(item) && (recipeList.allInItems.contains(item) || block.consumesItem(item))
        }

        fun dumpOutputs() {
            if (!configurable) for (output in recipeList.allOutItems) dump(output)
            else currentProcess.recipes.forEach { it.outItem.forEach { i -> dump(i.item) } }

            if (!configurable) for (output in recipeList.allOutLiquids) dumpLiquid(output)
            else currentProcess.recipes.forEach { it.outLiquid.forEach { i -> dumpLiquid(i.liquid) } }
        }

        fun craft(r: Recipe, idx: Int) {
            items.remove(r.inItem)

            for (output in r.outItem) {
                for (i in 0 until output.amount) {
                    offload(output.item)
                }
            }

            if (wasVisible) craftEffect.at(this)

            progress[idx] %= 1f
        }

        fun craftFluid(recipe: Recipe) {
            //continuously output based on efficiency
            if (recipe.outLiquid.isNotEmpty()) {
                val inc = getProgressIncrease(1f)
                for (output in recipe.outLiquid) {
                    handleLiquid(
                        this,
                        output.liquid,
                        min(output.amount * inc, liquidCapacity - liquids[output.liquid])
                    )
                }
            }
        }

        fun canCraft(): Boolean {
            return currentProcess.recipes.all {
                if (it.required) { canCraftRecipe(it) } else true
            }
        }

        fun canCraftRecipe(recipe: Recipe): Boolean {
            return items.has(recipe.inItem)
        }

        override fun progress() = progress[0]
        override fun warmup() = warmup
        override fun totalProgress() = totalProgress
        override fun buildConfiguration(table: Table) {
            val group = ButtonGroup<ImageButton>()
            group.setMinCheckCount(0)
            val cont = Table()
            cont.defaults().size(40f)

            for ((i, recipe) in processes.withIndex()) {
                val button = cont.button(Tex.whiteui, Styles.clearTogglei, 24f) {
                    deselect()
                }.group(group).tooltip(recipe.recipes[0].mainOut.localizedName).get()
                button.changed { if (i != curProcessIdx) configure(i) else configure(-1) }
                button.style.imageUp = TextureRegionDrawable(recipe.recipes[0].mainOut.uiIcon)
                button.update { button.isChecked = enabledRecipe && currentProcess.recipes[0].mainOut == recipe.recipes[0].mainOut }
            }
            val pane = ScrollPane(cont, Styles.smallPane)
            pane.setScrollingDisabled(true, false)

            pane.setScrollYForce(block.selectScroll)
            pane.update { block.selectScroll = pane.scrollY }

            table.add(pane).maxHeight(Scl.scl((40 * 5f)))
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(curProcessIdx)
        }

        override fun read(read: Reads) {
            super.read(read)
            curProcessIdx = read.i()
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
            /*recipes.forEach { r ->
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
            }*/
        }
    }
}
fun MultiCrafter.Process.addRecipe(
    craftTime: Float,
    inItem: Array<ItemStack> = emptyArray(),
    outItem: Array<ItemStack> = emptyArray(),
    outLiquid: Array<LiquidStack> = emptyArray(),
) {
    recipes.add(MultiCrafter.Recipe(
        craftTime, inItem, outItem, outLiquid
    ))
}
