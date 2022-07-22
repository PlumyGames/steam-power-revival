package steam

import mindustry.mod.Mod

class SteamMod : Mod() {

    init {
    }

    override fun loadContent() {
        Contents.load()
    }
}