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
                            result.add(ResItem.StringRes(keyMapper(key, file), text, qualifiers.key))
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
                            result.add(ResItem.StringArray(keyMapper(key, file), items, qualifiers.key))
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
                            result.add(ResItem.Plurals(keyMapper(key, file), items.toPluralList(), qualifiers.key))
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
    private fun XMLStreamReader.attrQuantity() = attr("quantity")?.let { PluralCategory.of(it) }
    private fun XMLStreamReader.attr(name: String) = (0 until attributeCount)
        .firstOrNull { getAttributeLocalName(it) == name }
        ?.let { getAttributeValue(it) }

    private fun XMLStreamReader.text() = elementText

    companion object {
        private const val TagResources = "resources"
        private const val TagString = "string"
        private const val TagStringArray = "string-array"
        private const val TagPlurals = "plurals"
        private const val TagItem = "item"
        internal const val parseStringArrays = false
    }
}
