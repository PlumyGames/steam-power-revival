package steam.utils

import mindustry.ctype.UnlockableContent
import mindustry.type.*
import mindustry.world.Block

inline operator fun <T : Block> T.invoke(config: T.() -> Unit): T = apply(config)

operator fun Item.plus(amount: Int) =
    ItemStack(this, amount)

operator fun Liquid.plus(amount: Float) =
    LiquidStack(this, amount)

operator fun UnlockableContent.plus(amount: Int) =
    PayloadStack(this, amount)
