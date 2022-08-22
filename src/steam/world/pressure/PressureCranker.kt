package steam.world.pressure

import arc.Graphics.Cursor
import arc.Graphics.Cursor.SystemCursor
import arc.audio.Sound
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.gen.Sounds
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import plumy.dsl.*
import steam.DebugOnly
import steam.utils.drawTextEasy

class PressureCranker(name: String) : PressureBlock(name) {
    var generateAmount = 3f
    var generateTime = 90f
    var drawer: DrawBlock = DrawDefault()
    var crankSound: Sound = Sounds.click

    init {
        // configurable = true
        solid = true
        update = true
        warmupSpeed = 1f
        buildType = Prov { PressureCrankerBuild() }
    }

    override fun init() {
        configNull<PressureCrankerBuild> {
            lastCrank = generateTime
        }
        super.init()
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun icons(): Array<TextureRegion> {
        return drawer.finalIcons(this)
    }

    inner class PressureCrankerBuild : PressureBuild(), IPressureProducer {
        override var pressureProduced: Pressure = 0f
        var lastCrank = 0f
            set(value) {
                field = value.coerceAtLeast(0f)
            }
        var totalProgress = 0f
        override fun updateTile() {
            super.updateTile()
            lastCrank -= Time.delta
            if (lastCrank > 0) {
                pressureProduced = generateAmount
                totalProgress++
            } else pressureProduced = 0f
        }

        override fun shouldActiveSound(): Boolean {
            return lastCrank > 0f
        }

        override fun tapped() {
            configure(null)
            crankSound.at(this)
        }

        override fun getCursor(): Cursor =
            if (interactable(Vars.player.team())) SystemCursor.hand else SystemCursor.arrow

        override fun totalProgress() = totalProgress

        override fun draw() {
            drawer.draw(this)
            DebugOnly {
                drawTextEasy("${((lastCrank / generateTime) * 100f).toInt()}%", x, y + 6f)
            }
        }

        override fun drawSelect() {
            super.drawSelect()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(lastCrank)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            lastCrank = read.f()
        }
    }
}