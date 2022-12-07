package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import mindustry.world.blocks.production.GenericCrafter

class AreaCrafter(name: String) : GenericCrafter(name) {
    var area = 3
    var updateDelay = 180f

    init {
        buildType = Prov { AreaCrafterBuild() }
    }
    inner class AreaCrafterBuild : GenericCrafterBuild() {
        var updateTimer = 0f
        var lastEfficiency = 0f

        override fun updateTile() {
            updateTimer += delta()
            if (updateTimer >= updateDelay) {
                updateTimer %= updateDelay
                val efficiencyPerBlock = 1f / Mathf.pow(area * 2 + 1, 2)
                lastEfficiency = 0f
                for (x in -area..area) {
                    for (y in -area..area) {
                        val target = tile.nearby(x, y)
                        if(target != null && !target.solid()) {
                            lastEfficiency += efficiencyPerBlock
                        }
                    }
                }
            }
            super.updateTile()
        }

        override fun efficiencyScale(): Float {
            return lastEfficiency
        }
    }
}