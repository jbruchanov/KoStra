package com.jibru.kostra.plugin

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.KAppResources
import com.jibru.kostra.KLocale
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.internal.FileDatabase
import com.jibru.kostra.internal.PluralDatabase
import com.jibru.kostra.internal.StringDatabase
import com.jibru.kostra.plugin.ext.addDefaultSurpressAnnotation
import com.jibru.kostra.plugin.ext.formattedDbKey
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KClass

class ResourcesKtGenerator(
    items: List<ResItem>,
    private val resDbsFolderName: String,
    className: String = KostraPluginConfig.KClassName,
    private val useAliasImports: Boolean = true,
) : ResItemsProcessor(items) {

    private val packageName = className.substringBeforeLast(".", "")
    private val className = className.substringAfterLast(".")
    private val resourcePropertyName = KostraPluginConfig.ResourcePropertyName
    private val resourceMemberName = MemberName(packageName, resourcePropertyName)

    private val composeDefaults = KostraPluginConfig.ComposeDefaultResourceProvider

    fun generateKClass(): FileSpec {
        return FileSpec.builder(packageName, className)
            .addDefaultSurpressAnnotation()
            .apply {
                if (useAliasImports) {
                    if (hasStrings) {
                        addAliasedImport(StringResourceKey::class, "S")
                    }
                    if (hasPlurals) {
                        addAliasedImport(PluralResourceKey::class, "P")
                    }
                    if (hasDrawables) {
                        addAliasedImport(PainterResourceKey::class, "D")
                    }
                    if (hasOthers) {
                        addAliasedImport(BinaryResourceKey::class, "B")
                    }
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
        val typeAppResources = KAppResources::class

        return FileSpec.builder(packageName, resourcePropertyName)
            .addDefaultSurpressAnnotation()
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
                                        "$resDbsFolderName/${ResItem.String}-%s.db",
                                    )
                                }
                                if (hasPlurals) {
                                    addResourcesProperty(
                                        propertyName = ResItem.Plural,
                                        propertyType = PluralDatabase::class,
                                        locales = stringsAndPluralsForDb.getValue(ResItem.Plural).keys,
                                        "$resDbsFolderName/${ResItem.Plural}-%s.db",
                                    )
                                }
                                if (hasAnyFiles) {
                                    addStatement("%L = %T(%S),", ResItem.Binary, FileDatabase::class, "$resDbsFolderName/${ResItem.Binary}.db")
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
        locales: Collection<KLocale>,
        fileNameTemplate: String,
    ) {
        addStatement("%L = %T(", propertyName, propertyType)
        indent()
        addStatement("mapOf(")
        indent()
        locales.onEach {
            if (it == KLocale.Undefined) {
                add("%T.Undefined", KLocale::class)
            } else {
                add("%T(%L)", KLocale::class, it.formattedDbKey())
            }
            val tag = if (it == KLocale.Undefined) "default" else it.languageRegion
            addStatement(" to %S,", fileNameTemplate.format(tag))
        }
        unindent()
        addStatement(")")
        unindent()
        addStatement("),")
    }

    fun generateComposeDefaults(): FileSpec {
        val stringExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "string")
        val pluralExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "plural")
        val ordinalExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "ordinal")
        val painterExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "painter")
        val assetExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "assetPath")
        val keyArgName = "key"
        val quantityArgName = "quantity"
        val formatArgName = "formatArgs"

        fun FileSpec.Builder.addPlurals(funName: String, extMember: MemberName): FileSpec.Builder {
            return this
                .addComposeDefaultFunc(funName, PluralResourceKey::class) {
                    addParameter(quantityArgName, IFixedDecimal::class)
                    addCode("return %M.%M(%L, %L)", resourceMemberName, extMember, keyArgName, quantityArgName)
                }
                .addComposeDefaultFunc(funName, PluralResourceKey::class) {
                    addParameter(quantityArgName, Int::class)
                    addCode("return %M.%M(%L, %L)", resourceMemberName, extMember, keyArgName, quantityArgName)
                }
                .addComposeDefaultFunc(funName, PluralResourceKey::class) {
                    addParameter(quantityArgName, IFixedDecimal::class)
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%M(%L, %L, *%L)", resourceMemberName, extMember, keyArgName, quantityArgName, formatArgName)
                }
                .addComposeDefaultFunc(funName, PluralResourceKey::class) {
                    addParameter(quantityArgName, Int::class)
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%M(%L, %L, *%L)", resourceMemberName, extMember, keyArgName, quantityArgName, formatArgName)
                }
        }

        return FileSpec.builder(packageName, composeDefaults)
            .addDefaultSurpressAnnotation("NOTHING_TO_INLINE")
            .addComposeDefaultFunc("stringResource", StringResourceKey::class) {
                addCode("return %M.%M(%L)", resourceMemberName, stringExtMember, keyArgName)
            }
            .addComposeDefaultFunc("stringResource", StringResourceKey::class) {
                addParameter(formatArgName, Any::class, KModifier.VARARG)
                addCode("return %M.%M(%L, *%L)", resourceMemberName, stringExtMember, keyArgName, formatArgName)
            }
            .addPlurals("pluralStringResource", pluralExtMember)
            .addPlurals("ordinalStringResource", ordinalExtMember)
            .addComposeDefaultFunc("painterResource", PainterResourceKey::class) {
                addCode("return %M.%M(%L)", resourceMemberName, painterExtMember, keyArgName)
            }
            .addComposeDefaultFunc("assetPath", AssetResourceKey::class) {
                addCode("return %M.%M(%L)", resourceMemberName, assetExtMember, keyArgName)
            }
            .build()
    }

    private fun FileSpec.Builder.addComposeDefaultFunc(
        name: String,
        keyType: KClass<out ResourceKey>,
        builder: FunSpec.Builder.() -> Unit,
    ): FileSpec.Builder {
        val composableType = ClassName("androidx.compose.runtime", "Composable")

        val returnType = when (keyType) {
            PainterResourceKey::class -> ClassName("androidx.compose.ui.graphics.painter", "Painter")
            else -> String::class.asClassName()
        }

        addFunction(
            FunSpec.builder(name)
                .addModifiers(KModifier.INLINE)
                .addAnnotation(composableType)
                .addParameter("key", keyType)
                .apply(builder)
                .returns(returnType)
                .build(),
        )
        return this
    }
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
    val type = if (objectName == ResItem.Drawable) PainterResourceKey::class else BinaryResourceKey::class
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
