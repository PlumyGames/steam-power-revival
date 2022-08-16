package steam.world.pressure

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import steam.utils.drawTextEasy

class PressureCranker(name: String) : PressureBlock(name) {
    var generateAmount = 3f
    var generateTime = 90f
    var drawer: DrawBlock = DrawMulti(DrawDefault(), DrawRegion("-cranker").apply { rotateSpeed = 5f; spinSprite = true }, DrawRegion("-top"))

    init {
        configurable = true
        solid = true
        update = true
        warmupSpeed = 1f
        buildType = Prov { PressureCrankerBuild() }
    }

    override fun init() {
        configClear<PressureCrankerBuild> {
            it.lastCrank = generateTime
        }
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
        var totalProgress = 0f

        override fun updateTile() {
            super.updateTile()
            lastCrank -= Time.delta
             if (lastCrank > 0) {
                pressureProduced = generateAmount
                totalProgress++
            } else pressureProduced = 0f
        }

        override fun totalProgress() = totalProgress

        override fun buildConfiguration(table: Table) {
            //insert joke
            val butt = table.button(Icon.wrench) { configure(null) }.get()
            butt.setDisabled { lastCrank > 0f }
        }

        override fun drawSelect() {
            super.drawSelect()
            drawTextEasy("${(lastCrank / generateTime) * 100f}%", x, y, Pal.accent)
        }

        override fun draw() {
            drawer.draw(this)
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