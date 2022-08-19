package steam

import arc.Events
import mindustry.Vars
import mindustry.content.Items
import mindustry.game.EventType.FileTreeInitEvent
import mindustry.mod.Mod
import mindustry.mod.Mods.LoadedMod
import steam.content.ContentsLoader
import steam.gen.DebugDialog
import steam.gen.OreGenerator
import steam.gen.OreIconGenerator
import steam.world.fluids.UpdatableFluid

class SteamMod : Mod() {
    companion object {
        lateinit var mod: LoadedMod
    }

    init {
        OreIconGenerator.apply {
            baseNumber = 1
            patchNumber = 3
            powderNumber = 2
        }
        Events.on(FileTreeInitEvent::class.java) {
            OreIconGenerator.load()
        }
    }

    override fun init() {
        DebugOnly {
            DebugDialog.show()
        }
        UpdatableFluid.functionAsClass()
    }

    override fun loadContent() {
        mod = Vars.mods.getMod(Meta.name)
        ContentsLoader.load()
        OreGenerator.apply {
            blacklist += Items.coal
            blacklist += Items.scrap
            blacklist += Items.sand
            generateAll()
            replaceAll()
        }
        ContentsLoader.loadAfterOreGenerated()
        ContentsLoader.resisterEvents()
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}
