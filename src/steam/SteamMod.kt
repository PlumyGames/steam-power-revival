package steam

import mindustry.mod.Mod
import steam.content.ContentsLoader

class SteamMod : Mod() {
    override fun loadContent() {
        ContentsLoader.load()
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}