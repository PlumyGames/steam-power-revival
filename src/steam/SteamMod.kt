package steam

import mindustry.mod.Mod
import steam.content.ContentsLoader

class SteamMod : Mod() {

    init {
    }

    override fun loadContent() {
        ContentsLoader.load()
        DebugOnly {
            Contents.unlockForDebug()
        }
    }
}