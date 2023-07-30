package com.jibru.kostra

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class ResourcesKtGenerator(
    private val packageName: String,
    private val className: String,
) {
    fun generateKClass(allResources: List<ResourceItem>): String {
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

    private fun TypeSpec.Builder.addResources(resources: List<ResourceItem>): TypeSpec.Builder {
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

    private fun TypeSpec.Builder.addCategoryItems(resources: List<ResourceItem>): TypeSpec.Builder {
        resources
            .sortedBy { it.key }
            .forEach {
                addProperty(
                    PropertySpec.builder(it.key, String::class, KModifier.PUBLIC)
                        .addModifiers(KModifier.CONST)
                        .initializer("%S", it.key)
                        .build(),
                )
            }
        return this
    }
}
