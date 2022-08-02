package steam.gen

import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.TextureRegion
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

fun IBakery.bake(vararg layers: IModelLayer) = bake(layers.toList())
interface IBakedModel {
    val texture: Pixmap
}

fun IBakedModel.toTexture() = Texture(texture)
fun IBakedModel.toTextureRegion() = TextureRegion(toTexture())