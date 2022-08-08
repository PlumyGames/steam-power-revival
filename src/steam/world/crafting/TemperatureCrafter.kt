package steam.world.crafting

import arc.Core
import arc.Core.bundle
import arc.func.Prov
import arc.math.Mathf
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.LiquidStack
import mindustry.ui.LiquidDisplay
import mindustry.world.consumers.ConsumeLiquid
import mindustry.world.meta.Stat
import steam.utils.addTable
import steam.world.module.celsius

class TemperatureCrafter(name: String) : TemperatureBlock(name) {
    //amount of temp lose per craft
    val craftTemp = 0.2f.celsius
    var warmupSpeed = 0.1f
    //in tick
    var craftTime = 1f
    val pressureCapacity: Float
        get() = liquidCapacity
    var maxTemp = 400f.celsius
    lateinit var outputFluid: LiquidStack

    init {
        hasLiquids = true
        outputsLiquid = true
        buildType = Prov { TempCrafterBuild() }
    }

    inner class TempCrafterBuild : TemperatureBuild() {
        val pressureAmount: Float
            get() = liquids[outputFluid.liquid]
        val pressureProportion: Float
            get() = pressureAmount / pressureCapacity
        var warmup = 0f
        override fun updateTile() {
            super.updateTile()
            if (efficiency > 0 && temp >= minRequired) {
                val amount = craftTime * edelta()
                if (amount > 0.001f) {
                    consume()
                    handleLiquid(this, outputFluid.liquid, amount * outputFluid.amount)
                    temp -= amount * craftTemp
                    warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed)
                } else warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed)
            }
            dumpLiquid(outputFluid.liquid)
        }

        override fun shouldConsume(): Boolean {
            return pressureProportion <= 1f && enabled && temp >= minRequired
        }

        override fun efficiency(): Float {
            return Mathf.clamp(temp - 25f, 0f, maxTemp) / minRequired
        }

        override fun warmup() = warmup
    }

    override fun setBars() {
        super.setBars()
        addLiquidBar(outputFluid.liquid)
    }

    override fun setStats() {
        super.setStats()
        stats.remove(Stat.input)
        stats.add(Stat.output) { stat ->
            stat.addTable {
                background(Tex.whiteui)
                setColor(Pal.darkestGray)

                addTable {
                    addTable {
                        for (i in consumers) {
                            if (i is ConsumeLiquid) {
                                add(LiquidDisplay(i.liquid, i.amount * 60f, true))
                            }
                        }
                        image(Icon.right).padLeft(10f).padRight(10f)
                        add(LiquidDisplay(outputFluid.liquid, outputFluid.amount * 60f, true))
                    }.row()
                    addTable {
                        image(Core.atlas.find("status-burning")).padRight(5f)
                        addTable {
                            add(bundle.format("stat.minTemp", minRequired))
                        }
                    }
                }.grow().pad(1f)
            }
        }
    }
}