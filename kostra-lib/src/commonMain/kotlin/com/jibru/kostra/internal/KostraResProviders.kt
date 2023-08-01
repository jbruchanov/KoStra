package com.jibru.kostra.internal

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.DrawableResourceKey
import com.jibru.kostra.MissionResourceException
import com.jibru.kostra.ResourceContainer
import com.jibru.kostra.ResourceKey
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

@Suppress("UnnecessaryVariable")
internal fun ResourceContainer.resolveResource(qualifiers: Qualifiers): ResourceItem<*> {
    val valueContainer = this.asValueContainer()
    val values = valueContainer.values
    val item = values.filterNotEmpty { it.qualifiers.locale == qualifiers.locale }?.firstOrNull(qualifiers.dpi)
        ?: values.filterNotEmpty { it.qualifiers.locale.equalsLanguage(qualifiers.locale) && it.qualifiers.locale.region == null }?.firstOrNull(qualifiers.dpi)
        ?: values.filterNotEmpty { it.qualifiers.locale == Locale.Undefined }?.firstOrNull(qualifiers.dpi)
        ?: throwMissing(valueContainer.key, "qualifiers", qualifiers)
    return item
}

@Suppress("NOTHING_TO_INLINE")
inline fun throwMissing(key: ResourceKey, type: String, value: Any): Nothing =
    throw MissionResourceException(key, "Unable to resolve value of key:'$key' based on $type:'$value'")

private fun <T> List<T>.filterNotEmpty(predicate: (T) -> Boolean) = filter(predicate).takeIf { it.isNotEmpty() }
private fun List<ResourceItem<Any>>.firstOrNull(dpi: Dpi) =
    firstOrNull { it.qualifiers.dpi == dpi }
        ?: firstOrNull { it.qualifiers.dpi == Dpi.Undefined }
