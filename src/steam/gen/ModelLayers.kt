package steam.gen

import arc.files.Fi
import arc.graphics.Pixmap
import java.io.File

fun PixmapModelLayerForm(fi: Fi) = PixmapModelLayer(fi.toPixmap())
fun PixmapModelLayerForm(file: File) = PixmapModelLayer(file.toPixmap())
class PixmapModelLayer(override val texture: Pixmap) : IModelLayer {
    val processors = ArrayList<ILayerProcessor>()
    override fun addProcess(processor: ILayerProcessor) {
        processors.add(processor)
    }

    override fun process(): Pixmap {
        var cur = texture
        for (processor in processors) {
            val res = processor.process(cur)
            cur = res
        }
        return cur
    }
}