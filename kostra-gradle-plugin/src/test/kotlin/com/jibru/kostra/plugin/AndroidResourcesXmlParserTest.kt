package com.jibru.kostra.plugin

import com.google.common.truth.Truth.assertThat
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import com.jibru.kostra.KQualifiers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIf
import javax.xml.stream.XMLStreamException

internal class AndroidResourcesXmlParserTest {

    private val xmlParser = AndroidResourcesXmlParser()

    @Test
    fun `findStrings WHEN different types`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string name="text1">Text1</string>
            <!--
                <string name="comment1">Comment1</string>
                <string name="comment2">comment2</string>
                Comment
            -->
            <string name="text2">text2</string>
            ignore
            <plurals name="plural_empty" />
            <plurals name="plural_dogs">
                <item quantity="one">dog</item>
                <item quantity="other">dogs</item>
            </plurals>
        </resources>
        """.trimIndent()
        val result = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
        assertThat(result).hasSize(4)
        assertAll(
            {
                val item = result[0] as ResItem.StringRes
                assertThat(item.key).isEqualTo("text1")
                assertThat(item.value).isEqualTo("Text1")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
            {
                val item = result[1] as ResItem.StringRes
                assertThat(item.key).isEqualTo("text2")
                assertThat(item.value).isEqualTo("text2")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
            {
                val item = result[2] as ResItem.Plurals
                assertThat(item.key).isEqualTo("plural_empty")
                assertThat(item.items).isEqualTo(List<String?>(PluralCategory.size) { null })
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
            {
                val item = result[3] as ResItem.Plurals
                assertThat(item.key).isEqualTo("plural_dogs")
                assertThat(item.items).isEqualTo(
                    mapOf(
                        PluralCategory.One to "dog",
                        PluralCategory.Other to "dogs",
                    ).toPluralList(),
                )
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
        )
    }

    @Test
    @EnabledIf(value = "isParseXmlEnabled")
    fun `findStrings WHEN string arrays types`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string-array name="empty" />
            <string-array name="colors">
                <item>#FFFF0000</item>
                <item>#FF00FF00</item>
            </string-array>
        </resources>
        """.trimIndent()
        val result = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
        assertThat(result).hasSize(2)
        assertAll(
            {
                val item = result[0] as ResItem.StringArray
                assertThat(item.key).isEqualTo("empty")
                assertThat(item.items).isEmpty()
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
            {
                val item = result[1] as ResItem.StringArray
                assertThat(item.key).isEqualTo("colors")
                assertThat(item.items).isEqualTo(listOf("#FFFF0000", "#FF00FF00"))
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
        )
    }

    @Test
    fun `findStrings WHEN invalid types THEN ignored`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string>ignored</string>
            <string>text2</string>
            <string-array/>
            <plurals />
            <plurals>
                <item>dog</item>
                <xitem quantity="other">dogs</xitem>
            </plurals>
            <something>
                <string name="text2">text2</string>
            </something>
            <!-- good one -->
            <string name="text1">Text1</string>
        </resources>
        """.trimIndent()
        val result = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
        assertThat(result).hasSize(1)
        assertAll(
            {
                val item = result[0] as ResItem.StringRes
                assertThat(item.key).isEqualTo("text1")
                assertThat(item.value).isEqualTo("Text1")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
        )
    }

    @Test
    fun `findStrings WHEN string elements`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string name="text1">Text1</string>
            <!--
                <string name="comment1">Comment1</string>
                <string name="comment2">comment2</string>
                Comment
            -->
            <string name="text2">text2</string>
        </resources>
        """.trimIndent()
        val result = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
        assertThat(result).hasSize(2)
        assertAll(
            {
                val item = result[0] as ResItem.StringRes
                assertThat(item.key).isEqualTo("text1")
                assertThat(item.value).isEqualTo("Text1")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
            {
                val item = result[1] as ResItem.StringRes
                assertThat(item.key).isEqualTo("text2")
                assertThat(item.value).isEqualTo("text2")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
            },
        )
    }

    @Test
    fun `findStrings WHEN escapes, UTF chars`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string name="special1">&lt;&amp;/#\&gt;@</string>
            <string name="emojis">👍⚙️🚗</string>
            <string name="special_utf">&#x18e;&#x2190;</string>
            <string name="emoji_utf">&#x1f600;</string>
            <string name="flag_uk">🇬🇧</string>
        </resources>
        """.trimIndent()
        val items = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
            .filterIsInstance<ResItem.StringRes>()
            .associate { it.key to it.value }
        assertThat(items).isEqualTo(
            mapOf(
                "special1" to "<&/#\\>@",
                "emojis" to "\uD83D\uDC4D⚙️\uD83D\uDE97",
                "special_utf" to "Ǝ←",
                "emoji_utf" to "😀",
                "flag_uk" to "\uD83C\uDDEC\uD83C\uDDE7",
            ),
        )
    }

    @Test
    fun `findStrings WHEN string includes another string Then error`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string name="brokenString">
                <string name="text2">text2</string>
            </string>
        </resources>
        """.trimIndent()

        assertThrows<XMLStreamException> { xmlParser.findStrings(stringXml, KQualifiers.Undefined) }
    }

    @Test
    @EnabledIf(value = "isParseXmlEnabled")
    fun `findStrings WHEN string-array`() {
        val stringXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <resources>
                <string-array name="empty" />
                <string-array name="empty_comment">
                    <!--item>item1</item-->
                </string-array>
                <string-array name="colors">
                    <item>item0</item>
                    <!--item>item1</item-->
                    <item>item1</item>
                </string-array>
            </resources>
        """.trimIndent()

        val result = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
        assertThat(result).hasSize(3)
        assertAll(
            {
                val item = result[0] as ResItem.StringArray
                assertThat(item.key).isEqualTo("empty")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
                assertThat(item.items).isEmpty()
            },
            {
                val item = result[1] as ResItem.StringArray
                assertThat(item.key).isEqualTo("empty_comment")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
                assertThat(item.items).isEmpty()
            },
            {
                val item = result[2] as ResItem.StringArray
                assertThat(item.key).isEqualTo("colors")
                assertThat(item.qualifiers).isEqualTo(KQualifiers.Undefined)
                assertThat(item.items).hasSize(2)
                assertThat(item.items).isEqualTo(listOf("item0", "item1"))
            },
        )
    }

    @Test
    fun `findStrings WHEN no resources THEN empty`() {
        val header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        val xmls = listOf(
            //'resource' instead of 'resources'
            """<resource><string name="empty"></string></resource>""",
            //no 'resources' at all
            """<string name="xyz">xyz</string>""",
            //empty string fails on parser same as android
        )

        val testAsserts = xmls.map { { assertThat(xmlParser.findStrings("$header\n$it", KQualifiers.Undefined)).isEmpty() } }
        assertAll(*testAsserts.toTypedArray())
    }

    @Test
    fun `findStrings WHEN keyMapper then keys updated`() {
        val stringXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <resources>
            <string name="TEXT1">Text1</string>
        </resources>
        """.trimIndent()
        val xmlParser = AndroidResourcesXmlParser(keyMapper = { key, _ -> key.lowercase() })
        val result = xmlParser.findStrings(stringXml, KQualifiers.Undefined)
        val item = result[0] as ResItem.StringRes
        assertThat(item.key).isEqualTo("text1")
    }

    private fun isParseXmlEnabled() = AndroidResourcesXmlParser.parseStringArrays
}
