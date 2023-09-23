package test

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.KQualifiers
import com.jibru.kostra.internal.FileDatabase

class FileMemoryReferences(
    internal val data: Map<KQualifiers, Map<AssetResourceKey, String>>,
) : FileDatabase("mem") {
    override fun getValue(key: AssetResourceKey, qualifiers: KQualifiers): String? {
        return data[qualifiers]?.get(key).also {
            println("FileMemoryReferences.getValue key:$key, qualifiers:$qualifiers, result:$it")
        }
    }
}
