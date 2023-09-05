package com.jibru.kostra.plugin.task

import com.jibru.kostra.KLocale
import com.jibru.kostra.database.BinaryDatabase
import com.jibru.kostra.plugin.KostraPluginConfig
import com.jibru.kostra.plugin.ResItem
import com.jibru.kostra.plugin.ResItemsProcessor
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateDatabasesTask : DefaultTask() {

    @get:InputFile
    abstract val resources: RegularFileProperty

    @get:OutputDirectory
    abstract val output: Property<File>

    init {
        group = KostraPluginConfig.Tasks.Group
    }

    @TaskAction
    fun run() {
        @Suppress("UNCHECKED_CAST")
        val items = ObjectInputStream(FileInputStream(resources.get().asFile)).readObject() as List<ResItem>
        val processor = ResItemsProcessor(items)
        val root = output.get()
        root.mkdirs()

        saveDataIntoDb(data = processor.stringsForDbs, "${ResItem.String}-%s.db", root)
        saveDataIntoDb(data = processor.pluralsForDbs, "${ResItem.Plural}-%s.db", root)

        run {
            val db = File(root, "${ResItem.Binary}.db")
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
