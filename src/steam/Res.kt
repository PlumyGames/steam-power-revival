package steam

import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset

class ResNotFoundException(msg: String) : RuntimeException(msg)
/**
 * Represents a location of resource.
 * Provides convenient methods of read.
 */
class Res(
    var locator: Class<*> = Res::class.java,
    var pos: String = "",
    var inJar: Boolean = true,
) {
    constructor(pos: String) :
            this(Res::class.java, pos = pos)

    fun readAsStream(): InputStream =
        load(locator, truePos)

    fun tryReadAsStream(): InputStream? =
        loadNullable(locator, truePos)

    val truePos: String
        get() =
            if (inJar && !(pos.startsWith('/'))) "/$pos"
            else pos

    infix fun sub(subPos: String): Res =
        Res(locator, "$pos/$subPos", inJar)

    fun enter(subPos: String): Res =
        this.apply {
            pos = "$pos/$subPos"
        }

    operator fun plusAssign(subPos: String) {
        enter(subPos)
    }

    operator fun plus(subPos: String): Res =
        this.sub(subPos)
    /**
     * Create a reader from this resource location
     *
     * **Note**:  It is the caller's responsibility to close this reader.
     *
     * @return the reader
     */
    fun reader(charset: Charset = Charsets.UTF_8): Reader =
        readAsStream().reader(charset)
    /**
     * Use this resource location as a reader. It will automatically close the reader.
     */
    inline fun <R> useAsReader(
        use: (Reader) -> R,
        charset: Charset = Charsets.UTF_8,
    ): R = reader(charset).use {
        use(it)
    }
    /**
     * Read all text from this resource location with UTF-8 charset.
     */
    fun readAllText(): String =
        reader().use { it.readText() }

    companion object {
        /**
         * @exception ResNotFoundException raises when this file doesn't exist.
         */
        fun load(locator: Class<*> = Res::class.java, name: String): InputStream =
            locator.getResourceAsStream(name)
                ?: throw ResNotFoundException("Can't find file $name with ${locator.name}")

        fun loadNullable(locator: Class<*> = Res::class.java, name: String): InputStream? =
            locator.getResourceAsStream(name)
    }
}

