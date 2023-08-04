package com.jibru.kostra.plugin

import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.StringResourceKey
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class ResourcesKtGenerator(
    private val packageName: String,
    private val className: String = "K",
    private val items: List<ResItem>,
) {

    //group:[string] -> key:[dog] -> items:[dogEn, dogEnGb, ...]
    private val itemsPerGroupPerKey by lazy {
        items
            .groupBy { it.group.replaceFirstChar { k -> k.lowercase() } }
            .mapValues { itemsPerGroup -> itemsPerGroup.value.groupBy { it -> it.key }.toSortedMap() }
            .toSortedMap()
    }

    //region kCLass
    fun generateKClass(): String {
        val file = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec
                    .objectBuilder(className)
                    .addKClassResources()
                    .build(),
            )
            .build()

        return file.toString().replace("\n\n ", "\n ")
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
                val type = when {
                    resItem is ResItem.StringRes -> StringResourceKey::class
                    resItem is ResItem.FileRes && resItem.drawable -> DrawableResourceKey::class
                    else -> BinaryResourceKey::class
                }

                addProperty(
                    PropertySpec.builder(key, type, KModifier.PUBLIC)
                        .initializer("%T(%S)", type, key)
                        .build(),
                )
            }
        return this
    }
    //endregion
}
