package com.jibru.kostra.plugin

import com.jibru.kostra.KQualifiers
import com.jibru.kostra.plugin.ext.distinctByLast
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.streams.asSequence
import kotlin.streams.asStream

data class FileResolverConfig(
    val keyMapper: (String, File) -> String = { key, _ -> key },
    val stringFiles: Set<Regex> = setOf("string.*\\.xml".toRegex()),
    val drawableGroups: Set<Regex> = setOf("drawables?.*".toRegex(), "mipmap?.*".toRegex(), "image?.*".toRegex()),
    val drawableExtensions: Set<String> = setOf("jpg", "jpeg", "png", "webp", "bmp", "xml"),
    val useOnlyFilesWithSize: Boolean = true,
    val parallelism: Boolean = false,
    val strictLocale: Boolean = true,
) {
    companion object {
        val Defaults = FileResolverConfig()
    }
}

class FileResolver(
    private val config: FileResolverConfig = FileResolverConfig(),
    private val androidResourcesXmlParser: AndroidResourcesXmlParser = AndroidResourcesXmlParser(config.keyMapper),
) {

    private val logger = LoggerFactory.getLogger(FileResolver::class.java)
    private val tag = "FileResolver"

    fun resolve(root: File): List<ResItem> = resolve(listOf(root))

    fun resolve(roots: List<File>): List<ResItem> {
        val resources = roots
            .tryParallelStream()
            .map { resolveImpl(it) }
            .asSequence()
            .toList()
            .flatten()
            .distinctByLast { it.distinctKey }

        return resources
    }

    //extra resolveImpl due to ^ distinctByLast per root, not per file
    private fun resolveImpl(resourcesRoot: File): List<ResItem> {
        //sorting seems to be necessary on mac
        val listFiles = resourcesRoot.listFiles()?.sorted() ?: return emptyList()

        val folders = fileGroups(listFiles) { it.isDirectory }
        val filesInRoot = fileGroups(listFiles) { it.isFile }.map { it.first to it.second.copy(group = "root") }
        val items = (folders + filesInRoot)
            //group values by the group, drawable/string etc
            .groupBy { it.second.group }
            //resolve particular files, simple image, strings etc
            .mapValues { items ->
                items.value
                    .tryParallelStream()
                    .map { (folder, groupQualifiers) -> resolve(folder, resourcesRoot, groupQualifiers) }
                    .asSequence()
                    .toList()
            }
            //returns ^ list of lists as it might be xml of strings
            .mapValues { it.value.flatten() }
            .values
            //flatten all items
            .flatten()

        return items
    }

    private fun fileGroups(listFiles: List<File>, predicate: (File) -> Boolean) = listFiles.filter(predicate)
        .asSequence()
        //parse group + qualifiers
        .map { it to it.groupQualifiers(anyLocale = !config.strictLocale) }

    private fun resolve(rootFolder: File, resRoot: File, groupQualifiers: GroupQualifiers): List<ResItem> = with(config) {
        val (rootGroup, rootQualifiers) = groupQualifiers
        val items = rootFolder.walkTopDown()
            .tryParallelStream()
            .filter { it.isFile && (!useOnlyFilesWithSize || it.length() > 0) }
            //ignore any unix hidden files starting with "."
            .filter { !it.name.startsWith(".") }
            .map { file ->
                var name = file.name
                val ext = file.ext()
                val fileQualifiers = file.findGroupQualifiers(file.parentFile)
                    ?.also { name = it.group }
                    ?.qualifiers
                val subPathQualifiers = file.parentFile.findGroupQualifiers(rootFolder)?.qualifiers
                val qualifiers = fileQualifiers ?: subPathQualifiers ?: rootQualifiers

                val key = keyMapper(name.let { if (ext.isNotEmpty()) it.substringBefore(".$ext") else it }, file)
                    .also { check(it.isNotEmpty()) { "Key must not be empty, file:'$file'" } }

                when {
                    stringFiles.any { regex -> regex.matches(file.name.lowercase()) } -> {
                        check(qualifiers.hasOnlyLocale) { "Only locale qualifiers allowed for strings, file:${file.absolutePath}, qualifiers:$qualifiers" }
                        androidResourcesXmlParser.findStrings(file, qualifiers)
                    }

                    drawableGroups.any { regex -> regex.matches(rootGroup) } && drawableExtensions.contains(ext.lowercase()) ->
                        listOf(ResItem.FileRes(key = key, file = file, root = resRoot, qualifiersKey = qualifiers.key, group = ResItem.Drawable))

                    else -> listOf(ResItem.FileRes(key = key, file = file, root = resRoot, qualifiersKey = qualifiers.key, group = rootGroup))
                }.also {
                    logger.info("[$tag]: ${file.absolutePath} => ${it.joinToString()}")
                }
            }
            .asSequence()
            .toList()
            .flatten()

        return items
    }

    private fun <T> Collection<T>.tryParallelStream() = if (config.parallelism) parallelStream() else stream()

    private fun <T> Sequence<T>.tryParallelStream() = if (config.parallelism) asStream().parallel() else asStream()

    private fun File.findGroupQualifiers(until: File): GroupQualifiers? {
        if (!absolutePath.contains(until.absolutePath)) return null
        var thisFile = this
        while (thisFile != until) {
            val groupQualifiers = thisFile.groupQualifiers(anyLocale = !config.strictLocale)
            if (groupQualifiers.qualifiers != KQualifiers.Undefined) {
                return groupQualifiers
            }
            thisFile = thisFile.parentFile
        }
        return null
    }
}
