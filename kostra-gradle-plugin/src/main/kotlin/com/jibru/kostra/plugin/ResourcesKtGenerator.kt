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
    private val className: String,
) {
    fun generateKClass(allResources: List<ResItem>): String {
        val file = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec
                    .objectBuilder(className)
                    .addResources(allResources)
                    .build(),
            )
            .build()

        return file.toString().replace("\n\n ", "\n ")
    }

    private fun TypeSpec.Builder.addResources(resources: List<ResItem>): TypeSpec.Builder {
        resources
            .groupBy { it.category.replaceFirstChar { k -> k.lowercase() } }
            .toSortedMap()
            .onEach { (categoryName, categoryItems) ->
                addType(
                    TypeSpec
                        .objectBuilder(categoryName)
                        .addCategoryItems(categoryItems)
                        .build(),
                )
            }
        return this
    }

    private fun TypeSpec.Builder.addCategoryItems(resources: List<ResItem>): TypeSpec.Builder {
        resources
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

    companion object {
        private val drawableCategories = setOf("drawable")
    }
}
