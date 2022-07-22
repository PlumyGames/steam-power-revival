package steam.content

object ContentsLoader {
    fun load() {
        SteamBlocks.apply {
            boiler()
            burner()
        }
    }
}