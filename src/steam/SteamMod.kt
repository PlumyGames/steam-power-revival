package steam

import arc.Events
import arc.graphics.Texture
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.Image
import arc.scene.utils.Elem
import mindustry.Vars
import mindustry.game.EventType.ClientLoadEvent
import mindustry.mod.Mod
import mindustry.mod.Mods.LoadedMod
import mindustry.ui.dialogs.BaseDialog
import steam.content.ContentsLoader
import steam.gen.IconMaker
import steam.gen.PixmapModelLayerForm
import steam.gen.PlainLayerProcessor
import steam.gen.plus
import java.io.File

class SteamMod : Mod() {
    companion object {
        lateinit var mod: LoadedMod
    }

    override fun init() {
        DebugOnly {
            Events.on(ClientLoadEvent::class.java) {
                val rootDir = File("../../../..").canonicalFile
                val abs = rootDir.absolutePath
                val assets = rootDir.resolve("assets")
                val templates = assets.resolve("sprites/template")
                val `ore-base1` = templates.resolve("ore-base1.png")
                val `ore-patch1` = templates.resolve("ore-patch1.png")
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
        DebugOnly {
            ContentsLoader.unlockForDebug()
        }
    }
}