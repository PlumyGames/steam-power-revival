package steam.world.pressure

import arc.struct.IntSeq
import mindustry.Vars
import mindustry.gen.Buildingc

interface IPressureNode : Buildingc {
    var graph: PressureGraph
    var graphInitialized: Boolean
    val currentPressure: Float
    val pressureCapacity: Float
    val links: IntSeq
    fun getNetworkConnections(out: MutableList<IPressureNode>):
            MutableList<IPressureNode> {
        out.clear()
        for (i in 0 until links.size) {
            val node = Vars.world.build(links[i]) as? IPressureNode ?: continue
            out.add(node)
        }
        return out
    }

    val linkedVertices: Iterable<IPressureNode>
        get() = getNetworkConnections(tempList)

    companion object {
        private val tempList = ArrayList<IPressureNode>()
    }
}

interface IPressureProducer : IPressureNode {
    val pressureProduced: Float
}

interface IPressureConsumer : IPressureNode {
    val pressureRequired: Float
}
