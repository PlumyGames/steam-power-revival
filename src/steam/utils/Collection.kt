package steam.utils

import arc.struct.Seq

inline fun <T> Seq<T>.sumOf(accumulator: T.() -> Float): Float {
    var sum = 0f
    for (i in 0 until size) {
        sum += accumulator(items[i])
    }
    return sum
}