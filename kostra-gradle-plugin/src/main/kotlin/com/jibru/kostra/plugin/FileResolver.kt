package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.ext.distinctByLast
import java.io.File
import kotlin.streams.asStream

data class FileResolverConfig(
    val keyMapper: (String, File) -> String = { key, _ -> key },
    val stringFiles: Set<Regex> = setOf("string.*\\.xml".toRegex()),
    val drawableGroups: Set<Regex> = setOf("drawables?.*".toRegex(), "mipmap?.*".toRegex()),
    val drawableExtensions: Set<String> = setOf("jpeg", "jpeg", "png", "webp", "bmp", "xml"),
    val useOnlyFilesWithSize: Boolean = true,
    val parallelism: Boolean = false,
)

class FileResolver(
    private val config: FileResolverConfig = FileResolverConfig(),
    private val androidResourcesXmlParser: AndroidResourcesXmlParser = AndroidResourcesXmlParser(config.keyMapper),
) {
    fun resolve(root: File): List<ResItem> = resolve(listOf(root))
    fun resolve(roots: List<File>): List<ResItem> {
        val resources = roots
            .tryParallelStream()
            .map { resolveImpl(it) }
            .toList()
            .flatten()
            .distinctByLast { it.distinctKey }

        return resources
    }

    //extra resolveImpl due to ^ distinctByLast per root, not per file
    private fun resolveImpl(resourcesRoot: File): List<ResItem> {
        //sorting seems to be necessary on mac
        val listFiles = resourcesRoot.listFiles()?.sorted() ?: return emptyList()
        val items = listFiles.filter { it.isDirectory }
            .asSequence()
            //parse group + qualifiers
            .map { it to it.groupQualifiers() }
            //group values by the group, drawable/string etc
            .groupBy { it.second.group }
            //resolve particular files, simple image, strings etc
            .mapValues { items ->
                items.value
                    .tryParallelStream()
                    .map { (folder, groupQualifiers) -> resolve(folder, resourcesRoot, groupQualifiers) }
                    .toList()
            }
            //returns ^ list of lists as it might be xml of strings
            .mapValues { it.value.flatten() }
            .values
            //flatten all items
            .flatten()

        return items
    }

    private fun resolve(rootFolder: File, resRoot: File, groupQualifiers: GroupQualifiers): List<ResItem> = with(config) {
        val (group, qualifiers) = groupQualifiers
        val files = rootFolder.walkTopDown()
            .tryParallelStream()
            .filter { !useOnlyFilesWithSize || (it.isFile && it.length() > 0) }
            .map { file ->
                val name = file.name
                val ext = file.ext()
                val key = keyMapper(name.let { if (ext.isNotEmpty()) it.substringBefore(".$ext") else it }, file)
                when {
                    stringFiles.any { regex -> regex.matches(name.lowercase()) } -> androidResourcesXmlParser.findStrings(file, qualifiers)

                    drawableGroups.any { regex -> regex.matches(group) } && drawableExtensions.contains(ext.lowercase()) ->
                        listOf(ResItem.FileRes(key = key, file = file, root = resRoot, qualifiers = qualifiers, group = ResItem.Drawable))

                    else -> listOf(ResItem.FileRes(key = key, file = file, root = resRoot, qualifiers = qualifiers, group = group))
                }
            }
            .toList()
            .flatten()

        return files
    }

    private fun <T> Collection<T>.tryParallelStream() = if (config.parallelism) parallelStream() else stream()
    private fun <T> Sequence<T>.tryParallelStream() = if (config.parallelism) asStream().parallel() else asStream()
}
