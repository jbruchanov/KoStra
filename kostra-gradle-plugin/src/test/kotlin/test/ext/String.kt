package test.ext

fun String.trimIndentTestResults() = trimIndent().replace("${'$'}kostra", "\${'$'}kostra")
