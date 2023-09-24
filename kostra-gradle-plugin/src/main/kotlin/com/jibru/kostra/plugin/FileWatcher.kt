package com.jibru.kostra.plugin

import com.jibru.kostra.plugin.ext.appendLog
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.util.concurrent.TimeUnit
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

class FileWatcher(
    private val pollTimeOutMs: Long = 50L,
    private val stringsRegexps: Collection<Regex>,
    private val log: File? = null,
) {

    private val watchKeys = mutableMapOf<Path, WatchKey>()

    suspend fun flowChanges(folders: List<File>) = callbackFlow<Path> {
        val watcher = FileSystems.getDefault().newWatchService()
        folders.forEach { folder ->
            val path = folder.toPath()
            path.register(
                watcher,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
            )
            Files.walk(path)
                .forEach { path ->
                    if (path.isDirectory()) {
                        log?.appendLog("FileWatcher: regPath:$path")
                        watchKeys[path] = path.register(
                            watcher,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                        )
                    } else if (path.isRegularFile() && stringsRegexps.any { it.matches(path.fileName.name) }) {
                        log?.appendLog("FileWatcher: regPath:$path")
                        //this won't probably work when new strings file is added
                        watchKeys[path] = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
                    }
                }
        }

        while (isActive) {
            val key: WatchKey?
            try {
                key = watcher.poll(pollTimeOutMs, TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                continue
            }

            if (key == null) {
                yield()
                continue
            }

            for (event in key.pollEvents()) {
                val kind = event.kind()

                @Suppress("UNCHECKED_CAST")
                val ev = event as WatchEvent<Path>
                val path = ev.context()

                when {
                    kind === StandardWatchEventKinds.OVERFLOW -> continue
                    kind === StandardWatchEventKinds.ENTRY_MODIFY -> {
                        log?.appendLog("FileWatcher: modify:'$path'")
                        send(path)
                    }

                    kind == StandardWatchEventKinds.ENTRY_CREATE && path.isStringXmlFile() -> {
                        log?.appendLog("FileWatcher: regPath:$path")
                        watchKeys[path] = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
                    }

                    kind == StandardWatchEventKinds.ENTRY_DELETE && path.isStringXmlFile() -> {
                        log?.appendLog("FileWatcher: cancel regPath:$path")
                        watchKeys[path]?.cancel()
                        watchKeys.remove(path)
                    }
                }
                val valid = key.reset()
                if (!valid) {
                    break
                }
            }
            yield()
        }
    }

    private fun Path.isStringXmlFile() = isRegularFile() && stringsRegexps.any { it.matches(fileName.name) }
}
