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
    private val items: List<ResItem>
) {

    //region kCLass
    fun generateKClass(): String {
        val file = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec
                    .objectBuilder(className)
                    .addKClassResources(items)
                    .build(),
            )
            .build()

        return file.toString().replace("\n\n ", "\n ")
    }

    private fun TypeSpec.Builder.addKClassResources(resources: List<ResItem>): TypeSpec.Builder {
        resources
            .groupBy { it.group.replaceFirstChar { k -> k.lowercase() } }
            .toSortedMap()
            .onEach { (group, groupItems) ->
                addType(
                    TypeSpec
                        .objectBuilder(group)
                        .addKClassGroupItems(groupItems)
                        .build(),
                )
            }
        return this
    }

    private fun TypeSpec.Builder.addKClassGroupItems(resources: List<ResItem>): TypeSpec.Builder {
        resources
            //at this point mutliple items with same key is only difference because of qualifiers
            //so can be ignored as we need key+type
            .distinctBy { it.key }
            .sortedBy { it.key }
            .forEach { resItem ->
                val type = when {
                    resItem is ResItem.StringRes -> StringResourceKey::class
                    resItem is ResItem.FileRes && resItem.drawable -> DrawableResourceKey::class
                    else -> BinaryResourceKey::class
                }

                addProperty(
                    PropertySpec.builder(resItem.key, type, KModifier.PUBLIC)
                        .initializer("%T(%S)", type, resItem.key)
                        .build(),
                )
            }
        return this
    }
    //endregion
}
