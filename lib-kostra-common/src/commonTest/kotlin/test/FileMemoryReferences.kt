package test

import com.jibru.kostra.AssetResourceKey
import com.jibru.kostra.Qualifiers
import com.jibru.kostra.internal.FileDatabase

class FileMemoryReferences(
    private val data: Map<Qualifiers, Map<AssetResourceKey, String>>,
) : FileDatabase("mem") {
    override fun getValue(key: AssetResourceKey, qualifiers: Qualifiers): String? {
        return data[qualifiers]?.get(key).also {
            println("FileMemoryReferences.getValue key:$key, qualifiers:$qualifiers, result:$it")
        }
    }
}
