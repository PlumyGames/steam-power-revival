package steam.gen

import arc.files.Fi
import arc.graphics.Pixmap
import java.io.File
import java.io.InputStream

fun File.toPixmap() = Pixmap(Fi(this))
fun Fi.toPixmap() = Pixmap(this)
fun InputStream.toPixmap() = Pixmap(this.readBytes())