package steam.world.distribution

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Point2
import arc.struct.IntSeq
import arc.util.Tmp
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.gen.Building
import mindustry.world.Tile
import plumy.world.unpack
import steam.utils.sheet
import steam.world.pressure.PressureBlock
import kotlin.math.abs

open class PressureBridge(name: String) : PressureBlock(name) {
    var range = 4f
    var maxConnection = 2
    //client side, for connecting
    var lastBuild: PressureBridgeBuild? = null
    lateinit var regions: Array<TextureRegion>
    lateinit var bridgeRegion: TextureRegion

    init {
        configurable = true
        buildType = Prov{ PressureBridgeBuild() }
    }

    override fun load() {
        super.load()
        regions = "$name-tile".sheet(32 * size, 32 * size)
        bridgeRegion = Core.atlas.find("$name-bridge")
    }
    override fun init() {
        fun connect(tile: PressureBridgeBuild, i: Point2) {
            val pos = Point2.pack(i.x + tile.tileX(), i.y + tile.tileY())
            if(!tile.linked.contains(pos)) {
                tile.linked.add(pos)
                tile.link(world.build(i.x, i.y))
            }
            else {
                tile.linked.removeValue(pos)
                tile.unlink(world.build(i.x, i.y))
            }
        }
        config(Point2::class.java) { tile: PressureBridgeBuild, i ->
            connect(tile, i)
        }
        config(Array<Point2>::class.java) { tile: PressureBridgeBuild, i ->
            i.forEach{ connect(tile, it) }
        }
        configClear { b: PressureBridgeBuild -> b.linked.clear() }
    }

    inner class PressureBridgeBuild : PressureBuild() {
        val linked = IntSeq()
        var drawIndex = 0

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if(other == this) {
                configure(null)
                deselect()
                return true
            } else if (linkValid(tile, other.tile)){
                configure(other.pos().unpack())
                other.configure(pos().unpack())
            }
            return false
        }

        override fun configure(value: Any?) {
            super.configure(value)
            updateRegion()
        }
        fun updateRegion() {
            drawIndex = 0
            for (i in 0 until linked.size) {
                val p = config()[i]!!.pack()
                val r = relativeTo(world.tile(p))
                drawIndex += 1 shl ((4 - r.toInt()) % 4)
            }
        }
        override fun config(): Array<Point2?> {
            val out = arrayOfNulls<Point2>(linked.size)
            for (i in out.indices) {
                out[i] = Point2.unpack(linked[i]).sub(tile.x.toInt(), tile.y.toInt())
            }
            return out
        }

        override fun draw() {
            Draw.rect(regions[drawIndex], x, y)
            Lines.stroke(8f)

            config().forEach{
                val t = world.tile(it!!.pack())

                Tmp.v1.set(x, y).sub(t.worldx(), t.worldy()).setLength(tilesize / 2f).inv()

                Lines.line(
                    bridgeRegion,
                    x + Tmp.v1.x,
                    y + Tmp.v1.y,
                    t.worldx() - Tmp.v1.x,
                    t.worldy() - Tmp.v1.y, false
                )
            }

            Draw.reset()
        }
        override fun onRemoved() {
            config().forEachIndexed { i, j ->
                val t = world.build(j!!.pack()) as PressureBridgeBuild
                t.unlink(this)
                t.linked.removeValue(linked[i])
            }
        }
    }

    fun linkValid(t1: Tile?, t2: Tile?): Boolean {
        if(t1 == null || t2 == null || !posValid(t1.x, t1.y, t2.x, t2.y)) return false
        return t2.block() is PressureBridge && t1.team() == t2.team()
            && (t1.build as PressureBridgeBuild).linked.size < maxConnection
            && (t2.build as PressureBridgeBuild).linked.size < maxConnection
    }
    fun posValid(x1: Short, y1: Short, x2: Short, y2: Short): Boolean {
        return if (x1 == x2)
            abs(y1 - y2) <= range
        else if (y1 == y2)
            abs(x1 - x2) <= range
        else false
    }
}