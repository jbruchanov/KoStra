package com.jibru.kostra

import kotlin.native.concurrent.ThreadLocal

expect fun defaultQualifiers(): KQualifiers

interface IDefaultQualifiersProvider {
    val current: KQualifiers
}

@ThreadLocal
object DefaultQualifiersProvider : IDefaultQualifiersProvider {
    var delegate: IDefaultQualifiersProvider? = null

    override val current: KQualifiers get() = delegate?.current ?: defaultQualifiers()
}
