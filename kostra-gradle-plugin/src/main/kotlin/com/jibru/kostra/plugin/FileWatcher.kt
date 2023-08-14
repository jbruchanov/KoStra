package com.jibru.kostra.plugin

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

class FileWatcher(
    private val pollTimeOutMs: Long = 50L,
) {
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
                .filter { Files.isDirectory(it) }
                .forEach {
                    it.register(
                        watcher,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                    )
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
                val filename = ev.context()

                if (kind === StandardWatchEventKinds.OVERFLOW) {
                    continue
                } else if (kind === StandardWatchEventKinds.ENTRY_MODIFY) {
                    send(filename)
                }
                val valid = key.reset()
                if (!valid) {
                    break
                }
            }
            yield()
        }
    }
}
