package steam.world.fluids

import arc.Events
import arc.graphics.Color
import mindustry.Vars
import mindustry.game.EventType.Trigger
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.type.Liquid

open class UpdatableFluid : Liquid {
    constructor(name: String, color: Color) : super(name, color)
    constructor(name: String) : super(name)
    /**
     * If the liquid amount is more than this, it will trigger [update] and [draw]
     */
    var threshhold = 0f
    var update = true
    var draw = true

    init {
        all.add(this)
    }

    override fun init() {
        if (update) updatable.add(this)
        if (draw) drawable.add(this)
        super.init()
    }

    open fun Building.update(amount: Float) {
    }

    open fun Building.draw(amount: Float) {
    }

    companion object {
        val all = HashSet<UpdatableFluid>()
        private val updatable = HashSet<UpdatableFluid>()
        private val drawable = HashSet<UpdatableFluid>()
        fun functionAsClass() {
            Events.run(Trigger.update) {
                if (!Vars.state.isPlaying || updatable.isEmpty()) return@run
                Groups.build.forEach {
                    val liquids = it.liquids ?: return@forEach
                    for (fluid in updatable) {
                        val amount = liquids[fluid]
                        if (amount > fluid.threshhold) {
                            with(fluid) {
                                it.update(amount)
                            }
                        }
                    }
                }
            }
            Events.run(Trigger.draw) {
                if (!Vars.state.isPlaying || drawable.isEmpty()) return@run
                Groups.build.forEach {
                    val liquids = it.liquids ?: return@forEach
                    for (fluid in drawable) {
                        val amount = liquids[fluid]
                        if (amount > fluid.threshhold) {
                            with(fluid) {
                                it.draw(amount)
                            }
                        }
                    }
                }
            }
        }
    }
}
