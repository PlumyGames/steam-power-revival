package steam.utils

import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table

inline fun Table.addTable(config: Table.() -> Unit): Cell<Table> {
    val table = Table()
    table.config()
    return this.add(table)
}