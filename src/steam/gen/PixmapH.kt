package steam.gen

import arc.files.Fi
import arc.graphics.Pixmap
import java.io.File

fun File.toPixmap() = Pixmap(Fi(this))
fun Fi.toPixmap() = Pixmap(this)
