package steam.utils

import arc.graphics.Color
import mindustry.entities.Effect

fun Color(hex: String): Color = Color.valueOf(hex)
fun NewEffect(
    duration: Float,
    clipSize: Float = 50f,
    render: Effect.EffectContainer.() -> Unit,
): Effect = Effect(duration, clipSize) {
    it.render()
}