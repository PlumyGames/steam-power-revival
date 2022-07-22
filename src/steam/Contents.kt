package steam

import steam.content.SteamBlocks

object Contents {
    fun load() {
        SteamBlocks.apply {
            boiler()
            burner()
        }
    }
}