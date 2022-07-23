package steam.world.module

interface ISteamContainer {
    val steamAmount: Float
    /**
     * The proportion of steam in this container
     */
    val steamProportion: Float
}