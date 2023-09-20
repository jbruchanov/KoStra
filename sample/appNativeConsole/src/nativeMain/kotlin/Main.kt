@file:OptIn(ExperimentalForeignApi::class)

import com.jibru.kostra.DefaultQualifiersProvider
import com.jibru.kostra.IDefaultQualifiersProvider
import com.jibru.kostra.K
import com.jibru.kostra.KDpi
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.Resources
import com.jibru.kostra.assetPath
import com.jibru.kostra.icu.FixedDecimal
import com.jibru.kostra.ordinal
import com.jibru.kostra.plural
import com.jibru.kostra.string
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.posix.FILE
import platform.posix.SEEK_END
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fseek
import platform.posix.ftell

/*
    run configuration from jIDEA has by default app project run folder, but the kexe is in build/../ => fails with Unable to open DB exception
    to make working proper test: just build the project and run it manually
    ./gradlew appNativeConsole:build
    cd appNativeConsole/build/bin/native/releaseExecutable/
    ./appNativeConsole.kexe (exe or whatever appropriate extension)
 */
fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
    val test = {
        println("-".repeat(32))
        println("Current locale:${DefaultQualifiersProvider.get().locale}")
        println("Strings:")
        val items = listOf(K.string.action_add, K.string.action_remove, K.string.color, K.string.plurals, K.string.ordinals)
        println(items.joinToString { Resources.string(it) })
        println("Plurals:")
        println(
            listOf(
                Resources.plural(K.plural.bug_x, 0, 0),
                Resources.plural(K.plural.bug_x, FixedDecimal(0.5), 0.5f),
                Resources.plural(K.plural.bug_x, 1, 1),
                Resources.plural(K.plural.bug_x, 2, 2),
                Resources.plural(K.plural.bug_x, 3, 3),
                Resources.plural(K.plural.bug_x, 4, 4),
                Resources.plural(K.plural.bug_x, 5, 5),
                Resources.plural(K.plural.bug_x, 10, 10)
            ).joinToString()
        )
        println("Ordinals:")
        println((0..5).joinToString { Resources.ordinal(K.plural.day_x, it, it) })

        println("Images:")
        val assetPath = Resources.assetPath(K.drawable.capital_city)
        println("$assetPath, fileSize:${fileSize(assetPath)}")
    }

    val codes = listOf("ar", "cs", "en", "enGB", "enUS", "he", "hi", "ja", "ko", "ru", "th")
    codes.forEach {
        DefaultQualifiersProvider.delegate = kQualifiers(it, KDpi.XXHDPI)
        test()
    }
}

private fun fileSize(name: String): Int {
    val file: CPointer<FILE>? = fopen(name, "rb")
    if (file == null) {
        println("Unable to open '$name'")
        return -1
    }
    fseek(file, 0, SEEK_END)
    val size = ftell(file)
    fclose(file)
    return size.convert()
}

private fun kQualifiers(locale: String, dpi: KDpi) = object : IDefaultQualifiersProvider {
    override fun get(): KQualifiers = KQualifiers(locale = locale, dpi = dpi)
}
