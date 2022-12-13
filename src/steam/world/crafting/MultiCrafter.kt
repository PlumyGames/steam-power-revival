package steam.world.crafting

import arc.Core.bundle
import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ButtonGroup
import arc.scene.ui.ImageButton
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.util.Strings.autoFixed
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.content.Fx
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.ItemDisplay
import mindustry.ui.Styles
import mindustry.world.meta.Stat
import plumy.dsl.AddBar
import plumy.dsl.config
import steam.utils.addTable
import steam.world.recipe.CrafterRecipe
import steam.world.recipe.Process
import steam.world.recipe.ProcessList
import kotlin.math.min

/* todo
*  add better liquid support
*  add temp support
*  add power support
*  better canCraft()
*  add booster support
* rewrite the whole thing
*/

class MultiCrafter(name: String) : TemperatureBlock(name) {
    var processes = ArrayList<Process>()
    lateinit var processList: ProcessList
    var warmupSpeed = 0.025f
    var craftEffect = Fx.none
    var groupSize = 0

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
            it.initialize(this)
        }
        processList = ProcessList(processes, groupSize)
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

        fun dumpOutputs() {
            if (!configurable) for (output in processList.allOutItems) dump(output)
            else currentProcess.recipes.forEach { it.outItem.forEach { i -> dump(i.item) } }

            if (!configurable) for (output in processList.allOutLiquids) dumpLiquid(output)
            else currentProcess.recipes.forEach { it.outLiquid.forEach { i -> dumpLiquid(i.liquid) } }
        }

        fun craft(r: CrafterRecipe, idx: Int) {
            r.consumer.forEach {
                it.trigger(this)
            }

            for (output in r.outItem) {
                for (i in 0 until output.amount) {
                    offload(output.item)
                }
            }

            if (wasVisible) craftEffect.at(this)

            progress[idx] %= 1f
        }

        fun craftFluid(recipe: CrafterRecipe) {
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

        fun canCraftRecipe(recipe: CrafterRecipe): Boolean {
            return recipe.consumer.all { it.valid(this) }
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
    @Suppress("LABEL_NAME_CLASH")
    override fun setStats() {
        super.setStats()
        stats.add(Stat.output) { stat ->
            stat.row()
            processList.indexedProcess.forEachIndexed { i, p ->
                p.forEach { process ->
                    stat.addTable {
                        background(Tex.whiteui)
                        setColor(Pal.darkestGray)
                        addTable {
                            if (process.name.isNotEmpty()) {
                                add(process.name).left().row()
                                image().growX().pad(7f).padLeft(5f).padRight(0f).height(4f).color(Color.darkGray).row()
                            }
                            addTable {
                                if (process.allConsumer.isNotEmpty()) {
                                    addTable {
                                        process.recipes.forEach { recipe ->
                                            addTable {
                                                background(Tex.whiteui)
                                                setColor(Pal.darkerGray)
                                                addTable {
                                                    addTable {
                                                        recipe.consumer.forEach {
                                                            it.displayTable(this@addTable, recipe)
                                                        }
                                                        image(Icon.right).padLeft(10f).padRight(10f)
                                                        recipe.outItem.forEach {
                                                            add(ItemDisplay(it.item, it.amount, recipe.craftTime, false).left())
                                                        }
                                                    }.padBottom(7f).row()
                                                    add("${bundle["stat.productiontime"]}: ${autoFixed(recipe.craftTime / 60f, 1)} ${bundle["unit.seconds"]}")
                                                        .color(Color.lightGray).growX().left().row()
                                                    add("${bundle["stat.optional"]}: ${if(!recipe.required) bundle["yes"] else bundle["no"]}")
                                                        .color(Color.lightGray).growX().left().row()
                                                }.growX().pad(10f)
                                            }.growX().padBottom(7f).row()
                                        }
                                    }.grow().left().row()
                                }
                            }.grow().row()
                        }.grow().pad(10f)
                    }.growX().padBottom(10f).row()
                }
            }
        }
    }
}