package steam.world.heating

import arc.Core.bundle
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Strings.autoFixed
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.Vars.content
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.ui.Bar
import mindustry.ui.ItemDisplay
import mindustry.world.Block
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Stat
import plumy.core.Serialized
import steam.utils.addTable
import kotlin.math.max

class ItemBurner(name: String) : Block(name) {
    companion object {
        var visualMaxOutput = -1f
    }

    var minFlammabilityReq = 0.3f
    var warmupRate = 0.15f
    var warmupSpeed = 0.019f
    var heatingTimeFactor = 70f // how long an item can output heat continuously
    var heatConvertFactor = 4f // heat from flammability
    var explosiveConvertingProportion = 0.4f //extra heat proportion from explosiveness
    // Explosion
    var explosivenessThreshold = 0.5f // if >= this, it will explode
    var explodeFactor = 20f //damage factor for explosion
    var explodeChance = 0.5f //explosion chance
    var explodeEffect: Effect = Fx.generatespark
    // Function
    lateinit var flammableFilter: BurnerItemConsume
    var drawer: DrawBlock = DrawDefault()

    init {
        update = true
        hasItems = true
        sync = true
        flags = EnumSet.of(BlockFlag.factory)
        rotateDraw = false
        rotate = true
        rotateDraw = false
        canOverdrive = false
        drawArrow = true
        solid = true
        buildType = Prov { BurnerBuild() }
    }
    fun toHeatingTime(flammability: Float) = heatingTimeFactor * flammability
    fun toHeat(flammability: Float) = flammability * heatConvertFactor
    fun toExplodeDamage(explosiveness: Float) = explosiveness * explodeFactor
    fun toFinalFlammability(item: Item) = item.flammability + if(item.explosiveness >= explosivenessThreshold) item.explosiveness * explosiveConvertingProportion else 0f
    fun toFinalHeat(item: Item) = toHeat(toFinalFlammability(item))
    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun init() {
        if (visualMaxOutput < 0f && heatConvertFactor >= 0f) {
            for (item in content.items()) {
                visualMaxOutput = max(visualMaxOutput, toHeat(item.flammability))
            }
        }
        flammableFilter = consume(BurnerItemConsume(minFlammabilityReq))
        super.init()
    }

    inner class BurnerBuild : Building(), HeatBlock {
        @Serialized
        var heat = 0f
        @Serialized
        var warmup = 0f
        @Serialized
        var heatingTime = 0f
        /**
         * It will also take [Item.explosiveness] into account.
         */
        @Serialized
        var curFlammability = 0f
        @Serialized
        var targetHeatingTime = 0f
        override fun updateEfficiencyMultiplier() {
            curFlammability = flammableFilter.efficiencyMultiplier(this)
            targetHeatingTime = toHeatingTime(curFlammability)
        }

        override fun updateTile() {
            if (efficiency > 0f) {
                heatingTime += delta()
                if (targetHeatingTime > 0f && heatingTime >= targetHeatingTime) {
                    // if the item is burnt out, try to consume next
                    consumeFuel()
                    heatingTime = 0f
                }
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed)
                val targetHeat = toHeat(curFlammability)
                heat = Mathf.approachDelta(heat, targetHeat * efficiency, warmupRate * delta())
            } else {
                heatingTime = 0f
                // cool down
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
                heat = Mathf.approachDelta(heat, 0f, warmupRate * delta())
            }
        }

        fun consumeFuel() {
            consume()
        }

        override fun heat() = heat
        override fun heatFrac() = heat / visualMaxOutput
        override fun warmup() = warmup
        override fun draw() {
            drawer.draw(this)
        }

        override fun drawLight() {
            super.drawLight()
            drawer.drawLight(this)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(heat)
            write.f(warmup)
            write.f(curFlammability)
            write.f(targetHeatingTime)
        }
        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            heat = read.f()
            warmup = read.f()
            curFlammability = read.f()
            targetHeatingTime = read.f()
        }
    }

    override fun setBars() {
        super.setBars()
        addBar<BurnerBuild>("heat") {
            Bar("bar.heat", Pal.lightOrange, it::heatFrac)
        }
    }

    override fun setStats() {
        super.setStats()
        stats.remove(Stat.input)
        stats.add(Stat.input) { stat ->
            stat.row()
            stat.addTable {
                content.items().each<Item>(flammableFilter.filter) {
                    add(ItemDisplay(it, 1, toHeatingTime(it.flammability), false)).padRight(15f).left()
                    add("[red]\ue83b[] ${autoFixed(toFinalHeat(it), 1)} ${bundle["unit.heatunits"]}").padRight(15f).left()
                    add("${autoFixed(toHeatingTime(it.flammability) / 60f, 1)} ${bundle["unit.seconds"]}").color(Color.gray).left().row()
                }
            }
        }
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        drawer.drawPlan(this, plan, list)
    }

    override fun getRegionsToOutline(out: Seq<TextureRegion>) {
        drawer.getRegionsToOutline(this, out)
    }

    override fun icons(): Array<TextureRegion> = drawer.finalIcons(this)
    inner class BurnerItemConsume : ConsumeItemFlammable {
        constructor(minFlammability: Float) : super(minFlammability)
        constructor() : super()

        override fun efficiencyMultiplier(build: Building?): Float {
            val item = getConsumed(build)
            return if (item == null) 0f
            else toFinalFlammability(item)
        }

        override fun trigger(build: Building) {
            val item = getConsumed(build)
            if (item != null) {
                if (item.explosiveness > explosivenessThreshold) {
                    if (Vars.state.rules.reactorExplosions &&
                        Mathf.chance((explodeChance * item.explosiveness).toDouble())
                    ) {
                        val damage = toExplodeDamage(item.explosiveness)
                        build.damage(damage)
                        explodeEffect.at(
                            build.x + Mathf.range(build.block.size * Vars.tilesize / 2f),
                            build.y + Mathf.range(build.block.size * Vars.tilesize / 2f)
                        )
                    }
                }
                build.items.remove(item, 1)
            }
        }
    }
}