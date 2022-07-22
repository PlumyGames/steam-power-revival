package steam

import mindustry.mod.*
import steam.content.SteamBlocks

class SteamPowerRevival : Mod(){

    init{

    }

    override fun loadContent(){
        SteamBlocks.load()
    }
}