package test

import com.jibru.kostra.BinaryResourceKey
import com.jibru.kostra.PainterResourceKey
import com.jibru.kostra.PluralResourceKey
import com.jibru.kostra.StringResourceKey
import kotlin.jvm.JvmInline

@JvmInline
value class SKey(override val key: Int) : StringResourceKey

@JvmInline
value class PKey(override val key: Int) : PluralResourceKey

@JvmInline
value class DKey(override val key: Int) : PainterResourceKey

@JvmInline
value class BKey(override val key: Int) : BinaryResourceKey
