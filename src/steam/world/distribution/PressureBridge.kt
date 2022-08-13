package steam.world.distribution

import arc.func.Prov
import arc.math.geom.Point2
import arc.struct.IntSeq
import arc.util.Log
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.world.Tile
import plumy.core.arc.forEach
import plumy.world.unpack
import steam.world.pressure.PressureBlock
import kotlin.math.abs

open class PressureBridge(name: String) : PressureBlock(name) {
    var range = 4f
    var maxConnection = 3
    //client side, for connecting
    var lastBuild: PressureBridgeBuild? = null

    init {
        configurable = true
        saveConfig = true
        buildType = Prov{ PressureBridgeBuild() }
    }
    override fun init() {
        fun connect(tile: PressureBridgeBuild, i: Point2) { if(!tile.linked.addUnique(Point2.pack(i.x + tile.tileX(), i.y + tile.tileY()))) tile.linked.removeValue(i.pack()) }
        config(Point2::class.java) { tile: PressureBridgeBuild, i ->
            connect(tile, i)
        }
        config(Array<Point2>::class.java) { tile: PressureBridgeBuild, i ->
            i.forEach{ connect(tile, it) }
        }
        configClear { b: PressureBridgeBuild ->
            b.linked.clear()
        }
    }

    inner class PressureBridgeBuild : PressureBuild() {
        val linked = IntSeq()

        override fun updateTile() {
            super.updateTile()
            Log.info(linked)
        }
        override fun onConfigureBuildTapped(other: Building): Boolean {
            if(other == this) {
                configure(null)
                deselect()
                return true
            } else if (linkValid(tile, other.tile)){
                configure(other.pos().unpack())
                other.configure(this)
            }
            return false
        }

        override fun drawSelect() {
            super.drawSelect()
            linked.forEach {
                val b = world.build(it) ?: return
                Drawf.select(b.x, b.y, b.block.size * tilesize.toFloat(), Pal.accent)
            }
        }

        override fun config(): Array<Point2?> {
            val out = arrayOfNulls<Point2>(linked.size)
            for (i in out.indices) {
                out[i] = Point2.unpack(linked[i]).sub(tile.x.toInt(), tile.y.toInt())
            }
            return out
        }
    }

    fun linkValid(t1: Tile?, t2: Tile?): Boolean {
        if(t1 == null || t2 == null || !posValid(t1.x, t1.y, t2.x, t2.y)) return false
        return t2.block() is PressureBridge && t1.team() == t2.team()
    }
    fun posValid(x1: Short, y1: Short, x2: Short, y2: Short): Boolean {
        return if (x1 == x2)
            abs(y1 - y2) <= range
        else if (y1 == y2)
            abs(x1 - x2) <= range
        else false
    }
}