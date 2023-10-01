@file:OptIn(ExperimentalForeignApi::class)

import app.native.N
import app.native.get
import app.native.getAssetPath
import app.native.getOrdinal
import app.native.pluralResource
import com.jibru.kostra.DefaultQualifiersProvider
import com.jibru.kostra.IDefaultQualifiersProvider
import com.jibru.kostra.KDpi
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.icu.FixedDecimal
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
fun main(
    @Suppress("UNUSED_PARAMETER") args: Array<String>,
) {
    val test = {
        println("-".repeat(32))
        println("Current locale:${DefaultQualifiersProvider.current.locale}")
        println("Strings:")
        val items = listOf(N.string.action_add, N.string.action_remove, N.string.color, N.string.plurals, N.string.ordinals)
        println(items.joinToString { it.get() })
        println("Plurals:")
        println(
            listOf(
                pluralResource(N.plural.bug_x, 0, 0),
                pluralResource(N.plural.bug_x, FixedDecimal(0.5), 0.5f),
                pluralResource(N.plural.bug_x, 1, 1),
                pluralResource(N.plural.bug_x, 2, 2),
                pluralResource(N.plural.bug_x, 3, 3),
                pluralResource(N.plural.bug_x, 4, 4),
                pluralResource(N.plural.bug_x, 5, 5),
                pluralResource(N.plural.bug_x, 10, 10),
            ).joinToString(),
        )
        println("Ordinals:")
        println((0..5).joinToString { N.plural.day_x.getOrdinal(it, it) })

        println("Images:")
        val assetPath = N.images.capital_city.getAssetPath()
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
    override val current: KQualifiers get() = KQualifiers(locale = locale, dpi = dpi)
}
