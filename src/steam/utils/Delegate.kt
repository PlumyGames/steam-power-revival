package steam.utils

import mindustry.world.Block

fun <T : Block> T.delegateI18nFrom(other: Block): T {
    this.localizedName = other.localizedName
    this.description = other.description
    this.details = other.details
    return this
}