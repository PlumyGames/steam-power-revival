package steam.world.mech

import arc.Events
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.util.Time
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.UnitTypes
import mindustry.game.EventType
import mindustry.gen.BlockUnitc
import mindustry.gen.Building
import mindustry.gen.Call
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.blocks.ControlBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault

class MechPad(name: String) : Block(name) {
    var constructTime = 240f
    var mech = UnitTypes.alpha
    var drawer: DrawBlock = DrawDefault()
    var spawnFx = Fx.spawn

    init {
        update = true
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun init() {
        super.init()

        Events.on(EventType.TapEvent::class.java) {
            val build = it.tile.build
            if (build is MechPadBuild) Call.unitControl(it.player, build.unit())
        }
    }

    inner class MechPadBuild : Building(), ControlBlock {
        var unit: BlockUnitc? = null
        var progress = 0f
        var warmup = 0f

        override fun unit(): Unit {
            if (unit == null) {
                unit = UnitTypes.block.create(team) as BlockUnitc
                unit!!.tile(this)
            }
            return unit as Unit
        }

        override fun drawSelect() {
            Draw.color(Pal.accent)
            for (i in 0..3) {
                val length = Vars.tilesize * size / 2f + 3 + Mathf.absin(Time.time, 5f, 2f)
                Draw.rect(
                    "transfer-arrow",
                    tile.drawx() + Geometry.d4[i].x * length,
                    tile.drawy() + Geometry.d4[i].y * length,
                    (i + 2) * 90f
                )
            }
            Draw.color()
        }

        override fun updateTile() {
            super.updateTile()
            if(isControlled && efficiency >= 0.01f) {
                if (progress >= 1f) {
                    progress = 0f
                    warmup = 0f
                    val u = mech.create(team)
                    u.spawnedByCore(true)
                    u.set(this)
                    u.rotation(90f)
                    spawnFx.at(this)

                    Events.fire(EventType.UnitCreateEvent(u, this, null))
                    if (!Vars.net.client()) u.add()

                    Call.unitControl(unit!!.player, u)
                } else {
                    progress += getProgressIncrease(constructTime)
                    warmup = Mathf.lerp(warmup, 1f, 0.1f)
                }
            } else {
                progress = 0f
                warmup = Mathf.lerp(warmup, 0f, 0.1f)
            }
        }

        override fun progress() = progress
        override fun warmup() = warmup
        override fun shouldConsume() = enabled && isControlled

        override fun draw() {
            super.draw()
            Draw.draw(Layer.blockOver) {
                Drawf.construct(this, mech, 0f, progress(), warmup(), totalProgress())
            }
        }
    }
}