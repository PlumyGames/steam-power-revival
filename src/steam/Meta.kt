package steam

object Meta {
    const val debugMod = true
    const val name = "steam"
    const val displayName = "SteamPowerRevival"
}

val String.steam:String
    get() = "${Meta.name}-$this"

inline fun DebugOnly(func: () -> Unit) {
    if (Meta.debugMod) {
        func()
    }
}

inline fun UndebugOnly(func: () -> Unit) {
    if (!Meta.debugMod) {
        func()
    }
}