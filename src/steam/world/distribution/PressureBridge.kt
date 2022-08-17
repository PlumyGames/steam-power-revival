package steam.world.distribution

import arc.Core
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.util.Tmp
import mindustry.Vars.tilesize
import mindustry.Vars.world
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.world.Tile
import plumy.core.assets.EmptyTR
import plumy.core.assets.EmptyTRs
import plumy.world.PackedPos
import plumy.world.castBuild
import plumy.world.config
import plumy.world.configNull
import steam.DebugOnly
import steam.utils.drawTextEasy
import steam.utils.sheet
import steam.world.pressure.PressureBlock
import steam.world.pressure.tryLink
import steam.world.pressure.tryUnlink
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

typealias Side = Int

/**
 * Get the reflected side index in [Geometry.d4]
 */
val Side.reflect: Side
    get() = (this + 2) % 4

open class PressureBridge(name: String) : PressureBlock(name) {
    var range = 4f
    var maxConnection = 4
        set(value) {
            field = value.coerceIn(1, 4)
        }
    //client side, for connecting
    var lastBuild: PressureBridgeBuild? = null
    var regions: Array<TextureRegion> = EmptyTRs
    var bridgeRegion1: TextureRegion = EmptyTR
    var bridgeRegion2: TextureRegion = EmptyTR
    var underRegion: TextureRegion = EmptyTR

    init {
        configurable = true
        copyConfig = false
        buildType = Prov { PressureBridgeBuild() }
    }

    override fun load() {
        super.load()
        regions = "$name-tile".sheet(size * 256, size * 64)
        bridgeRegion1 = Core.atlas.find("$name-bridge1")
        bridgeRegion2 = Core.atlas.find("$name-bridge2")
        underRegion = Core.atlas.find("$name-under")
    }

    override fun init() {
        config<PressureBridgeBuild, Int> {
            toggleConnectionFromRemote(it)
        }
        config<PressureBridgeBuild, Point2> {
            toggleConnectionFromRemote(it)
        }
        config<PressureBridgeBuild, Array<Point2>> {
            toggleConnectionFromRemote(it)
        }
        configNull<PressureBridgeBuild> {
            emptyLinkFromRemote()
        }
        super.init()
    }

    inner class PressureBridgeBuild : PressureBuild() {
        val bridgeLinks = IntArray(4) { -1 }
        val activeLinks: Int
            get() = bridgeLinks.count { it != -1 }
        var drawIndex = 0
        var lastTileChange = -2
        override fun updateTile() {
            if (lastTileChange != world.tileChanges) {
                lastTileChange = world.tileChanges
                updateRegion()
            }
        }

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (other == this) {
                configure(null)
                deselect()
                return true
            } else if (linkValid(tile, other.tile)) {
                configure(other.pos())
                other.configure(pos())
            }
            return false
        }

        fun toggleConnectionFromRemote(point: Point2) {
            val pos = Point2.pack(point.x + tileX(), point.y + tileY())
            val other = pos.castBuild<PressureBridgeBuild>() ?: return
            val dir = relativeTo(other).toInt().let {
                if (it == -1) return
                else it.coerceIn(0, 4)
            }
            if (bridgeLinks[dir] == -1) {
                if (tryLink(other)) {
                    bridgeLinks[dir] = pos
                }
            } else {
                if (tryUnlink(other)) {
                    bridgeLinks[dir] = -1
                }
            }
            updateRegion()
        }

        fun toggleConnectionFromRemote(pos: PackedPos) {
            val other = pos.castBuild<PressureBridgeBuild>() ?: return
            val dir = relativeTo(other).toInt().let {
                if (it == -1) return
                else it.coerceIn(0, 4)
            }
            if (bridgeLinks[dir] == -1) {
                if (tryLink(other)) {
                    bridgeLinks[dir] = pos
                }
            } else {
                if (tryUnlink(other)) {
                    bridgeLinks[dir] = -1
                }
            }
            updateRegion()
        }

        fun toggleConnectionFromRemote(points: Array<Point2>) {
            for ((dir, point) in points.withIndex()) {
                val pos = Point2.pack(point.x + tileX(), point.y + tileY())
                val other = pos.castBuild<PressureBridgeBuild>() ?: return
                if (bridgeLinks[dir] == -1) {
                    if (tryLink(other)) {
                        bridgeLinks[dir] = pos
                    }
                } else {
                    if (tryUnlink(other)) {
                        bridgeLinks[dir] = -1
                    }
                }
            }
            updateRegion()
        }

