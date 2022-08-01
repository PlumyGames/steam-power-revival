package steam.gen

import arc.graphics.Pixmap
import java.io.Closeable

interface IModelLayer : Closeable {
    fun addProcess(processor: ILayerProcessor)
    fun process(): Pixmap
    val texture: Pixmap
    override fun close() {
        texture.dispose()
    }
}

operator fun IModelLayer.plusAssign(processor: ILayerProcessor) {
    this.addProcess(processor)
}

operator fun IModelLayer.plus(processor: ILayerProcessor): IModelLayer {
    this.addProcess(processor)
    return this
}

interface ILayerProcessor {
    fun process(raw: Pixmap): Pixmap
}

interface IBakery {
    fun bake(layers: List<IModelLayer>): IBakedModel
}

interface IBakedModel {
    val texture: Pixmap
}