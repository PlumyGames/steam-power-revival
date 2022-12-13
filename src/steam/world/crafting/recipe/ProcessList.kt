package steam.world.crafting.recipe

class ProcessList(
    process: List<Process>,
    groupSize: Int
) {
    val allInItems = process.flatMap { it.allInItems }
    val allOutItems = process.flatMap { it.allOutItems }
    val allInLiquids = process.flatMap { it.allInLiquids }
    val allOutLiquids = process.flatMap { it.allOutLiquids }
    val allItems = (allInItems + allOutItems).distinct()
    val indexedProcess = List(groupSize) { i ->
        return@List process.filter { it.group == i + 1 }
    }
}