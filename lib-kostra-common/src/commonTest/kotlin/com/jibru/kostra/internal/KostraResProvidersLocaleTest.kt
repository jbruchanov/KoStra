package com.jibru.kostra.internal

import com.jibru.kostra.Fixtures
import com.jibru.kostra.Fixtures.Resources.K
import com.jibru.kostra.MissionResourceException
import com.jibru.kostra.StringResourceKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KostraResProvidersLocaleTest : KostraResProviders {

    private val localeUnspecified = Qualifiers.Undefined
    private val localeEn = Qualifiers(locale = Locale("en", null))
    private val localeEnUs = Qualifiers(locale = Locale("en", "us"))
    private val localeEnGb = Qualifiers(locale = Locale("en", "gb"))
    private val localeCs = Qualifiers(locale = Locale("cs", null))
    private val localeCsCZ = Qualifiers(locale = Locale("cs", "cz"))

    @Test
    fun `stringResource WHEN no resLocale defined THEN works with any`() {
        with(Fixtures.Resources.stringResources) {
            val allLocales = listOf(localeUnspecified, localeEn, localeEnUs, localeCs, localeCsCZ)
            allLocales.forEach { locale ->
                assertEquals("Simple", stringResource(K.string.test1, locale).value)
            }
        }
    }

    @Test
    fun `stringResource WHEN resLocale defined with default THEN works with expected`() {
        with(Fixtures.Resources.stringResources) {
            assertEquals("Default", stringResource(K.string.test2, localeUnspecified).value)
            assertEquals("EN", stringResource(K.string.test2, localeEn).value)
            assertEquals("US", stringResource(K.string.test2, localeEnUs).value)
            assertEquals("EN", stringResource(K.string.test2, localeEnGb).value)
            assertEquals("Default", stringResource(K.string.test2, localeCs).value)
            assertEquals("Default", stringResource(K.string.test2, localeCsCZ).value)
        }
    }

    @Test
    fun `stringResource WHEN resLocale defined with no default THEN works with expected`() {
        with(Fixtures.Resources.stringResources) {
            assertEquals("EN", stringResource(K.string.test3, localeEn).value)
            assertEquals("EN", stringResource(K.string.test3, localeEnUs).value)
            assertEquals("EN", stringResource(K.string.test3, localeEnGb).value)
            assertFailsWith<MissionResourceException> { stringResource(K.string.test3, localeUnspecified).value }
            assertFailsWith<MissionResourceException> { stringResource(K.string.test3, localeCs).value }
            assertFailsWith<MissionResourceException> { stringResource(K.string.test3, localeCsCZ).value }
        }
    }

    @Test
    fun `stringResource WHEN fails THEN meaningful error`() {
        with(Fixtures.Resources.stringResources) {
            val ex1 = assertFailsWith<MissionResourceException> { stringResource(StringResourceKey("unknown"), localeUnspecified).value }
            assertEquals(StringResourceKey("unknown"), ex1.key)
            assertEquals("Undefined string resource", ex1.message)

            val ex2 = assertFailsWith<MissionResourceException> { stringResource(K.string.test3, localeCs).value }
            assertEquals(K.string.test3, ex2.key)
            assertEquals(
                "Unable to resolve value of key:'StringResourceKey(key=test3)' " +
                    "based on qualifiers:'Qualifiers(key='cs', locale=Locale(l='cs', r=null), dpi=Undefined, others=[])'",
                ex2.message,
            )
        }
    }
}
