package com.jibru.kostra

import kotlin.native.concurrent.ThreadLocal

expect fun defaultQualifiers(): KQualifiers

interface IDefaultQualifiersProvider {
    fun get(): KQualifiers
}

@ThreadLocal
object DefaultQualifiersProvider : IDefaultQualifiersProvider {
    var delegate: IDefaultQualifiersProvider? = null

    override fun get(): KQualifiers = delegate?.get() ?: defaultQualifiers()
}
