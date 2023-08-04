package test

object IOTestTools {

    fun requireTestInputStream(file: String) = javaClass.classLoader?.getResourceAsStream(file)!!

    fun readTextFile(file: String) = requireTestInputStream(file).reader().readText()
}
