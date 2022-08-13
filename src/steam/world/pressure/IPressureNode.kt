package steam.world.pressure

import arc.Core.bundle
import arc.math.Mathf
import arc.struct.IntSeq
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Buildingc
import mindustry.graphics.Drawf
import mindustry.world.Block
import plumy.core.Serialized
import plumy.core.arc.hsvLerp
import plumy.core.math.Progress
import plumy.core.math.clamp
import plumy.core.math.isZero
import plumy.world.AddBar
import steam.DebugOnly
import steam.R
import steam.utils.format
import steam.world.pressure.IPressureNode.Companion.pressureFact

typealias Pressure = Float

interface IPressureNode : Buildingc {
    var graph: PressureGraph
    var graphInitialized: Boolean
    @Serialized
    var currentPressure: Pressure
    @Serialized
    val links: IntSeq
    var flash: Float
    val pressureCapacity: Pressure
    val pressureWarmupSpeed: Float
    val nodeRevision: Int get() = 0
    fun getNetworkConnections(out: MutableList<IPressureNode>):
            MutableList<IPressureNode> {
        out.clear()
        for (i in 0 until links.size) {
            val node = Vars.world.build(links[i]) as? IPressureNode ?: continue
            out.add(node)
        }
        return out
    }

    fun updatePressure() {
        val links = linkedVertices
        val proximateMaxPressure = if (links.isNotEmpty()) links.maxOf { it.currentPressure } else 0f
        val proximateMinPressure = if (links.isNotEmpty()) links.minOf { it.currentPressure } else 0f
        val graphPressure = graph.currentPressure
        if (this is IPressureProducer) {
            currentPressure = if (graphPressure > 0f) Mathf.approachDelta(currentPressure, graphPressure, pressureWarmupSpeed)
            else Mathf.approachDelta(currentPressure, 0f, pressureWarmupSpeed)
        } else {
            val targetPressure = if (graphPressure > 0f) {
                proximateMaxPressure
            } else {
                if (links.size > 1) proximateMinPressure else 0f
            }
            currentPressure = if (targetPressure > 0f) Mathf.approachDelta(currentPressure, targetPressure, pressureWarmupSpeed)
            else Mathf.approachDelta(currentPressure, 0f, pressureWarmupSpeed)
        }
    }

    fun pdelta() = currentPressure * Time.delta * timeScale()
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

    fun writeNode(writer: Writes) {
        writer.b(nodeRevision)
        writer.f(currentPressure)
        writer.s(links.size)
        for (i in 0 until links.size) {
            writer.i(links[i])
        }
    }

    fun readNode(reader: Reads) {
        val revision = reader.b().toInt()
        currentPressure = reader.f()
        links.clear()
        val size = reader.s()
        for (i in 0 until size) {
            links.add(reader.i())
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

fun IPressureNode.drawWholeGraphForDebug() {
    DebugOnly {
        graph.all.forEach {
            Drawf.square(it.x, it.y, it.block().size * Vars.tilesize / 2.5f, 0f)
        }
    }
}

interface IPressureProducer : IPressureNode {
    val pressureProduced: Pressure
    override fun updateProximateLink() {
        val proximity = proximity()
        for (build in proximity) {
            if (build is IPressureNode && build !is IPressureProducer) {
                this.connectToTwoWay(build)
                PressureGraph.mergeToLagerNetwork(this, build)
            }
        }
    }
}

interface IPressureConsumer : IPressureNode {
    var pressureRequired: Pressure
    override fun updateProximateLink() {
        val proximity = proximity()
        for (build in proximity) {
            if (build is IPressureNode && build !is IPressureConsumer) {
                this.connectToTwoWay(build)
                PressureGraph.mergeToLagerNetwork(this, build)
            }
        }
    }
}

inline fun <reified T> Block.addPressureBar() where T : Building, T : IPressureNode {
    AddBar<T>("pressure",
        { bundle.format("bar.pressure", Strings.autoFixed(currentPressure, 1)) },
        { Tmp.c1.set(R.C.pressureSafe).hsvLerp(R.C.pressureWarning, pressureFact) },
        { pressureFact })
}

inline fun <reified T> Block.addPressureProducedBar(maxProduced: Pressure) where T : Building, T : IPressureProducer {
    AddBar<T>("pressure-produced",
        { bundle.format("bar.pressure-procured", if (pressureProduced.isZero) "0" else pressureProduced.format(1)) },
        { R.C.pressure },
        { pressureProduced / maxProduced })
}

inline fun <reified T> Block.addPressureRequiredBar(maxRequirement: Pressure) where T : Building, T : IPressureConsumer {
    AddBar<T>("pressure-required",
        { bundle.format("bar.pressure-required", if (pressureRequired.isZero) "0" else pressureRequired.format(1)) },
        { R.C.pressure },
        { pressureRequired / maxRequirement })
}