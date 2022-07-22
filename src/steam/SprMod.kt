package steam

import mindustry.mod.Mod
import steam.contents.Contents

class SprMod : Mod() {
    init {
    }

    override fun init() {
    }

    override fun loadContent() {
        Contents.load()
    }
}