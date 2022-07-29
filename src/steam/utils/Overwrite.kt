package steam.utils

import mindustry.Vars
import steam.SteamMod

inline fun FakeVanilla(fake: () -> Unit) {
    Vars.content.setCurrentMod(null)
    fake()
    Vars.content.setCurrentMod(SteamMod.mod)
}