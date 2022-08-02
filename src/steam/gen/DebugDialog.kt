package steam.gen

import arc.scene.ui.layout.Table
import mindustry.ui.dialogs.BaseDialog

object DebugDialog {
    fun show() {
        BaseDialog("Debug Icon Generating").apply {
            val icons = Table()
            fun rebuild() {
                icons.clear()
                OreGenerator.all.values.forEach {
                    icons.add(Table().apply {
                        image(it.uiIcon).size(100f).row()
                        add(it.localizedName)
                    }).size(120f).pad(10f)
                }
            }
            rebuild()
            cont.add(icons).grow().row()
            addCloseButton()
            buttons.button("Reload") {
                OreGenerator.all.values.forEach {
                    it.loadIcon()
                }
                rebuild()
            }
        }.show()
    }
}