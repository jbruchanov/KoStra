package test

import java.io.File

interface IOTestTools {

    fun requireTestInputStream(file: String) = javaClass.classLoader?.getResourceAsStream(file)!!

    fun readTextFile(file: String) = requireTestInputStream(file).reader().readText()

    fun testResourceFile(name: String) = File("src/test/resources/", name)

    companion object : IOTestTools
}
