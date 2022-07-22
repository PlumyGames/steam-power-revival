package steam.contents

import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawHeatOutput
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.BuildVisibility
import steam.blocks.ItemBurner
import steam.utils.invoke

object Blocks {
    lateinit var burner: ItemBurner
    fun burner() {
        burner = ItemBurner("burner")() {
            requirements(Category.crafting, BuildVisibility.shown, arrayOf())
            size = 1
            drawer = DrawMulti(DrawDefault(), DrawHeatOutput())
        }
    }
}
