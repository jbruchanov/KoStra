package com.jibru.kostra.plugin

import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.AppResources
import com.jibru.kostra.internal.FileDatabase
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.PluralDatabase
import com.jibru.kostra.internal.StringDatabase
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

class ResourcesKtGenerator(
    private val packageName: String,
    private val className: String = "K",
    items: List<ResItem>,
) : ResItemsProcessor(items) {

    private val resourcePropertyName = "Resources"
    private val innerDbsPath = "__kostra"

    fun generateKClass(): FileSpec {
        return FileSpec.builder(packageName, className)
            .apply {
                if (hasStrings) {
                    addAliasedImport(StringResourceKey::class, "S")
                }
                if (hasPlurals) {
                    addAliasedImport(PluralResourceKey::class, "P")
                }
                if (hasDrawables) {
                    addAliasedImport(DrawableResourceKey::class, "D")
                }
                if (hasOthers) {
                    addAliasedImport(BinaryResourceKey::class, "B")
                }
            }
            .addType(
                TypeSpec
                    .objectBuilder(className)
                    .apply {
                        tryAddPositionIndexedObject(stringsDistinctKeys, ResItem.String, StringResourceKey::class)
                        tryAddPositionIndexedObject(pluralsDistinctKeys, ResItem.Plural, PluralResourceKey::class)
                        otherItemsPerGroupPerKey.onEach { (group, items) ->
                            addLongKeyObjectWithProperties(items.keys, group)
                        }
                    }
                    .build(),
            )
            .build()
    }

    fun generateResources(): FileSpec {
        val typeAppResources = AppResources::class

        return FileSpec.builder(packageName, resourcePropertyName)
            .addProperty(
                PropertySpec.builder(resourcePropertyName, typeAppResources)
                    .initializer(
                        CodeBlock.Builder()
                            .apply {
                                addStatement("%T(", typeAppResources)
                                indent()
                                if (hasStrings) {
                                    addResourcesProperty(
                                        propertyName = ResItem.String,
                                        propertyType = StringDatabase::class,
                                        locales = stringsAndPluralsForDb.getValue(ResItem.String).keys,
                                        "$innerDbsPath/${ResItem.String}-%s.db",
                                    )
                                }
                                if (hasPlurals) {
                                    addResourcesProperty(
                                        propertyName = ResItem.Plural,
                                        propertyType = PluralDatabase::class,
                                        locales = stringsAndPluralsForDb.getValue(ResItem.Plural).keys,
                                        "$innerDbsPath/${ResItem.Plural}-%s.db",
                                    )
                                }
                                if (hasAnyFiles) {
                                    addStatement("%L = %T(%S),", ResItem.Binary, FileDatabase::class, "$innerDbsPath/${ResItem.Binary}.db")
                                }
                                unindent()
                                addStatement(")")
                            }.build(),
                    )
                    .build(),
            )
            .build()
    }

    /**
     * ```
     * string = StringDatabase(
     *   mapOf(
     *     Locale.Undefined to "__kostra/string-default.db",
     *     Locale(5_14_07_02) to "__kostra/string-engb.db",
     *   )
     * )
     * ```
     */
    private fun CodeBlock.Builder.addResourcesProperty(
        propertyName: String,
        propertyType: KClass<out Any>,
        locales: Collection<Locale>,
        fileNameTemplate: String,
    ) {
        addStatement("%L = %T(", propertyName, propertyType)
        indent()
        addStatement("mapOf(")
        indent()
        locales.onEach {
            if (it == Locale.Undefined) {
                add("%T.Undefined", Locale::class)
            } else {
                add("%T(%L)", Locale::class, it.formattedDbKey())
            }
            val tag = if (it == Locale.Undefined) "default" else it.languageRegion
            addStatement(" to %S,", fileNameTemplate.format(tag))
        }
        unindent()
        addStatement(")")
        unindent()
        addStatement("),")
    }

    private fun Locale.formattedDbKey() =
        key.toString().padStart(8, '0').windowed(2, 2).joinToString("_").trimStart('0')
}

/**
 * ```
 * object objectName {
 *    val item1: S = S(0)
 *    val item2: P = P(1)
 * }
 * ```
 */
private fun TypeSpec.Builder.tryAddPositionIndexedObject(
    items: List<String>?,
    objectName: String,
    type: KClass<out Any>,
) {
    items ?: return
    addType(
        TypeSpec
            .objectBuilder(objectName)
            .apply {
                items.forEachIndexed { index, key ->
                    addProperty(
                        PropertySpec.builder(key, type, KModifier.PUBLIC)
                            .initializer("%T(%L)", type, index)
                            .build(),
                    )
                }
            }
            .build(),
    )
}

/**
 * ```
 * object objectName {
 *    val resItemKey1: D = D(dbKey1)
 *    val resItemKey2: B = B(dbKey2)
 * }
 * ```
 */
private fun TypeSpec.Builder.addLongKeyObjectWithProperties(
    items: Set<ResItemKeyDbKey>,
    objectName: String,
) {
    val type = if (objectName == ResItem.Drawable) DrawableResourceKey::class else BinaryResourceKey::class
    addType(
        TypeSpec
            .objectBuilder(objectName)
            .apply {
                items.forEach { (resItemKey, dbRootKey) ->
                    addProperty(
                        PropertySpec.builder(resItemKey, type, KModifier.PUBLIC)
                            .initializer("%T(%L)", type, dbRootKey)
                            .build(),
                    )
                }
            }
            .build(),
    )
}
