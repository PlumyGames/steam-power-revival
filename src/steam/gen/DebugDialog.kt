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
                OreGenerator.all.values.forEachIndexed { i, it ->
                    icons.add(Table().apply {
                        image(it.uiIcon).size(100f).row()
                        add(it.localizedName).grow()
                    }).minSize(120f).pad(10f)
                    if (i != 0 && i % 5 == 4) {
                        icons.row()
                    }
                }
            }
            rebuild()
            cont.add(icons).grow().row()
            val alpha255 = Label("${(OreIconGenerator.alpha * 255).toInt()}")
            val alpha = Label("${OreIconGenerator.alpha}")
            cont.add(alpha255).row()
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
                alpha255.setText("${(it * 255).toInt()}")
                reload()
            }.width(1000f)
            addCloseButton()

            buttons.button("Reload") {
                reload()
            }
        }.show()
    }
}