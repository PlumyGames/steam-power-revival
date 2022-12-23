package steam.world.recipe

import mindustry.ctype.UnlockableContent
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.Block

class CrafterRecipe(
    val craftTime: Float = 60f,
    val consumer: Array<RecipeConsume> = emptyArray(),
    val outItem: Array<ItemStack> = emptyArray(),
    val outLiquid: Array<LiquidStack> = emptyArray(),
    val required: Boolean = true //whether this recipe is required for the process
) {
    val allOutItems = outItem.map { it.item }
    val allOutLiquids = outLiquid.map { it.liquid }
    val mainOut: UnlockableContent by lazy {
        (outItem.getOrNull(0)?.item ?: outLiquid.getOrNull(0)?.liquid) as UnlockableContent
    }
    fun initialize(block: Block) {
        consumer.forEach { it.initialize(block) }
    }

    fun haveOutput(): Boolean {
        return allOutItems.isNotEmpty() && allOutLiquids.isNotEmpty()
    }
}