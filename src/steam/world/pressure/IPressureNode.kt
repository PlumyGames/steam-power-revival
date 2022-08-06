package steam.world.pressure

import arc.Core.bundle
import arc.struct.IntSeq
import arc.util.Tmp
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Buildingc
import mindustry.world.Block
import plumy.core.Serialized
import plumy.core.arc.hsvLerp
import plumy.core.math.Progress
import plumy.core.math.clamp
import plumy.world.AddBar
import steam.R
import steam.world.pressure.IPressureNode.Companion.pressureFact

typealias Pressure = Float

interface IPressureNode : Buildingc {
    var graph: PressureGraph
    var graphInitialized: Boolean
    @Serialized
    val currentPressure: Pressure
    @Serialized
    val links: IntSeq
    val pressureCapacity: Pressure
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
        val IPressureNode.pressureFact: Progress get() = if (maxPressure != 0f) (currentPressure / maxPressure).clamp else 0f
        val IPressureNode.maxPressure: Pressure get() = graph.maxPressure
    }
}

interface IPressureProducer : IPressureNode {
    val pressureProduced: Pressure
}

interface IPressureConsumer : IPressureNode {
    val pressureRequired: Pressure
}

inline fun <reified T> Block.addPressureBar() where T : Building, T : IPressureNode {
    AddBar<T>("pressure",
        {
            bundle.format("bar.pressure", currentPressure)
        }, {
            Tmp.c1.set(R.C.pressureSafe).hsvLerp(R.C.pressureWarning, pressureFact)
        }, {
            pressureFact
        }) {
    }
}

inline fun <reified T> Block.addPressureProducedBar(maxProduced: Pressure) where T : Building, T : IPressureProducer {
    AddBar<T>("pressure",
        {
            bundle.format("bar.pressure-procured", pressureProduced)
        }, {
            R.C.pressure
        }, {
            pressureProduced / maxProduced
        })
}

inline fun <reified T> Block.addPressureRequiredBar(maxRequirement: Pressure) where T : Building, T : IPressureConsumer {
    AddBar<T>("pressure",
        {
            bundle.format("bar.pressure-required", pressureRequired)
        }, {
            R.C.pressure
        }, {
            pressureRequired / maxRequirement
        })
}