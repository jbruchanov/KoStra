package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.KDpi
import com.jibru.kostra.KQualifiers
import java.io.File
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

class GroupQualifiersTest {
    @ParameterizedTest
    @ArgumentsSource(StrictGroupQualifiersArgsProvider::class)
    fun groupQualifiers(strict: Boolean?, path: String, qualifiers: GroupQualifiers) {
        if (strict == null) {
            assertThat(File(path).groupQualifiers(anyLocale = true)).isEqualTo(qualifiers)
            assertThat(File(path).groupQualifiers(anyLocale = false)).isEqualTo(qualifiers)
        } else {
            assertThat(File(path).groupQualifiers(anyLocale = !strict)).isEqualTo(qualifiers)
        }
    }

    @Test
    fun groupQualifiersInvalid() {
        assertThrows<IllegalArgumentException> { File("group-abcde").groupQualifiers(anyLocale = true) }
    }
}

private class StrictGroupQualifiersArgsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            /*strict, path, qualifiers*/
            //locale
            Arguments.of(null, "group", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(null, "GROUP", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(null, "grOUp", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(null, "group1-en", GroupQualifiers("group1", KQualifiers("en"))),
            Arguments.of(null, "group2-en-rGB", GroupQualifiers("group2", KQualifiers("enGB"))),
            Arguments.of(null, "group-en-rUS", GroupQualifiers("group", KQualifiers("enUS"))),
            //dpi
            Arguments.of(null, "tst", GroupQualifiers("tst", KQualifiers(dpi = KDpi.Undefined))),
            Arguments.of(null, "tst-nodpi", GroupQualifiers("tst", KQualifiers(dpi = KDpi.NoDpi))),
            Arguments.of(null, "tst-LDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.LDPI))),
            Arguments.of(null, "tst-MDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.MDPI))),
            Arguments.of(null, "tst-HDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.HDPI))),
            Arguments.of(null, "tst-XHDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.XHDPI))),
            Arguments.of(null, "tst-XXHDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.XXHDPI))),
            Arguments.of(null, "tst-XXXHDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.XXXHDPI))),
            Arguments.of(null, "tst-TVDPI", GroupQualifiers("tst", KQualifiers(dpi = KDpi.TVDPI))),
            //mixed
            Arguments.of(null, "group1-en-xxhdpi", GroupQualifiers("group1", KQualifiers("en", KDpi.XXHDPI))),
            Arguments.of(null, "group2-xhdpi-en-rGB", GroupQualifiers("group2", KQualifiers("enGB", KDpi.XHDPI))),

            //somehow invalid
            Arguments.of(true, "group-en-xhdpi-rUS", GroupQualifiers("group", KQualifiers("en", KDpi.XHDPI))),
            Arguments.of(true, "group-ab-vvhdpi", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(true, "group-ab", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(true, "group-abc", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(true, "group-abcd", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(true, "group-ab-cd", GroupQualifiers("group", KQualifiers.Undefined)),
            Arguments.of(true, "group-ab-rCD", GroupQualifiers("group", KQualifiers.Undefined)),
            //allowed any locale
            Arguments.of(false, "xyz", GroupQualifiers("xyz", KQualifiers.Undefined)),
            Arguments.of(false, "xyz-", GroupQualifiers("xyz", KQualifiers.Undefined)),
            Arguments.of(false, "xyz-a", GroupQualifiers("xyz", KQualifiers("a"))),
            Arguments.of(false, "xyz-ab", GroupQualifiers("xyz", KQualifiers("ab"))),
            Arguments.of(false, "xyz-abc", GroupQualifiers("xyz", KQualifiers("abc"))),
            Arguments.of(false, "xyz-abcd", GroupQualifiers("xyz", KQualifiers("abcd"))),
            Arguments.of(false, "xyz-ab-cd", GroupQualifiers("xyz", KQualifiers("abcd"))),
            Arguments.of(false, "xyz-ab-rCD", GroupQualifiers("xyz", KQualifiers("abcd"))),
            Arguments.of(false, "xyz-ab-rCDE", GroupQualifiers("xyz", KQualifiers("ab"))),

            Arguments.of(false, "group-tvdpi-ab-rCDE", GroupQualifiers("group", KQualifiers("ab", KDpi.TVDPI))),
            Arguments.of(false, "group-ab-rCDE-ldpi", GroupQualifiers("group", KQualifiers("ab", KDpi.LDPI))),
            Arguments.of(false, "group-xxxhdpi-abcd", GroupQualifiers("group", KQualifiers("abcd", KDpi.XXXHDPI))),
            Arguments.of(false, "group-ab-cd-hdpi", GroupQualifiers("group", KQualifiers("abcd", KDpi.HDPI))),
            Arguments.of(false, "group-ab-mdpi-cd", GroupQualifiers("group", KQualifiers("ab", KDpi.MDPI))),
        )
    }
}