        fun emptyLinkFromRemote() {
            bridgeLinks.copyOf().forEachIndexed { side, pos ->
                val bridge = pos.castBuild<PressureBridgeBuild>() ?: return@forEachIndexed
                bridge.bridgeLinks[side.reflect] = -1
                this.bridgeLinks[side] = -1
                bridge.updateRegion()
            }
            unlink(this)
            updateRegion()
        }

        fun updateRegion() {
            drawIndex = 0
            forEachLink { it: Int ->
                val r = relativeTo(world.tile(it))
                drawIndex += 1 shl ((4 - r.toInt()) % 4)
            }
        }

        override fun config() = Array(4) {
            val link = bridgeLinks[it]
            if (link != -1) Point2.unpack(link).sub(tile.x.toInt(), tile.y.toInt())
            else Point2(Int.MAX_VALUE, Int.MAX_VALUE)
        }
        @JvmName("forEachLinkPoint")
        inline fun forEachLink(func: (Point2) -> Unit) {
            bridgeLinks.forEach {
                if (it != -1) func(Point2.unpack(it).sub(tile.x.toInt(), tile.y.toInt()))
            }
        }
        @JvmName("forEachLinkInt")
        inline fun forEachLink(func: (Int) -> Unit) {
            bridgeLinks.forEach {
                if (it != -1) func(it)
            }
        }

        inline fun forEachLinkIndexed(func: (Side, Int) -> Unit) {
            bridgeLinks.forEachIndexed { index, pos ->
                if (pos != -1) func(index, pos)
            }
        }

        override fun draw() {
            Draw.z(Layer.blockOver)
            Draw.rect(regions[drawIndex], x, y)

            Lines.stroke(8f)

            forEachLinkIndexed { side, pos ->
                val t = pos.castBuild<Building>() ?: return@forEachLinkIndexed
                val other = Tmp.v1.set(x, y).sub(t.x, t.y).setLength(tilesize / 2f).inv()
                val bridgeTR = if (side % 2 == 0) bridgeRegion2 else bridgeRegion1
                val x1 = max(x + other.x, t.x - other.x)
                val x2 = min(x + other.x, t.x - other.x)
                val y1 = max(y + other.y, t.y - other.y)
                val y2 = min(y + other.y, t.y - other.y)
                Lines.line(bridgeTR, x1, y1, x2, y2, false)
                DebugOnly {
                    Lines.stroke(2f)
                    if (bridgeTR == bridgeRegion1)
                        Draw.color(Color.red)
                    else if (bridgeTR == bridgeRegion2)
                        Draw.color(Color.yellow)
                    val dir = Geometry.d4[side]
                    Fill.circle(x + dir.x * 2f, y + dir.y * 2f, 1f)
                    Draw.color()
                    Lines.stroke(8f)
                }
            }
            DebugOnly {
                Draw.z(Layer.endPixeled)
                drawTextEasy("${graph.id}", x, y + 5f)
            }
            Draw.z(Layer.blockUnder + 0.01f)
            Draw.rect(underRegion, x, y)
            Draw.reset()
        }

        override fun onRemoved() {
            bridgeLinks.forEachIndexed { side, pos ->
                val bridge = pos.castBuild<PressureBridgeBuild>() ?: return@forEachIndexed
                bridge.unlink(this)
                bridge.bridgeLinks[side.reflect] = -1
                this.bridgeLinks[side] = -1
            }
            removeFromGraph()
        }
    }

    fun linkValid(t1: Tile?, t2: Tile?): Boolean {
        if (t1 == null || t2 == null || !posValid(t1.x, t1.y, t2.x, t2.y)) return false
        return t2.block() is PressureBridge && t1.team() == t2.team()
                && (t1.build as PressureBridgeBuild).activeLinks < maxConnection
                && (t2.build as PressureBridgeBuild).activeLinks < maxConnection
    }

    fun posValid(x1: Short, y1: Short, x2: Short, y2: Short): Boolean {
        return if (x1 == x2)
            abs(y1 - y2) <= range
        else if (y1 == y2)
            abs(x1 - x2) <= range
        else false
    }
}
