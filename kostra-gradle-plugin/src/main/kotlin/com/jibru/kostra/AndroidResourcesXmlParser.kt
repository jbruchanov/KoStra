@file:Suppress("MemberVisibilityCanBePrivate")

package com.jibru.kostra

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
object AndroidResourcesXmlParser {
    private const val TagResources = "resources"
    private const val TagString = "string"
    private const val TagStringArray = "string-array"
    private const val TagPlurals = "plurals"
    private const val TagItem = "item"

    private val logger = LoggerFactory.getLogger(AndroidResourcesXmlParser::class.java)

    fun findStrings(file: File, locale: String): List<ResourceItem> {
        logger.info(file.absolutePath)
        return findStrings(file.bufferedReader(), locale)
    }

    fun findStrings(xml: String, locale: String) = findStrings(StringReader(xml), locale)

    fun findStrings(reader: Reader, locale: String): List<ResourceItem> {
        val xmlParser = XMLInputFactory.newInstance()
        val xmlReader = xmlParser.createXMLStreamReader(reader)
        var androidResourcesFile = false
        var level = 0
        val result = mutableListOf<ResourceItem>()
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
                            result.add(ResourceItem.StringRes(key, text, locale))
                        } else {
                            xmlReader.skipUntilEndElement()
                        }
                        level--
                    }

                    level == 2 && androidResourcesFile && part == TagStringArray -> {
                        val key = xmlReader.attrName()
                        if (key != null) {
                            val items = mutableListOf<String>()
                            xmlReader.parseNestedElements {
                                items.add(xmlReader.text())
                            }
                            logger.info("[$TagStringArray]: '$key'=[${items.joinToString(prefix = "[", postfix = "]") { "'$it'" }}]")
                            result.add(ResourceItem.StringArray(key, items, locale))
                        } else {
                            xmlReader.skipUntilEndElement()
                        }
                        level--
                    }

                    level == 2 && androidResourcesFile && part == TagPlurals -> {
                        val key = xmlReader.attrName()
                        if (key != null) {
                            val items = mutableMapOf<String, String>()
                            xmlReader.parseNestedElements {
                                val quantity = xmlReader.attrQuantity() ?: throw IllegalStateException("Expecting 'quantity' attribute, $xmlReader")
                                items[quantity] = xmlReader.text()
                            }
                            logger.info("[$TagPlurals]: '$key'=[$items]")
                            result.add(ResourceItem.Plurals(key, items, locale))
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
            val items = mutableListOf<String>()
            run {
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
    private fun XMLStreamReader.attrQuantity() = attr("quantity")
    private fun XMLStreamReader.attr(name: String) = (0 until attributeCount)
        .firstOrNull { getAttributeLocalName(it) == name }
        ?.let { getAttributeValue(it) }

    private fun XMLStreamReader.text() = elementText
}
