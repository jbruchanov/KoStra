plugins {
    id("org.jetbrains.kotlin.jvm").version(libs.versions.kotlin)
    id("application")
    id("com.jibru.kostra.resources")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmtarget.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jvmtarget.get())
}

sourceSets {
    main {
        resources {
            //just to reuse same resources
            srcDir("../shared/src/commonMain/resources")
        }
    }
}

kotlin {
    jvmToolchain(libs.versions.jvmtarget.get().toInt())
}

application {
    mainClass.set("com.jibru.kostra.appsample.jvm.KtAppKt")
}

dependencies {
    implementation(libs.kostra.common)
}

kostra {
    androidResources {
        keyMapperKt.set { key, _ -> key.toCamelCase() }
        resourceDirs.add(file("../shared/src/commonMain/resources_strings"))
    }
}

fun String.toCamelCase(): String = split("_")
    .map { it.lowercase() }
    .filter { it.isNotEmpty() }
    .takeIf { it.isNotEmpty() }
    ?.mapIndexed { index, s -> if (index > 0) s[0].uppercase() + s.drop(1) else s }
    ?.joinToString(separator = "")
    //just return anything original, this is naive toCamelCase implementation
    ?: this
