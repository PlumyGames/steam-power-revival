package steam.world.production

import arc.func.Prov
import arc.struct.ObjectIntMap
import arc.util.Log
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env
import steam.world.environment.GroundFloor
import kotlin.math.min

class GroundPump(name: String) : Block(name) {
    var counterVicosity = .5f
    var pumpSpeed = .1f
    var drawer = DrawDefault()

    init {
        update = true
        solid = true
        hasLiquids = true
        group = BlockGroup.liquids
        outputsLiquid = true
        envEnabled = envEnabled or Env.space or Env.underwater
        buildType = Prov { GroundPumpBuild() }
    }

    protected val fluidCount = ObjectIntMap<Liquid>()

    override fun load() {
        super.load()
        drawer.load(this)
    }

    fun getDrop(tile: Tile): Liquid? {
        return tile.overlay().liquidDrop
    }

    fun getFluid(tile: Tile) {
        fluidCount.clear()
        tile.getLinkedTilesAs(this, tempTiles).forEach {
            if (canPump(it)) fluidCount.increment(getDrop(it))
        }
    }

    fun canPump(tile: Tile): Boolean {
        if (tile.overlay() !is GroundFloor || tile.block().isStatic) return false
        val drop = getDrop(tile)
        return drop != null && drop.viscosity <= counterVicosity
    }

    inner class GroundPumpBuild : Building() {
        var drillable = ObjectIntMap<Liquid>()

        override fun draw() {
            drawer.draw(this)
        }

        override fun updateTile() {
            super.updateTile()
            if (efficiency <= 0f) return
            drillable.forEach {
                val amt = liquids.get(it.key)
                if (amt >= liquidCapacity - 0.001f) return
                liquids.add(it.key,
                    min(liquidCapacity - amt, pumpSpeed * edelta() * it.value)
                )
            }
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            getFluid(tile)
            drillable = fluidCount
        }
    }
}