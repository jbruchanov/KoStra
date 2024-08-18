@file:Suppress("MemberVisibilityCanBePrivate")

package com.jibru.kostra.plugin

import com.jibru.kostra.KQualifiers
import com.jibru.kostra.icu.PluralCategory
import com.jibru.kostra.icu.PluralCategory.Companion.toPluralList
import java.io.File
import java.io.Reader
import java.io.StringReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamReader
import org.slf4j.LoggerFactory

/**
 * Simple implementation for android resources parser.
 * Currently, implement only string variants:
 * string, string-array, plurals
 */
class AndroidResourcesXmlParser(
    private val keyMapper: (String, File) -> String = { key, _ -> key },
) {

    private val logger = LoggerFactory.getLogger(AndroidResourcesXmlParser::class.java)
    private val noFile = File("/")

    internal fun findStrings(xml: String, qualifiers: KQualifiers) = findStrings(StringReader(xml), qualifiers, noFile)

    fun findStrings(file: File, qualifiers: KQualifiers): List<ResItem> {
        logger.info(file.absolutePath)
        return findStrings(file.reader(), qualifiers, file)
    }

    fun findStrings(reader: Reader, qualifiers: KQualifiers, file: File = noFile): List<ResItem> {
        val xmlParser = XMLInputFactory.newInstance()
        val xmlReader = xmlParser.createXMLStreamReader(reader)
        var androidResourcesFile = false
        var level = 0
        val result = mutableListOf<ResItem>()
        while (xmlReader.hasNext()) {
            xmlReader.next()
            if (xmlReader.isStartElement) {
                level++
                val part = xmlReader.name.localPart
                when {
                    level == 1 && part == TagResources -> {
                        androidResourcesFile = true
                        logger.info("[$TagResources]:$TagResources")
                    }

                    level == 2 && androidResourcesFile && part == TagString -> {
                        val key = xmlReader.attrName()

                        if (key != null) {
                            val text = xmlReader.text()
                            logger.info("[$TagString]: '$key'='$text'")
                            try {
                                result.add(ResItem.StringRes(keyMapper(key, file), text, qualifiers.key).also {
                                    it.origin = file.takeIf { it != noFile }
                                })
                            } catch (t: Throwable) {
                                throwParsingException(TagString, file, xmlReader, t)
                            }
                        } else {
                            xmlReader.skipUntilEndElement()
                        }
                        level--
                    }

                    level == 2 && androidResourcesFile && part == TagStringArray && parseStringArrays -> {
                        val key = xmlReader.attrName()
                        if (key != null) {
                            val items = mutableListOf<String>()
                            xmlReader.parseNestedElements {
                                items.add(xmlReader.text())
                            }
                            logger.info("[$TagStringArray]: '$key'=[${items.joinToString(prefix = "[", postfix = "]") { "'$it'" }}]")
                            try {
                                result.add(ResItem.StringArray(keyMapper(key, file), items, qualifiers.key).also {
                                    it.origin = file.takeIf { it != noFile }
                                })
                            } catch (t: Throwable) {
                                throwParsingException(TagStringArray, file, xmlReader, t)
                            }
                        } else {
                            xmlReader.skipUntilEndElement()
                        }
                        level--
                    }

                    level == 2 && androidResourcesFile && part == TagPlurals -> {
                        val key = xmlReader.attrName()
                        if (key != null) {
                            val items = mutableMapOf<PluralCategory, String>()
                            xmlReader.parseNestedElements {
                                val pluralKey = xmlReader.attrQuantity() ?: throw IllegalStateException("Expecting 'quantity' attribute, $xmlReader")
                                items[pluralKey] = xmlReader.text()
                            }
                            logger.info("[$TagPlurals]: '$key'=[$items]")
                            try {
                                result.add(ResItem.Plurals(keyMapper(key, file), items.toPluralList(), qualifiers.key).also {
                                    it.origin = file.takeIf { it != noFile }
                                })
                            } catch (t: Throwable) {
                                throwParsingException(TagPlurals, file, xmlReader, t)
                            }
                        } else {
                            xmlReader.skipUntilEndElement()
                        }
                        level--
                    }
                }
            } else if (xmlReader.isEndElement) {
                level--
            }
        }
        return result
    }

    private fun throwParsingException(tag: String, file: File, xmlReader: XMLStreamReader, t: Throwable): Nothing {
        throw IllegalStateException("Unable to parse $tag\nFile '$file'\n${xmlReader.location}", t)
    }

    private fun XMLStreamReader.parseNestedElements(onItemAction: () -> Unit) {
        val reader = this
        val key = reader.attrName()
        if (key != null) {
            while (reader.hasNext()) {
                reader.next()
                when {
                    reader.isStartElement && reader.localName == TagItem -> onItemAction()
                    //comment or something we don't care about
                    reader.isCharacters || reader.eventType == XMLStreamConstants.COMMENT -> Unit
                    reader.isCharacters -> Unit
                    reader.isEndElement -> break
                    else -> throw IllegalStateException("Unexpected state:$reader")
                }
            }
        }
    }

    private fun XMLStreamReader.skipUntilEndElement() {
        require(isStartElement) { "XMLStreamReader is in invalid state, expected is in isStartElement " }
        var level = 1
        while (hasNext() && level > 0) {
            next()
            when {
                isStartElement -> level++
                isEndElement -> level--
            }
        }
    }

    private fun XMLStreamReader.attrName() = attr("name")
    private fun XMLStreamReader.trimIndent() = attr("trimIndent") == "true"
    private fun XMLStreamReader.trimMargin() = attr("trimMargin")

    private fun XMLStreamReader.attrQuantity() = attr("quantity")?.let { PluralCategory.of(it) }

    private fun XMLStreamReader.attr(name: String) = (0 until attributeCount)
        .firstOrNull { getAttributeLocalName(it) == name }
        ?.let { getAttributeValue(it) }

    private fun XMLStreamReader.text(): String {
        val trimMargin = trimMargin()
        val trimIndent = trimIndent()
        return elementText.let { text ->
            when {
                trimMargin != null -> text.trimMargin(trimMargin)
                trimIndent -> text.trimIndent()
                else -> text
            }
        }
    }

    companion object {
        private const val TagResources = "resources"
        private const val TagString = "string"
        private const val TagStringArray = "string-array"
        private const val TagPlurals = "plurals"
        private const val TagItem = "item"
        internal const val parseStringArrays = false
    }
}
