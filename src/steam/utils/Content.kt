package steam.utils

import mindustry.world.Block
import mindustry.world.meta.BuildVisibility

fun Block.hide() { buildVisibility = BuildVisibility.hidden }