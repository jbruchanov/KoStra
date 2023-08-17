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

tasks.test {
    useJUnitPlatform()
}

kostra {
    androidResources {
        keyMapperKt = { key, _ -> key.lowercase() }
    }
}
