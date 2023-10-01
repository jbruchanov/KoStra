@file:Suppress("ktlint:standard:argument-list-wrapping")

package com.jibru.kostra.plugin

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.Plurals
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.icu.IFixedDecimal
import com.jibru.kostra.plugin.ext.addDefaultSuppressAnnotation
import com.jibru.kostra.plugin.ext.applyIf
import com.jibru.kostra.plugin.ext.applyIfNotNull
import com.jibru.kostra.plugin.ext.asLocalResourceType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

class DefaultsKtGenerator(
    kClassName: String = KostraPluginConfig.KClassName,
    private val modulePrefix: String = "",
    private val internalVisibility: Boolean = false,
) {
    private val kClassPackageName = kClassName.substringBeforeLast(".", "")
    private val kClassNameSimple = kClassName.substringAfterLast(".")
    private val resourcePropertyName = modulePrefix + KostraPluginConfig.ResourcePropertyName
    private val resourcesMemberName = MemberName(kClassPackageName, resourcePropertyName)
    private val painterPropertyMember = MemberName(KostraPluginConfig.PackageNameCompose, "painter")
    private val binaryPropertyMember = MemberName(KostraPluginConfig.PackageName, "binaryByteArray")
    private val localQualifierMember = MemberName(KostraPluginConfig.PackageNameCompose, "LocalQualifiers")
    private val defaultQualifiersProviderMember = MemberName(KostraPluginConfig.PackageName, "DefaultQualifiersProvider")

    private val pluralType = Plurals.Type::class.asClassName().member(Plurals.Type.Plurals.name)
    private val ordinalType = Plurals.Type::class.asClassName().member(Plurals.Type.Ordinals.name)
    private val byteArrayType = ByteArray::class.asTypeName()
    private val stringClassName = String::class.asClassName()

    private val painterClassName = ClassName("androidx.compose.ui.graphics.painter", "Painter")
    private val composableType = ClassName("androidx.compose.runtime", "Composable")
    private val formatArgName = "formatArgs"

    fun generateComposeDefaults(vararg resourcesDefaults: ResourcesDefaults): List<FileSpec> {
        return resourcesDefaults
            .partition { it.composable }
            .let { listOf(it.first, it.second) }
            .filter { it.isNotEmpty() }
            .map { defaultsPerGroup ->
                val composeGroup = defaultsPerGroup.any { it.composable }
                val packageName = if (composeGroup) "$kClassPackageName.compose".trimStart('.') else kClassPackageName
                val classNameSimple = if (composeGroup) "${kClassNameSimple}Compose" else kClassNameSimple
                FileSpec
                    .builder(packageName, KostraPluginConfig.ComposeDefaultResourceProvider_x.format(classNameSimple))
                    .addDefaultSuppressAnnotation("NOTHING_TO_INLINE")
                    .apply {
                        defaultsPerGroup.onEach {
                            addComposeDefaults(it)
                        }
                    }
                    .build()
            }
    }

    private fun FileSpec.Builder.addComposeDefaults(defaults: ResourcesDefaults): FileSpec.Builder {
        addKostraDefault(defaults, keyType = StringResourceKey::class, useVarArg = false)
        addKostraDefault(defaults, keyType = StringResourceKey::class, useVarArg = true)
        addKostraDefault(defaults, keyType = PainterResourceKey::class)
        addKostraDefault(defaults, keyType = PainterResourceKey::class, returnType = byteArrayType)
        addKostraDefault(defaults, keyType = AssetResourceKey::class)
        addKostraDefault(defaults, keyType = BinaryResourceKey::class)
        listOf(Int::class, IFixedDecimal::class).onEach { argType ->
            listOf(pluralType, ordinalType).onEach { pType ->
                listOf(true, false).onEach { useVarArg ->
                    addKostraDefault(defaults, keyType = PluralResourceKey::class, useVarArg = useVarArg, extraArg = "quantity" to argType.asTypeName(), extraParam = pType)
                }
            }
        }
        return this
    }

    private fun FileSpec.Builder.addKostraDefault(
        resourcesDefaults: ResourcesDefaults,
        keyType: KClass<out ResourceKey>,
        useVarArg: Boolean = false,
        extraArg: Pair<String, TypeName>? = null,
        extraParam: MemberName? = null,
        returnType: TypeName? = null,
    ) {
        val funcName = when {
            keyType == PainterResourceKey::class && resourcesDefaults.composable && returnType == byteArrayType -> return
            //compose/painter isn't valid for non composable, only byteArray
            keyType == PainterResourceKey::class && !resourcesDefaults.composable && returnType == null -> return
            resourcesDefaults.isGetter && extraParam == ordinalType -> "getOrdinal"
            resourcesDefaults.isGetter && keyType == PainterResourceKey::class && returnType == byteArrayType -> "getByteArray"
            keyType == PainterResourceKey::class && returnType == byteArrayType -> "byteArray"
            resourcesDefaults.isGetter && keyType == AssetResourceKey::class -> "getAssetPath"
            keyType == AssetResourceKey::class -> "assetPath"
            resourcesDefaults.isGetter -> "get"
            keyType == PluralResourceKey::class && extraParam == ordinalType -> "ordinalResource"
            keyType == PluralResourceKey::class && extraParam == pluralType -> "pluralResource"
            keyType == StringResourceKey::class -> "stringResource"
            keyType == PainterResourceKey::class -> "painterResource"
            keyType == BinaryResourceKey::class -> "binaryResource"
            else -> throw IllegalArgumentException("Unsupported input combination")
        }

        val (propertyName, retType) = when (keyType) {
            StringResourceKey::class -> "string" to stringClassName
            PluralResourceKey::class -> "plural" to stringClassName
            PainterResourceKey::class -> "painter" to painterClassName
            BinaryResourceKey::class -> "binary" to ByteArray::class.asClassName()
            AssetResourceKey::class -> "binary" to stringClassName
            else -> throw IllegalArgumentException("Unsupported $keyType")
        }

        @Suppress("NAME_SHADOWING")
        val returnType: TypeName = returnType ?: retType

        val extraKostraExtFunction = when {
            //extra ext function in libs
            keyType == BinaryResourceKey::class -> binaryPropertyMember
            keyType == PainterResourceKey::class && returnType == byteArrayType -> binaryPropertyMember
            keyType == PainterResourceKey::class -> painterPropertyMember
            else -> null
        }

        @Suppress("NAME_SHADOWING")
        val keyType = when {
            keyType == BinaryResourceKey::class && resourcesDefaults.isCommon -> AssetResourceKey::class
            else -> keyType
        }

        addFunction(
            FunSpec.builder(funcName)
                .addModifiers(KModifier.INLINE, if (internalVisibility) KModifier.INTERNAL else KModifier.PUBLIC)
                .applyIf(resourcesDefaults.composable) { addAnnotation(composableType) }
                .applyIf(resourcesDefaults.isGetter) { receiver(keyType.asLocalResourceType(kClassPackageName)) }
                .applyIf(resourcesDefaults.isCommon) { addParameter("key", keyType.asLocalResourceType(kClassPackageName)) }
                .applyIfNotNull(extraArg) { (name, type) -> addParameter(name, type) }
                .applyIf(useVarArg) { addParameter(formatArgName, Any::class, KModifier.VARARG) }
                .addCode(
                    CodeBlock.Builder()
                        .add("return ")
                        .add("%M", resourcesMemberName)
                        .apply {
                            if (extraKostraExtFunction != null) {
                                add(".%M", extraKostraExtFunction)
                            } else {
                                add(".%L.get", propertyName)
                            }
                        }
                        .add("(")
                        .add("%L", if (resourcesDefaults.isGetter) "this" else "key")
                        .add(", ")
                        .add("%M.current", if (resourcesDefaults.composable) localQualifierMember else defaultQualifiersProviderMember)
                        .applyIfNotNull(extraArg) { (name, _) -> add(", %L", name) }
                        .applyIfNotNull(extraParam) { add(", %M", it) }
                        .applyIf(useVarArg) { add(", *%L", formatArgName) }
                        .add(")")
                        .build(),
                )
                .returns(returnType)
                .build(),
        )
    }
}
