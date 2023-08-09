package com.jibru.kostra.plugin

import com.jibru.kostra.internal.Locale
import com.jibru.kostra.plugin.ext.distinctByLast
import com.jibru.kostra.plugin.ext.setOf

data class ResItemKeyDbKey(
    val resItemKey: String,
    val dbKey: Int,
)

open class ResItemsProcessor(private val items: List<ResItem>) {
    val distinctItemsByDistinctKey by lazy { items.distinctByLast { it.distinctKey } }

    val allItemsPerGroup by lazy {
        distinctItemsByDistinctKey
            .groupBy { it.group.replaceFirstChar { k -> k.lowercase() } }
            .mapValues { it.value.sortedBy { it.key } }
    }

    val hasStrings by lazy { allItemsPerGroup.containsKey(ResItem.String) }
    val hasPlurals by lazy { allItemsPerGroup.containsKey(ResItem.Plural) }
    val hasDrawables by lazy { allItemsPerGroup.containsKey(ResItem.Drawable) }
    val hasOthers by lazy { (allItemsPerGroup.keys - setOf(ResItem.String, ResItem.Plural, ResItem.Drawable)).isNotEmpty() }
    val hasAnyFiles by lazy { (allItemsPerGroup.keys - setOf(ResItem.String, ResItem.Plural)).isNotEmpty() }

    //Map<Group, Map<Locale, List<Pair<Key, ResItem?>>>>
    val stringsAndPluralsForDb by lazy {
        allItemsPerGroup
            .filterKeys { it == ResItem.String || it == ResItem.Plural }
            .mapValues { (group, itemsPerGroup) ->
                val itemsPerKey = itemsPerGroup
                    .groupBy { it.key }
                    .mapValues { (key, listOfItems) -> listOfItems.associate { item -> item.qualifiers.locale to item } }

                val locales = itemsPerGroup.setOf { it.qualifiers.locale }
                val keys = itemsPerGroup.setOf { it.key }.sorted()

                locales.associateWith { locale ->
                    keys.map { key ->
                        //key necessary for plurals here
                        key to itemsPerKey.getValue(key)[locale]
                    }
                }
            }
    }

    val otherItemsPerGroupPerKey by lazy {
        //counting from 1 to avoid having 0 as valid db key
        var counter = 1
        allItemsPerGroup
            .filterKeys { !(it == ResItem.String || it == ResItem.Plural) }
            .toSortedMap()
            .mapValues { (_, items) ->
                items.groupBy { it.key }.mapKeys { ResItemKeyDbKey(it.key, counter++) }
            }
    }

    @Suppress("UNCHECKED_CAST")
    val stringsForDbs by lazy {
        stringsAndPluralsForDb[ResItem.String]
            ?.let { it as? Map<Locale, List<Pair<String, ResItem.StringRes?>>> }
    }

    //TODO: test
    val stringsDistinctKeys by lazy {
        stringsForDbs?.values?.firstOrNull()?.map { it.first }
    }

    val pluralsPerLocale by lazy {
        stringsAndPluralsForDb[ResItem.Plural]
            ?.let { it as? Map<Locale, List<Pair<String, ResItem.Plurals?>>> }
            ?.mapValues { (locale, plurals) -> plurals.map { (k, v) -> k to (v?.items ?: ResItem.Plurals.EmptyItems) } }
    }

    val pluralsDistinctKeys by lazy {
        pluralsPerLocale?.values?.firstOrNull()?.map { it.first }
    }

    @Suppress("UNCHECKED_CAST")
    val pluralsForDbs = pluralsPerLocale
        ?.mapValues { it.value.map { (key, item) -> item }.flatten() }
}
