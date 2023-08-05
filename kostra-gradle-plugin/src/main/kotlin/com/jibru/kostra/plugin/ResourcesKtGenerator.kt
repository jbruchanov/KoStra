package com.jibru.kostra.plugin

import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.StringResourceKey
import com.jibru.kostra.internal.AppResources
import com.jibru.kostra.internal.Dpi
import com.jibru.kostra.internal.KostraResourceHider
import com.jibru.kostra.internal.Locale
import com.jibru.kostra.internal.Qualifiers
import com.jibru.kostra.internal.ResourceItem
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import org.gradle.configurationcache.extensions.capitalized

class ResourcesKtGenerator(
    private val packageName: String,
    private val className: String = "K",
    private val items: List<ResItem>,
) {
    private val shareQualifiersClassName = "KQualifiers"
    private val supportResourcesFileName = "SupportResources"
    private val resourcesPropertyName = "Resources"

    private val typeResourceItem = ResourceItem::class.asTypeName()
    private val typeResourceContainerValue = ResourceContainer.Value::class.asTypeName()
    private val typeResourceHider = KostraResourceHider::class.asTypeName()
    private val typeResourceProvider = ClassName(typeResourceHider.packageName, "KostraResProviders")
    private val typeQualifiers = Qualifiers::class.asTypeName()

    //class name to have in code Qualifiers.cs, instead of just cs + import
    private val typeQualifierUndefined = Qualifiers::class.asClassName().let { ClassName(it.packageName, it.simpleName, "Undefined") }

    //group:[string] -> key:[dog] -> items:[dogEn, dogEnGb, ...]
    private val itemsPerGroupPerKey by lazy {
        items
            .groupBy { it.group.replaceFirstChar { k -> k.lowercase() } }
            .mapValues { itemsPerGroup -> itemsPerGroup.value.groupBy { it -> it.key }.toSortedMap() }
            .toSortedMap()
    }

    //map of qualifiers
    private val qualifierRefsPerQualifiers by lazy {
        items
            .map { it.qualifiers }
            .distinct()
            .filter { it != Qualifiers.Undefined }
            .associateWith { ClassName(packageName, shareQualifiersClassName, it.key) }
            .toSortedMap { o1, o2 -> o1.key.compareTo(o2.key) }
    }

    private val itemsPerResourceGroupPerKey by lazy {
        items
            .groupBy { it.resourcesGroup }
            .mapValues { resGroupItems -> resGroupItems.value.groupBy { it.key }.toSortedMap() }
    }

    //region kCLass
    fun generateKClass(): FileSpec {
        return FileSpec.builder(packageName, className)
            .addType(
                TypeSpec
                    .objectBuilder(className)
                    .addKClassResources()
                    .build(),
            )
            .build()
    }

    private fun TypeSpec.Builder.addKClassResources(): TypeSpec.Builder {
        itemsPerGroupPerKey
            .onEach { (group, itemsPerKey) ->
                addType(
                    TypeSpec
                        .objectBuilder(group)
                        .addKClassGroupItems(group, itemsPerKey)
                        .build(),
                )
            }
        return this
    }

    private fun TypeSpec.Builder.addKClassGroupItems(group: String, resources: Map<String, List<ResItem>>): TypeSpec.Builder {
        resources
            .forEach { (key, itemsPerKey) ->
                //at this point, all resource per key should belong to single group
                val resItem = itemsPerKey.distinctBy { it::class }.single()
                addProperty(
                    PropertySpec.builder(key, resItem.resourceKeyType, KModifier.PUBLIC)
                        .initializer("%T(%S)", resItem.resourceKeyType, key)
                        .build(),
                )
            }
        return this
    }
    //endregion

    /**
     * Generate support resources.
     * Currently list of Qualifiers to save some resources
     */
    fun generateSupportResources(): FileSpec? {
        if (qualifierRefsPerQualifiers.isEmpty()) return null
        return FileSpec.builder(packageName, supportResourcesFileName)
            .addType(
                TypeSpec.objectBuilder(shareQualifiersClassName)
                    .addModifiers(KModifier.INTERNAL)
                    .apply {
                        qualifierRefsPerQualifiers
                            .keys
                            .forEach {
                                addProperty(
                                    PropertySpec.builder(it.key, typeQualifiers)
                                        .initializer(it.toCodeBlock())
                                        .build(),
                                )
                            }
                    }
                    .build(),
            )
            .build()
    }

    /**
     * Generate files per each resources group
     */
    fun generateResourceClasses(): List<FileSpec> {
        val propsPerGroup = itemsPerResourceGroupPerKey
            .mapValues { (property, items) ->
                createSinglePropertyMap(property, items)
            }

        val propsGettersPerGroup = itemsPerResourceGroupPerKey
            .mapValues { (property, items) ->
                createResourcesHiddenExtensionGetter(property, items)
            }

        val resourceGroupFiles = propsPerGroup.map { (group, propSpec) ->
            FileSpec.builder(packageName, group.capitalized())
                .addProperty(propSpec)
                .addFunction(propsGettersPerGroup.getValue(group))
                .build()
        }

        return resourceGroupFiles
    }

    /**
     * ```
     * public val Resources: AppResources = AppResources(
     *   binary = with(KostraResourceHider) { binary },
     *   drawable = with(KostraResourceHider) { drawable },
     *   string = with(KostraResourceHider) { string },
     *   plural = with(KostraResourceHider) { plural },
     * )
     * ```
     */
    fun generateCreateAppResources(): FileSpec {
        val appResourcesType = AppResources::class.asTypeName()
        val file = FileSpec.builder(packageName, resourcesPropertyName)
            .addProperty(
                PropertySpec.builder(resourcesPropertyName, appResourcesType, KModifier.PUBLIC)
                    .initializer(
                        CodeBlock.Builder()
                            .apply {
                                addStatement("%T(", appResourcesType)
                                indent()
                                itemsPerResourceGroupPerKey.forEach { (group, _) ->
                                    addStatement("$group = with(%T) { %N() },", typeResourceHider, group)
                                }
                                unindent()
                                addStatement(")")
                            }.build(),
                    ).build(),
            ).build()
        return file
    }

    /**
     * Create a single file of resource properties
     * ```
     * private val string: Map<StringResourceKey, ResourceContainer> = buildMap(1) {
     *   put(K.string.str1, ResourceContainer.Value(key = K.string.str1,
     *     values = listOf(
     *       ResourceItem(K.string.str1, "value:str1", Qualifiers.Undefined)
     *   )))
     * }
     * ```
     */
    private fun createSinglePropertyMap(
        property: String,
        items: Map<String, List<ResItem>>,
    ): PropertySpec {
        val item = items.getValue(items.keys.first()).first().resourceKeyType
        val mapKeyType = Map::class.asTypeName().parameterizedBy(item, ResourceContainer::class.asTypeName())
        return PropertySpec.builder(property, mapKeyType, KModifier.PRIVATE)
            .initializer(
                CodeBlock.Builder()
                    .addStatement("buildMap(%L) {", items.size)
                    .indent()
                    .apply {
                        items.forEach {
                            addPutResourceContainerToMapByKey(it.value, qualifierRefsPerQualifiers)
                        }
                    }
                    .unindent()
                    .addStatement("}")
                    .build(),
            )
            .build()
    }

    /**
     * Create hidden getter
     * ```
     * public fun KostraResourceHider.string(): Map<StringResourceKey, ResourceContainer> = string
     * ```
     */
    private fun createResourcesHiddenExtensionGetter(
        property: String,
        items: Map<String, List<ResItem>>,
    ): FunSpec {
        val item = items.getValue(items.keys.first()).first().resourceKeyType
        val mapKeyType = Map::class.asTypeName().parameterizedBy(item, ResourceContainer::class.asTypeName())
        return FunSpec.builder(property)
            .receiver(typeResourceHider)
            .returns(mapKeyType)
            .addStatement("return %L", property)
            .build()
    }

    /**
     * Add code block of put item into map
     * ```
     * put(K.string.item2, ResourceContainer.Value(key = K.string.item2,
     *     values = listOf(
     *       ResourceItem(K.string.item2, "src2Item2",  Qualifiers.Undefined),
     *   )))
     * ```
     */
    private fun CodeBlock.Builder.addPutResourceContainerToMapByKey(resItems: List<ResItem>, qualifiers: Map<Qualifiers, ClassName>): CodeBlock.Builder {
        val type = resItems.first().let { ClassName(packageName, "K", it.group, it.key) }
        add("put(%1T, %2T(", type, typeResourceContainerValue)
        indent()
        addStatement("key = %1T, ", type)
        addStatement("values = listOf(")
        indent()
        resItems.forEach { item ->
            val qualifier = if (item.qualifiers == Qualifiers.Undefined) {
                typeQualifierUndefined
            } else {
                qualifiers.getValue(item.qualifiers)
            }
            when (item) {
                //TODO: KS32 REDO
                /*
                is StringValueResItem -> addStatement("%1T(%2T, %3S,  %4T),", typeResourceItem, type, item.value, qualifier)
                is ResItem.Plurals -> {
                    add("%1T(%2T, ", typeResourceItem, type)
                    add(
                        CodeBlock.builder()
                            .add("mapOf(")
                            .indent()
                            .apply {
                                item.items.forEach { key, value ->
                                    add("%S to %S,", key, value)
                                }
                            }
                            .unindent()
                            .addStatement("),")
                            .build(),
                    )
                    add("%T", qualifier)
                    addStatement("),")
                }
                 */

                else -> throw UnsupportedOperationException(item.toString())
            }
        }
        unindent()
        unindent()
        addStatement(")))")
        return this
    }

    private fun Qualifiers.toCodeBlock() = CodeBlock.Builder()
        .apply {
            add("%T(locale = ", Qualifiers::class)
            add("%T(%S, %S)", Locale::class, locale.language, locale.region)
            add(", dpi = ")
            add("%T.%L", Dpi::class, dpi.name)
            add(", others = setOf(")
            others.forEach { add("%S,", it) }
            add("))")
        }
        .build()

    /**
     * Generate all resource providers
     * ```
     * @Composable
     * fun stringResource(...)
     * ```
     */
    fun generateResourceProviders(): FileSpec {
        val typeComposable = ClassName("androidx.compose.runtime", "Composable")
        val typeComposePainter = ClassName("androidx.compose.ui.graphics.painter", "Painter")
        val typeComposeResource = ClassName("org.jetbrains.compose.resources", "Resource")
        val typeFunComposeResource = ClassName("org.jetbrains.compose.resources", "resource")
        val typeFunComposePainterResource = ClassName("org.jetbrains.compose.resources", "painterResource")
        val typeFunRememberDefaultResourceQualifiers = ClassName("com.jibru.kostra.compose", "rememberDefaultResourceQualifiers")

        return FileSpec.builder(packageName, "ResourceProvider")
            //fun stringResource(key: StringResourceKey): String
            .addAnnotation(
                AnnotationSpec.builder(ClassName("kotlin", "OptIn"))
                    .addMember("%T::class", ClassName("org.jetbrains.compose.resources", "ExperimentalResourceApi"))
                    .build(),
            )
            .addFunction(
                FunSpec.builder("stringResource")
                    .addAnnotation(typeComposable)
                    .addParameter("key", StringResourceKey::class)
                    .returns(String::class)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement("with(%T) {", typeResourceProvider)
                            .indent()
                            .addStatement("val qualifiers = rememberDefaultResourceQualifiers()")
                            .addStatement("return %N.stringResource(key, qualifiers).value", resourcesPropertyName)
                            .unindent()
                            .addStatement("}")
                            .build(),
                    )
                    .build(),
            )
            .addFunction(
                //fun stringResource(key: StringResourceKey, vararg formatArgs: String)
                FunSpec.builder("stringResource")
                    .addAnnotation(typeComposable)
                    .addParameter("key", StringResourceKey::class)
                    .addParameter("formatArgs", Any::class, KModifier.VARARG)
                    .returns(String::class)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement("with(%T) {", typeResourceProvider)
                            .indent()
                            .addStatement("val qualifiers = rememberDefaultResourceQualifiers()")
                            .addStatement("return %N.stringResource(key, qualifiers).value.format(*formatArgs)", resourcesPropertyName)
                            .unindent()
                            .addStatement("}")
                            .build(),
                    )
                    .build(),
            )
            .addFunction(
                //public fun pluralStringResource(key: PluralResourceKey, count: Int, vararg formatArgs: Any): String {
                FunSpec.builder("pluralStringResource")
                    .addAnnotation(typeComposable)
                    .addParameter("key", PluralResourceKey::class)
                    .addParameter("count", Int::class)
                    .returns(String::class)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement("with(%T) {", typeResourceProvider)
                            .indent()
                            .addStatement("val qualifiers = rememberDefaultResourceQualifiers()")
                            .addStatement("val quantityKey = %S", "other")
                            .addStatement("return %N.pluralResource(key, qualifiers, count).value.getValue(quantityKey)", resourcesPropertyName)
                            .unindent()
                            .addStatement("}")
                            .build(),
                    )
                    .build(),
            )
            .addFunction(
                //public fun pluralStringResource(key: PluralResourceKey, count: Int, vararg formatArgs: Any): String {
                FunSpec.builder("pluralStringResource")
                    .addAnnotation(typeComposable)
                    .addParameter("key", PluralResourceKey::class)
                    .addParameter("count", Int::class)
                    .addParameter("formatArgs", Any::class, KModifier.VARARG)
                    .returns(String::class)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement("with(%T) {", typeResourceProvider)
                            .indent()
                            .addStatement("val qualifiers = rememberDefaultResourceQualifiers()")
                            .addStatement("val quantityKey = %S", "other")
                            .addStatement("return %N.pluralResource(key, qualifiers, count).value.getValue(quantityKey).format(*formatArgs)", resourcesPropertyName)
                            .unindent()
                            .addStatement("}")
                            .build(),
                    )
                    .build(),
            )
            .addFunction(
                //public fun painterResource(key: DrawableResourceKey): Painter
                FunSpec.builder("painterResource")
                    .addAnnotation(typeComposable)
                    .addParameter("key", DrawableResourceKey::class)
                    .returns(typeComposePainter)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement("with(%T) {", typeResourceProvider)
                            .indent()
                            .addStatement("val qualifiers = rememberDefaultResourceQualifiers()")
                            .addStatement("return %T(%N.painterResource(key, qualifiers).value)", typeFunComposePainterResource, resourcesPropertyName)
                            .unindent()
                            .addStatement("}")
                            .build(),
                    )
                    .build(),
            )
            .addFunction(
                //public fun binaryResource(key: DrawableResourceKey): Resource
                FunSpec.builder("binaryResource")
                    .addAnnotation(typeComposable)
                    .addParameter("key", DrawableResourceKey::class)
                    .returns(typeComposeResource)
                    .addCode(
                        CodeBlock.builder()
                            .addStatement("with(%T) {", typeResourceProvider)
                            .indent()
                            .addStatement("val qualifiers = %T()", typeFunRememberDefaultResourceQualifiers)
                            .addStatement("return %T(%N.binaryResource(key, qualifiers).value)", typeFunComposeResource, resourcesPropertyName)
                            .unindent()
                            .addStatement("}")
                            .build(),
                    )
                    .build(),
            )
            .build()
    }
}
