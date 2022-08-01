package steam.gen

import arc.graphics.Pixmap

interface IRawTexture {
    val texture: Pixmap
}

interface IProcessedTexture {
    val texture: Pixmap
}

interface IModelLayer {
    fun addProcess(process: ILayerProcess)
    val texture: IRawTexture
}

interface ILayerProcess {
    fun process(layer: IModelLayer): IProcessedTexture
}

interface IBakery {
    fun bake(layers: List<IModelLayer>): IBakedModel
}

interface IBakedModel {
    val texture: Pixmap
}