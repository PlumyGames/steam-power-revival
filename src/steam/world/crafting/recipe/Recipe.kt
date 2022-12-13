package steam.world.crafting.recipe

import mindustry.ctype.UnlockableContent
import mindustry.type.ItemStack
import mindustry.type.LiquidStack

class Recipe(
    val craftTime: Float = 60f,
    val inItem: Array<ItemStack> = emptyArray(),
    val outItem: Array<ItemStack> = emptyArray(),
    val inLiquid: Array<LiquidStack> = emptyArray(),
    val outLiquid: Array<LiquidStack> = emptyArray(),
    val required: Boolean = true //whether this recipe is required for the process
) {
    val allInItems = inItem.map { it.item }
    val allOutItems = outItem.map { it.item }
    val allInLiquids = inLiquid.map { it.liquid }
    val allOutLiquids = outLiquid.map { it.liquid }
    val allItems = (allInItems + allOutItems).distinct()
    val mainOut: UnlockableContent by lazy {
        (outItem.getOrNull(0)?.item ?: outLiquid.getOrNull(0)?.liquid) as UnlockableContent
    }
}