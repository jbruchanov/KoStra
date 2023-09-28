package com.jibru.kostra.plugin.ext

import com.squareup.kotlinpoet.ClassName
import kotlin.reflect.KClass

internal fun KClass<*>.asLocalResourceType(packageName: String): ClassName = ClassName(packageName, this.simpleName!!)
