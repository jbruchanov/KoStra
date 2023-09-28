package com.jibru.kostra.plugin

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.plugin.ext.addDefaultSuppressAnnotation
import com.jibru.kostra.plugin.ext.asLocalResourceType
import com.jibru.kostra.plugin.task.ComposeDefaults
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
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
    private val stringExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "string")
    private val pluralExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "plural")
    private val ordinalExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "ordinal")
    private val painterExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "painter")
    private val assetExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "assetPath")

    private val stringClassName = String::class.asClassName()
    private val painterClassName = ClassName("androidx.compose.ui.graphics.painter", "Painter")
    private val svgPainterExtMember = MemberName(KostraPluginConfig.PackageNameCompose, "svgPainter")
    private val composableType = ClassName("androidx.compose.runtime", "Composable")
    private val keyArgName = "key"

    fun generateComposeDefaults(composeDefaults: ComposeDefaults): FileSpec = when (composeDefaults) {
        ComposeDefaults.Common -> generateCommonComposeDefaults()
        ComposeDefaults.Svg -> generateSvgComposeDefaults()
    }

    private fun generateCommonComposeDefaults(): FileSpec {
        val quantityArgName = "quantity"
        val formatArgName = "formatArgs"

        fun FileSpec.Builder.addPlurals(funName: String, extMember: MemberName): FileSpec.Builder {
            val getterFunName = if (funName.contains("plural")) "getPlural" else "getOrdinal"
            return this
                .addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                    addParameter(quantityArgName, IFixedDecimal::class)
                    addCode("return %M.%M(%L, %L)", resourceMemberName, extMember, keyArgName, quantityArgName)
                }
                .addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                    addParameter(quantityArgName, IFixedDecimal::class)
                    addCode("return %M.%M(this, %L)", resourceMemberName, extMember, quantityArgName)
                }
                .addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                    addParameter(quantityArgName, Int::class)
                    addCode("return %M.%M(%L, %L)", resourceMemberName, extMember, keyArgName, quantityArgName)
                }
                .addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                    addParameter(quantityArgName, Int::class)
                    addCode("return %M.%M(this, %L)", resourceMemberName, extMember, quantityArgName)
                }
                .addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                    addParameter(quantityArgName, IFixedDecimal::class)
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%M(%L, %L, *%L)", resourceMemberName, extMember, keyArgName, quantityArgName, formatArgName)
                }
                .addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                    addParameter(quantityArgName, IFixedDecimal::class)
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%M(this, %L, *%L)", resourceMemberName, extMember, quantityArgName, formatArgName)
                }
                .addComposeDefaultFunc(funName, PluralResourceKey::class, stringClassName) {
                    addParameter(quantityArgName, Int::class)
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%M(%L, %L, *%L)", resourceMemberName, extMember, keyArgName, quantityArgName, formatArgName)
                }
                .addComposeDefaultGet(PluralResourceKey::class, stringClassName, getterFunName) {
                    addParameter(quantityArgName, Int::class)
                    addParameter(formatArgName, Any::class, KModifier.VARARG)
                    addCode("return %M.%M(this, %L, *%L)", resourceMemberName, extMember, quantityArgName, formatArgName)
                }
        }

        return FileSpec
            .builder(packageName, KostraPluginConfig.ComposeDefaultResourceProvider_x.format("Common"))
            .addDefaultSuppressAnnotation("NOTHING_TO_INLINE")
            .addComposeDefaultFunc("stringResource", StringResourceKey::class, stringClassName) {
                addCode("return %M.%M(%L)", resourceMemberName, stringExtMember, keyArgName)
            }
            .addComposeDefaultGet(StringResourceKey::class, stringClassName) {
                addCode("return %M.%M(this)", resourceMemberName, stringExtMember)
            }
            .addComposeDefaultFunc("stringResource", StringResourceKey::class, stringClassName) {
                addParameter(formatArgName, Any::class, KModifier.VARARG)
                addCode("return %M.%M(%L, *%L)", resourceMemberName, stringExtMember, keyArgName, formatArgName)
            }
            .addComposeDefaultGet(StringResourceKey::class, stringClassName) {
                addParameter(formatArgName, Any::class, KModifier.VARARG)
                addCode("return %M.%M(this, *%L)", resourceMemberName, stringExtMember, formatArgName)
            }
            .addPlurals("pluralStringResource", pluralExtMember)
            .addPlurals("ordinalStringResource", ordinalExtMember)
            .addComposeDefaultFunc("painterResource", PainterResourceKey::class, painterClassName) {
                addCode("return %M.%M(%L)", resourceMemberName, painterExtMember, keyArgName)
            }
            .addComposeDefaultGet(PainterResourceKey::class, painterClassName) {
                addCode("return %M.%M(this)", resourceMemberName, painterExtMember)
            }
            .addComposeDefaultFunc("assetPath", AssetResourceKey::class, stringClassName) {
                addCode("return %M.%M(%L)", resourceMemberName, assetExtMember, keyArgName)
            }
            .addComposeDefaultGet(AssetResourceKey::class, stringClassName) {
                addCode("return %M.%M(this)", resourceMemberName, assetExtMember)
            }
            .build()
    }

    private fun generateSvgComposeDefaults() = FileSpec
        .builder(packageName, KostraPluginConfig.ComposeDefaultResourceProvider_x.format("Svg"))
        .addDefaultSuppressAnnotation("NOTHING_TO_INLINE")
        .addComposeDefaultFunc("svgPainterResource", PainterResourceKey::class, painterClassName) {
            addCode("return %M.%M(%L)", resourceMemberName, svgPainterExtMember, keyArgName)
        }
        .build()

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
