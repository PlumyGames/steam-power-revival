package steam

import mindustry.Vars
import mindustry.mod.Mod
import mindustry.mod.Mods.LoadedMod
import steam.content.ContentsLoader

class SteamMod : Mod() {
    companion object {
        lateinit var mod: LoadedMod
    }

    override fun loadContent() {
        mod = Vars.mods.getMod(Meta.name)
        ContentsLoader.load()
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}