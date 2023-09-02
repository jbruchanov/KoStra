
import com.jibru.kostra.DefaultQualifiersProvider
import com.jibru.kostra.IDefaultQualifiersProvider
import com.jibru.kostra.K
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.Resources
import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.ordinal
import com.jibru.kostra.plural
import com.jibru.kostra.string

fun main() {
    val test = {
        println("-".repeat(32))
        println("Current locale:${DefaultQualifiersProvider.get().locale}")
        println("Strings:")
        val items = listOf(K.string.action_add, K.string.action_remove, K.string.color, K.string.plurals, K.string.ordinals)
        println(items.joinToString { Resources.string(it) })
        println("Plurals:")
        println(
            listOf(
                Resources.plural(K.plural.bug_x, 0, 0f),
                Resources.plural(K.plural.bug_x, FixedDecimal(0.5), 0.5f),
                Resources.plural(K.plural.bug_x, 1, 1),
                Resources.plural(K.plural.bug_x, 10, 10)
            ).joinToString()
        )
        println("Ordinals:")
        println((0..5).joinToString { Resources.ordinal(K.plural.day_x, it, it) })

        /*val assetPath = Resources.assetPath(K.drawable.capital_city)
        println("$assetPath")

        val xxHdpiQualifiers = defaultQualifiers().copy(dpi = KDpi.XXHDPI)
        ImageIO.read(Resources.binaryInputStream(K.drawable.capital_city, qualifiers = xxHdpiQualifiers)).also {
            val assetPath = Resources.assetPath(K.drawable.capital_city, xxHdpiQualifiers)
            if (it != null) {
                println("$assetPath imageRes:${it.width}x${it.height}")
            } else {
                println("Unable to load $assetPath, (webp not supported in java!?)")
            }
        }
        Unit*/
    }

    DefaultQualifiersProvider.delegate = kLocale("enUS")
    test()
    DefaultQualifiersProvider.delegate = kLocale("enGB")
    test()
    DefaultQualifiersProvider.delegate = kLocale("cs")
    test()
}

private fun kLocale(locale: String) = object : IDefaultQualifiersProvider {
    override fun get(): KQualifiers = KQualifiers(locale = locale)
}
