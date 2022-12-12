package steam.world.crafting

import arc.Core
import arc.func.Prov
import arc.util.Strings
import mindustry.Vars.tilesize
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.blocks.production.GenericCrafter

class AreaCrafter(name: String) : GenericCrafter(name) {
    var area = 7
    var updateDelay = 180f

    init {
        buildType = Prov { AreaCrafterBuild() }
    }

    override fun setBars() {
        super.setBars()
        addBar("efficiency") { entity: AreaCrafter.AreaCrafterBuild ->
            Bar(
                { Core.bundle.format("bar.efficiency",
                    Strings.autoFixed(entity.efficiency * 100f, 1)) },
                { Pal.accent },
                { entity.efficiency }
            )
        }
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        Drawf.dashSquare(
            if(valid) Pal.accent else Pal.remove,
            x * tilesize.toFloat(),
            y * tilesize.toFloat(),
            area * tilesize.toFloat()
        )
    }
    inner class AreaCrafterBuild : GenericCrafterBuild() {
        var updateTimer = updateDelay
        var lastEfficiency = 0f

        override fun updateTile() {
            updateTimer += delta()
            if (updateTimer >= updateDelay) {
                updateTimer %= updateDelay
                val efficiencyPerBlock = 1f / (area * area)
                lastEfficiency = 0f
                val halfArea = area / 2
                for (x in -halfArea..halfArea) {
                    for (y in -halfArea..halfArea) {
                        val target = tile.nearby(x, y)
                        if(target != null && (!target.solid()) || target.build == tile.build) {
                            lastEfficiency += efficiencyPerBlock
                        }
                    }
                }
            }
            super.updateTile()
        }

        override fun drawSelect() {
            super.drawSelect()
            Drawf.dashSquare(
                Pal.accent, x, y, area * tilesize.toFloat()
            )
        }

        override fun efficiencyScale(): Float {
            return lastEfficiency
        }
    }
}