package com.jibru.kostra

class MissingResourceException(val key: ResourceKey, val qualifiers: Qualifiers, type: String) : Exception(
    "Unable to find resource key:$key for type:$type based on $qualifiers",
)

class UnableToOpenResourceStream(val path: String) : Exception(path)
