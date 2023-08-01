package com.jibru.kostra.internal

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.ResourceKey
import com.jibru.kostra.StringArrayResourceKey
import com.jibru.kostra.StringResourceKey

@Suppress("UNCHECKED_CAST")
interface KostraResProviders {

    fun KostraResources.stringResource(key: StringResourceKey, qualifiers: Qualifiers): ResourceItem<String> {
        val res = strings[key] ?: throw MissionResourceException(key, "Undefined string resource")
        return res.resolveResource(qualifiers) as ResourceItem<String>
    }

    fun KostraResources.pluralResource(key: StringResourceKey, qualifiers: Qualifiers): ResourceItem<String> {
        val res = strings[key] ?: throw MissionResourceException(key, "Undefined string resource")
        return res.resolveResource(qualifiers) as ResourceItem<String>
    }

    fun KostraResources.stringArrayResource(key: StringArrayResourceKey, qualifiers: Qualifiers): ResourceItem<List<String>> {
        val res = stringArrays[key] ?: throw MissionResourceException(key, "Undefined stringArrayResource resource")
        return res.resolveResource(qualifiers) as ResourceItem<List<String>>
    }

    fun KostraResources.painterResource(key: DrawableResourceKey, qualifiers: Qualifiers): ResourceItem<String> {
        val res = drawables[key] ?: throw IllegalArgumentException("Unable to find binary resource for key:'${key.key}'")
        return res.resolveResource(qualifiers) as ResourceItem<String>
    }

    fun KostraResources.binaryResource(key: AssetResourceKey, qualifiers: Qualifiers): ResourceItem<String> {
        return resource(key = key, qualifiers) as ResourceItem<String>
    }

    fun KostraResources.resource(key: AssetResourceKey, qualifiers: Qualifiers): ResourceItem<*> {
        val res = binary[key] ?: drawables[key] ?: throw IllegalArgumentException("Unable to find drawable/binary resource for key:'${key.key}'")
        return res.resolveResource(qualifiers)
    }

    companion object : KostraResProviders
}

class MissionResourceException(val key: ResourceKey, msg: String) : Exception(msg)

internal fun ResourceContainer.resolveResource(qualifiers: Qualifiers): ResourceItem<*> {
    val valueContainer = this.asValueContainer()
    val values = valueContainer.values
    val localeRes = (
        values.filterNotEmpty { it.qualifiers.locale == qualifiers.locale }
            ?: values.filterNotEmpty { it.qualifiers.locale.equalsLanguage(qualifiers.locale) }
            ?: values.filterNotEmpty { it.qualifiers.locale == Locale.Undefined }
        )

    //TODO display density
    return localeRes?.get(0)
        ?: throw MissionResourceException(valueContainer.key, "Unable to resolve value of key:'${valueContainer.key}' based on qualifiers:'$qualifiers'")
}

private fun <T> List<T>.filterNotEmpty(predicate: (T) -> Boolean) = filter(predicate).takeIf { it.isNotEmpty() }
