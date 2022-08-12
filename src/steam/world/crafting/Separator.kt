package steam.world.crafting

import arc.func.Prov
import arc.math.Mathf
import arc.util.Structs
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.consumers.Consume
import mindustry.world.consumers.ConsumeItems
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValues
import plumy.core.assets.TRs

/**
 * Extracts a random list of items from an input item and an input liquid.
 */
class Separator(name: String) : Block(name) {
    var consItems: ConsumeItems? = null
    var results: Array<ItemStack> = ItemStack.empty
    var craftTime = 0f
    var drawer: DrawBlock = DrawDefault()
    var craftFx: Effect = Fx.none

    init {
        update = true
        solid = true
        hasItems = true
        hasLiquids = true
        sync = true
        buildType = Prov { SeparatorBuild() }
    }

    override fun load() {
        super.load()
        drawer.load(this)
    }

    override fun setStats() {
        stats.timePeriod = craftTime
        super.setStats()
        stats.add(Stat.output, StatValues.items { item: Item ->
            Structs.contains(
                results
            ) { i: ItemStack -> i.item === item }
        })
        stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds)
    }

    override fun icons(): TRs = drawer.finalIcons(this)
    override fun init() {
        super.init()
        consItems = findConsumer { c: Consume? -> c is ConsumeItems }
    }

    inner class SeparatorBuild : Building() {
        var progress = 0f
        var totalProgress = 0f
        var warmup = 0f
        var seed = 0
        override fun created() {
            seed = Mathf.randomSeed(tile.pos().toLong(), 0, Int.MAX_VALUE - 1)
        }

        override fun progress() = progress
        override fun shouldAmbientSound(): Boolean {
            return efficiency > 0
        }

        override fun shouldConsume(): Boolean {
            var total = items.total()
            //very inefficient way of allowing separators to ignore input buffer storage
            if (consItems != null) {
                for (stack in consItems!!.items) {
                    total -= items[stack.item]
                }
            }
            return total < itemCapacity && enabled
        }

        override fun draw() {
            drawer.draw(this)
        }

        override fun updateTile() {
            totalProgress += warmup * delta()
            if (efficiency > 0) {
                progress += getProgressIncrease(craftTime)
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f)
            } else {
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f)
            }
            if (progress >= 1f) {
                progress %= 1f
                var sum = 0
                for (stack in results) sum += stack.amount
                val i = Mathf.randomSeed(seed++.toLong(), 0, sum - 1)
                var count = 0
                var item: Item? = null
                //guaranteed desync since items are random - won't be fixed and probably isn't too important
                for (stack in results) {
                    if (i >= count && i < count + stack.amount) {
                        item = stack.item
                        break
                    }
                    count += stack.amount
                }
                consume()
                if (item != null && items[item] < itemCapacity) {
                    offload(item)
                }
                craftFx.at(this)
            }
            if (timer(timerDump, dumpTime.toFloat())) {
                dump()
            }
        }

        override fun sense(sensor: LAccess): Double {
            return if (sensor == LAccess.progress) progress.toDouble() else super.sense(sensor)
        }

        override fun canDump(to: Building, item: Item): Boolean {
            return !consumesItem(item)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(progress)
            write.f(warmup)
            write.i(seed)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            progress = read.f()
            warmup = read.f()
            seed = read.i()
        }
    }
}
