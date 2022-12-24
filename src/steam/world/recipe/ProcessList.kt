package steam.world.recipe

class ProcessList(
    process: List<Process>,
    groupSize: Int
) {
    val allOutItems = process.flatMap { it.allOutItems }
    val allOutLiquids = process.flatMap { it.allOutLiquids }
    val indexedProcess = List(groupSize + 1) { i ->
        return@List process.filter { it.group == i }
    }
}