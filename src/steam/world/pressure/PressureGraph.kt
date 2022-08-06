package steam.world.pressure

import arc.struct.IntSet
import arc.struct.Seq
import mindustry.gen.Building
import plumy.core.math.isZero
import steam.utils.sumOf
import steam.world.pressure.IPressureNode.Companion.linkedVertices
import steam.world.pressure.IPressureNode.Companion.linkedVertices2
import java.util.*

class PressureGraph {
    val entity = PressureGraphUpdater.create().apply {
        graph = this@PressureGraph
    }
    var id = lastNetworkID++
        private set
    val all = Seq<IPressureNode>(false, 16, IPressureNode::class.java)
    val producers = Seq<IPressureProducer>(false, 16, IPressureNode::class.java)
    val consumers = Seq<IPressureConsumer>(false, 16, IPressureNode::class.java)
    val size: Int
        get() = all.size
    var currentPressure = 0f
    var maxPressure = 2f
    fun update() {
        currentPressure = if (consumers.isEmpty) {
            0f
        } else {
            // accumulate the pressure produced from all producers
            val produced = producers.sumOf(IPressureProducer::pressureProduced)
            // accumulate the pressure required from all consumers
            val required = consumers.sumOf(IPressureConsumer::pressureRequired)
            if (produced.isZero || required.isZero)
                0f
            else
                produced / required
        }
    }
    /**
     * Initialize a node, used in [Building.create]
     */
    fun initNode(node: IPressureNode) {
        add(node)
    }
    /**
     * Add a node into this pressure graph and initialize it.
     */
    private fun add(node: IPressureNode) {
        if (node.graph != this || !node.graphInitialized) {
            node.graph = this
            node.graphInitialized = true
            all.add(node)
            entity.add()

            when (node) {
                is IPressureProducer -> producers.add(node)
                is IPressureConsumer -> consumers.add(node)
            }
        }
    }

    private fun clear() {
        all.clear()
        producers.clear()
        consumers.clear()
        entity.remove()
    }
    /**
     * Merge a node into this network
     */
    private fun merge(node: IPressureNode) {
        if (node.graph == this) return
        node.graph.entity.remove()
        // iterate its link
        entity.add()
        bfsQueue.clear()
        bfsQueue.addLast(node)
        closedSet.clear()
        while (bfsQueue.size > 0) {
            val child = bfsQueue.removeFirst()
            add(child)
            for (next in child.linkedVertices) {
                if (closedSet.add(next.pos())) {
                    bfsQueue.addLast(next)
                }
            }
        }
    }

    fun reflow(node: IPressureNode) {
        bfsQueue.clear()
        bfsQueue.addLast(node)
        closedSet.clear()
        while (bfsQueue.size > 0) {
            val child = bfsQueue.removeFirst()
            add(child)
            for (next in child.linkedVertices) {
                if (closedSet.add(next.pos())) {
                    bfsQueue.addLast(next)
                }
            }
        }
    }

    fun unlink(from: IPressureNode) {
        for (link in from.linkedVertices) {
            if (link.graph != this) continue
            val newGraph = PressureGraph()
            newGraph.add(link)
            bfsQueue.clear()
            bfsQueue.addLast(link)
            while (bfsQueue.size > 0) {
                val child = bfsQueue.removeFirst()
                newGraph.add(child)
                for (next in child.linkedVertices2) {
                    if (next != from && next.graph != newGraph) {
                        newGraph.add(next)
                        bfsQueue.addLast(next)
                    }
                }
            }
        }
        entity.remove()
    }

    override fun toString() =
        "PressureGraph#$id"

    companion object {
        private val bfsQueue = LinkedList<IPressureNode>()
        private val closedSet = IntSet()
        private var lastNetworkID = 0
        fun mergeToLagerNetwork(a: IPressureNode, b: IPressureNode) {
            if (a.graph.size >= b.graph.size) {
                a.graph.merge(b)
            } else {
                b.graph.merge(a)
            }
        }
    }
}