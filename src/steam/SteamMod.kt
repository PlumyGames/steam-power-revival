package steam

import arc.Events
import mindustry.Vars
import mindustry.content.Items
import mindustry.game.EventType.FileTreeInitEvent
import mindustry.mod.Mod
import mindustry.mod.Mods.LoadedMod
import steam.content.ContentsLoader
import steam.content.SteamItems
import steam.gen.DebugDialog
import steam.gen.OreGenerator
import steam.gen.OreIconGenerator

class SteamMod : Mod() {
    companion object {
        lateinit var mod: LoadedMod
    }

    init {
        OreIconGenerator.apply {
            baseNumber = 1
            patchNumber = 3
        }
        Events.on(FileTreeInitEvent::class.java) {
            OreIconGenerator.load()
        }
    }

    override fun init() {
        DebugOnly {
            DebugDialog.show()
        }
    }

    override fun loadContent() {
        mod = Vars.mods.getMod(Meta.name)
        ContentsLoader.load()
        OreGenerator.apply {
            // lol
            /*
            blacklist += Items.coal
            blacklist += Items.scrap
            blacklist += Items.sand
            */
            // lol
            extra += SteamItems.stone
            extra += SteamItems.quartz
            extra += Items.serpuloItems
            extra += Items.erekirItems
            generateAll()
            replaceAll()
        }
        ContentsLoader.loadAfterOreGenerated()
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}
