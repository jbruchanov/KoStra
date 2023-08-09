package com.jibru.kostra

import com.jibru.kostra.internal.Qualifiers

class MissingResourceException(val key: ResourceKey, val qualifiers: Qualifiers, type: String) : Exception(
    "Unable to find resource key:$key for type:$type based on $qualifiers",
)
