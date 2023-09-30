@file:Suppress("ktlint:standard:argument-list-wrapping")

package com.jibru.kostra.plugin

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.plugin.ext.addDefaultSuppressAnnotation
import com.jibru.kostra.plugin.ext.applyIf
import com.jibru.kostra.plugin.ext.asLocalResourceType
import com.jibru.kostra.plugin.task.ComposeDefaults
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KClass

class ComposeDefaultsKtGenerator(
    kClassName: String = KostraPluginConfig.KClassName,
    private val modulePrefix: String = "",
    private val internalVisibility: Boolean = false,
    private val onlyGetters: Boolean = false,
) {
    private val packageName = kClassName.substringBeforeLast(".", "")
    private val resourcePropertyName = modulePrefix + KostraPluginConfig.ResourcePropertyName
    private val resourceMemberName = MemberName(packageName, resourcePropertyName)
    private val stringProperty = "string"
    private val pluralProperty = "plural"
    private val painterPropertyMember = MemberName(KostraPluginConfig.PackageNameCompose, "painter")
    private val binaryProperty = "binary"

    private val localQualifierMember = MemberName(KostraPluginConfig.PackageNameCompose, "LocalQualifiers")

    private val stringClassName = String::class.asClassName()
    private val painterClassName = ClassName("androidx.compose.ui.graphics.painter", "Painter")
    private val composableType = ClassName("androidx.compose.runtime", "Composable")
    private val keyArgName = "key"

    fun generateComposeDefaults(vararg composeDefaults: ComposeDefaults): FileSpec {
        val quantityArgName = "quantity"
        val formatArgName = "formatArgs"

        fun FileSpec.Builder.addPlurals(
            funName: String,
            pluralType: MemberName,
        ): FileSpec.Builder {
            val getterFunName = if (funName.contains("plural")) "get" else "getOrdinal"
            return this
                .applyIf(composeDefaults.contains(ComposeDefaults.Common)) {
                    addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                        addParameter(quantityArgName, IFixedDecimal::class)
                        addCode("return %M.%L.get(%L, %M.current, %L, %M)", resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType)
                    }
                    addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                        addParameter(quantityArgName, Int::class)
                        addCode("return %M.%L.get(%L, %M.current, %L, %M)", resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType)
                    }
                    addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                        addParameter(quantityArgName, IFixedDecimal::class)
                        addParameter(formatArgName, Any::class, KModifier.VARARG)
                        addCode(
                            "return %M.%L.get(%L, %M.current, %L, %M, *%L)",
                            resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType, formatArgName,
                        )
                    }
                    addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                        addParameter(quantityArgName, Int::class)
                        addParameter(formatArgName, Any::class, KModifier.VARARG)
                        addCode(
                            "return %M.%L.get(%L, %M.current, %L, %M, *%L)",
                            resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType, formatArgName,
                        )
                    }
                }
                .applyIf(composeDefaults.contains(ComposeDefaults.Getters)) {
                    val keyArgName = "this"
                    addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                        addParameter(quantityArgName, IFixedDecimal::class)
                        addCode("return %M.%L.get(%L, %M.current, %L, %M)", resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType)
                    }
                    addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                        addParameter(quantityArgName, Int::class)
                        addCode("return %M.%L.get(%L, %M.current, %L, %M)", resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType)
                    }
                    addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                        addParameter(quantityArgName, IFixedDecimal::class)
                        addParameter(formatArgName, Any::class, KModifier.VARARG)
                        addCode(
                            "return %M.%L.get(%L, %M.current, %L, %M, *%L)",
                            resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType, formatArgName,
                        )
                    }
                    addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                        addParameter(quantityArgName, Int::class)
                        addParameter(formatArgName, Any::class, KModifier.VARARG)
                        addCode(
                            "return %M.%L.get(%L, %M.current, %L, %M, *%L)",
                            resourceMemberName, pluralProperty, keyArgName, localQualifierMember, quantityArgName, pluralType, formatArgName,
                        )
                    }
                }
        }

        return FileSpec
            .builder(packageName, KostraPluginConfig.ComposeDefaultResourceProvider_x.format("Common"))
            .addDefaultSuppressAnnotation("NOTHING_TO_INLINE")
            .applyIf(composeDefaults.contains(ComposeDefaults.Common)) {
                addComposeDefaultFunc("stringResource", StringResourceKey::class, stringClassName) {
                    addCode("return %M.%L.get(%L, %M.current)", resourceMemberName, stringProperty, keyArgName, localQualifierMember)
                }
                addComposeDefaultFunc("stringResource", StringResourceKey::class, stringClassName) {
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%L.get(%L, %M.current, *%L)", resourceMemberName, stringProperty, keyArgName, localQualifierMember, formatArgName)
                }
                addComposeDefaultFunc("painterResource", PainterResourceKey::class, painterClassName) {
                    addCode("return %M.%M(%L, %M.current)", resourceMemberName, painterPropertyMember, keyArgName, localQualifierMember)
                }
                addComposeDefaultFunc("assetPath", AssetResourceKey::class, stringClassName) {
                    addCode("return %M.%L.get(%L, %M.current)", resourceMemberName, binaryProperty, keyArgName, localQualifierMember)
                }
            }
            .applyIf(composeDefaults.contains(ComposeDefaults.Getters)) {
                val keyArgName = "this"
                addComposeDefaultGet(StringResourceKey::class, stringClassName) {
                    addCode("return %M.%L.get(%L, %M.current)", resourceMemberName, stringProperty, keyArgName, localQualifierMember)
                }
                addComposeDefaultGet(StringResourceKey::class, stringClassName) {
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%L.get(%L, %M.current, *%L)", resourceMemberName, stringProperty, keyArgName, localQualifierMember, formatArgName)
                }
                addComposeDefaultGet(PainterResourceKey::class, painterClassName) {
                    addCode("return %M.%M(%L, %M.current)", resourceMemberName, painterPropertyMember, keyArgName, localQualifierMember)
                }
                addComposeDefaultGet(AssetResourceKey::class, stringClassName) {
                    addCode("return %M.%L.get(%L, %M.current)", resourceMemberName, binaryProperty, keyArgName, localQualifierMember)
                }
            }
            .addPlurals("pluralStringResource", Plurals.Type::class.asClassName().member(Plurals.Type.Plurals.name))
            .addPlurals("ordinalStringResource", Plurals.Type::class.asClassName().member(Plurals.Type.Ordinals.name))
            .build()
    }

    private fun FileSpec.Builder.addComposeDefaultFunc(
        name: String,
        keyType: KClass<out ResourceKey>,
        returnType: ClassName,
        builder: FunSpec.Builder.() -> Unit,
    ): FileSpec.Builder {
        if (onlyGetters) return this
        addFunction(
            FunSpec.builder(name)
                .addModifiers(KModifier.INLINE, if (internalVisibility) KModifier.INTERNAL else KModifier.PUBLIC)
                .addAnnotation(composableType)
                .addParameter("key", keyType.asLocalResourceType(packageName))
                .apply(builder)
                .returns(returnType)
                .build(),
        )

        return this
    }

    private fun FileSpec.Builder.addComposeDefaultGet(
        receiverType: KClass<out ResourceKey>,
        returnType: ClassName,
        funName: String = "get",
        builder: FunSpec.Builder.() -> Unit,
    ): FileSpec.Builder {
        addFunction(
            FunSpec.builder(funName)
                .addModifiers(KModifier.INLINE, if (internalVisibility) KModifier.INTERNAL else KModifier.PUBLIC)
                .addAnnotation(composableType)
                .receiver(receiverType.asLocalResourceType(packageName))
                .apply(builder)
                .returns(returnType)
                .build(),
        )

        return this
    }
}
