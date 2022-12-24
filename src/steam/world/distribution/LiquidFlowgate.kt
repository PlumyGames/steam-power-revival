package steam.world.distribution

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.scene.ui.layout.Table
import mindustry.Vars
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.blocks.ItemSelection
import mindustry.world.blocks.liquid.LiquidRouter
import plumy.core.assets.EmptyTR
import plumy.dsl.config

class LiquidFlowgate(name: String) : LiquidRouter(name) {
    var filterRegion = EmptyTR
    var crossRegion = EmptyTR

    override fun load() {
        super.load()
        filterRegion = Core.atlas.find("$name-liquid")
        crossRegion = Core.atlas.find("$name-cross")
    }

    init {
        configurable = true
        saveConfig = true
        buildType = Prov { LiquidFlowgateBuild() }
        config<LiquidFlowgateBuild, Liquid> {
            this.filter = it
        }
        configClear<LiquidFlowgateBuild> {
            it.filter = null
        }
    }

    inner class LiquidFlowgateBuild : LiquidRouterBuild() {
        var filter: Liquid? = null

        override fun config(): Liquid? {
            return filter
        }

        override fun acceptLiquid(source: Building?, liquid: Liquid?): Boolean {
            return super.acceptLiquid(source, liquid) && liquid == filter
        }

        override fun buildConfiguration(table: Table) {
            ItemSelection.buildTable(this@LiquidFlowgate, table, Vars.content.liquids(),
                { filter },
                { value: Liquid? -> configure(value) }, selectionRows, selectionColumns
            )
        }

        override fun draw() {
            super.draw()
            Draw.color()
            if (filter == null) {
                Draw.rect(crossRegion, x, y)
            } else {
                Draw.color(filter!!.color)
                Draw.rect(filterRegion, x, y)
                Draw.color()
            }
        }
    }
}