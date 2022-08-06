package steam.world.pressure

import arc.struct.IntSeq
import mindustry.Vars
import mindustry.gen.Buildingc

typealias Pressure = Float

interface IPressureNode : Buildingc {
    var graph: PressureGraph
    var graphInitialized: Boolean
    val currentPressure: Pressure
    val pressureCapacity: Pressure
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

    fun isConnectedToTwoWay(other: IPressureNode) =
        other.pos() in this.links && this.pos() in other.links

    fun connectToTwoWay(other: IPressureNode) {
        other.links.addUnique(this.pos())
        this.links.addUnique(other.pos())
    }

    fun updateProximateLink() {
        val proximity = proximity()
        for (build in proximity) {
            if (build is IPressureNode) {
                this.connectToTwoWay(build)
                PressureGraph.mergeToLagerNetwork(this, build)
            }
        }
    }

    fun removeFromGraph() {
        graph.unlink(this)
    }

    companion object {
        private val tempList = ArrayList<IPressureNode>()
        private val tempList2 = ArrayList<IPressureNode>()
        val IPressureNode.linkedVertices: Iterable<IPressureNode>
            get() = getNetworkConnections(tempList)
        val IPressureNode.linkedVertices2: Iterable<IPressureNode>
            get() = getNetworkConnections(tempList2)
    }
}

interface IPressureProducer : IPressureNode {
    val pressureProduced: Float
}

interface IPressureConsumer : IPressureNode {
    val pressureRequired: Float
}
