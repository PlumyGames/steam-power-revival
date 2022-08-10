package steam.world.temp

import mindustry.gen.Buildingc

typealias Celsius = Float

val Float.celsius: Celsius
    get() = this
val Double.celsius: Celsius
    get() = this.toFloat()
const val Celsius100: Celsius = 100f

interface ITemperatureBlock : Buildingc {
    var temp: Celsius
    val tempCap: Celsius
    var flash:Float
    companion object {
        fun ITemperatureBlock.warmupImpl() = (temp / Celsius100).coerceIn(0f, 1f)
    }
}