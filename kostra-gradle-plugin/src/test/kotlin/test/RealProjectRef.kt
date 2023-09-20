package test

import java.io.File
import java.util.Properties

object RealProjectRef {
    fun isDefined() = resources() != null

    fun resources() = File("../local.properties")
        .takeIf { it.exists() }
        ?.let {
            Properties()
                .apply { load(it.bufferedReader()) }["realProjectResources"]?.toString()
        }
        ?.let { File(it).listFiles()?.filter { v -> v.isDirectory && v.name.startsWith("res") } }
}
