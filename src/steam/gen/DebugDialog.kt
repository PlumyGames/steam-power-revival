package steam.gen

import arc.scene.ui.Label
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
            val alpha = Label("${OreIconGenerator.alpha}")
            cont.add(alpha).row()
            fun reload() {
                OreGenerator.all.values.forEach {
                    it.loadIcon()
                }
                rebuild()
            }
            cont.slider(0f, 1f, 0.0001f, OreIconGenerator.alpha) {
                OreIconGenerator.alpha = it
                alpha.setText("$it")
                reload()
            }.width(1000f)
            addCloseButton()

            buttons.button("Reload") {
                reload()
            }
        }.show()
    }
}