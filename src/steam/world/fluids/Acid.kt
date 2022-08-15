package steam.world.fluids

import arc.graphics.Color
import mindustry.gen.Building
import mindustry.world.Block

class Acid : UpdatableFluid {
    constructor(name: String, color: Color) : super(name, color)
    constructor(name: String) : super(name)

    var damage = 0.2f
    val acidproof = HashSet<Block>()
    override fun Building.update(amount: Float) {
        if (this !is IAcidProof && block !in acidproof)
            damageContinuousPierce(damage)
    }
}

interface IAcidProof