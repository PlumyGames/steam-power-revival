package steam.world.module

interface IPressureContainer {
    val steamAmount: Float
    /**
     * The proportion of steam in this container
     */
    val steamProportion: Float
}