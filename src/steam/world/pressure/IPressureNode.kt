package steam.world.pressure

import arc.Core.bundle
import arc.struct.IntSeq
import arc.util.Tmp
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Buildingc
import mindustry.ui.Bar
import mindustry.world.Block
import steam.R
import steam.world.pressure.IPressureNode.Companion.pressureFact

typealias Pressure = Float

interface IPressureNode : Buildingc {
    var graph: PressureGraph
    var graphInitialized: Boolean
    val currentPressure: Pressure
    val maxPressure: Pressure
        get() = graph.currentPressure
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
        val IPressureNode.linkedVertices get() = getNetworkConnections(tempList)
        val IPressureNode.linkedVertices2 get() = getNetworkConnections(tempList2)
        val IPressureNode.pressureFact get() = if (maxPressure != 0f) currentPressure / maxPressure else 0f
    }
}

interface IPressureProducer : IPressureNode {
    val pressureProduced: Float
}

interface IPressureConsumer : IPressureNode {
    val pressureRequired: Float
}

inline fun <reified T> Block.addPressureBar() where T : Building, T : IPressureNode {
    addBar<T>("pressure") {
        Bar({
            bundle.format("bar.pressure", it.currentPressure)
        }, {
            Tmp.c1.set(R.C.pressureSafe).lerp(R.C.pressureWarning, it.pressureFact)
        }, {
            it.pressureFact
        })
    }
}