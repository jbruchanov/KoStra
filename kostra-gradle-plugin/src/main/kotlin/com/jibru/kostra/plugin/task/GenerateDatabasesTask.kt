package com.jibru.kostra.plugin.task

import com.jibru.kostra.KLocale
import com.jibru.kostra.database.BinaryDatabase
import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResItemsProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

abstract class GenerateDatabasesTask : DefaultTask() {

    @get:InputFile
    abstract val resourcesAnalysisFile: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val databaseDir: Property<String>

    @get:OutputDirectory
    abstract val outputDir: Property<File>

    init {
        group = KostraPluginConfig.Tasks.Group
    }

    @TaskAction
    fun run() {
        @Suppress("UNCHECKED_CAST")
        val items = ObjectInputStream(FileInputStream(resourcesAnalysisFile.get().asFile)).readObject() as List<ResItem>
        val processor = ResItemsProcessor(items)
        val outDir = outputDir.get()
        val dbDir = databaseDir.orNull?.let { File(outDir, it) } ?: outDir
        dbDir.mkdirs()
        saveDataIntoDb(data = processor.stringsForDbs, "${ResItem.String}-%s.db", dbDir)
        saveDataIntoDb(data = processor.pluralsForDbs, "${ResItem.Plural}-%s.db", dbDir)

        run {
            val db = File(dbDir, "${ResItem.Binary}.db")
            val data = BinaryDatabase().apply { setPairs(processor.otherForDbs) }.save()
            db.writeBytes(data)
        }
    }

    private fun saveDataIntoDb(data: Map<KLocale, List<String?>>, fileNameTemplate: String, location: File) {
        data.forEach { (locale, items) ->
            val tag = if (locale == KLocale.Undefined) "default" else locale.languageRegion
            val db = File(location, fileNameTemplate.format(tag))
            val dbData = BinaryDatabase().apply { setList(items) }.save()
            db.writeBytes(dbData)
        }
    }
}
