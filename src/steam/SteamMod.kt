package steam

import arc.Events
import mindustry.Vars
import mindustry.content.Items
import mindustry.game.EventType.FileTreeInitEvent
import mindustry.mod.Mod
import mindustry.mod.Mods.LoadedMod
import steam.content.ContentsLoader
import steam.content.SteamAttribute
import steam.gen.*
import steam.world.fluids.UpdatableFluid

class SteamMod : Mod() {
    companion object {
        lateinit var mod: LoadedMod
    }

    init {
        OreIconGenerator.apply {
            baseNumber = 3
            crushedOreNumber = 3
        }
        Events.on(FileTreeInitEvent::class.java) {
            OreIconGenerator.load()
            FluidCapsuleIconGenerator.load()
        }
        SteamAttribute.load() //attribute have to be loaded before content
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
        FluidCapsuleGenerator.apply {
            generateAll()
        }
        ContentsLoader.loadAfterOreGenerated()
        ContentsLoader.resisterEvents()
        ContentsLoader.iterateStats()
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}
