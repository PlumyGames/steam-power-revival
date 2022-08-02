package steam

import arc.Events
import arc.graphics.Texture
import arc.scene.ui.Image
import mindustry.Vars
import mindustry.content.Items
import mindustry.game.EventType.ClientLoadEvent
import mindustry.game.EventType.FileTreeInitEvent
import mindustry.mod.Mod
import mindustry.mod.Mods.LoadedMod
import mindustry.ui.dialogs.BaseDialog
import steam.content.ContentsLoader
import steam.gen.*
import java.io.File

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
            Events.on(ClientLoadEvent::class.java) {
                val rootDir = File("../../../..").canonicalFile
                val abs = rootDir.absolutePath
                val assets = rootDir.resolve("assets")
                val templates = assets.resolve("sprites/template")
                val `ore-base1` = templates.resolve("ore-base0.png")
                val `ore-patch1` = templates.resolve("ore-patch0.png")
                val maker = IconMaker(32, 32)
                val layers = listOf(
                    PixmapModelLayerForm(`ore-base1`) + PlainLayerProcessor(),
                    PixmapModelLayerForm(`ore-patch1`) + PlainLayerProcessor()
                )
                val baked = maker.bake(layers)
                BaseDialog("Test icon maker").apply {
                    val texture = Texture(baked.texture)
                    cont.add(Image(texture)).size(Vars.iconXLarge)
                    addCloseButton()
                }.show()
            }
        }
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
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}