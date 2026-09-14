plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradle.plugin)
    alias(libs.plugins.maven.publish)
}

group = "com.operationpotato"
version = "1.0.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    vcsUrl = "https://github.com/OperationPotato/VersionedCatalogs"
    plugins {
        create("plugin") {
            id = "com.operationpotato.versioned-catalogs"
            implementationClass = "com.operationpotato.catalogs.VersionedCatalogsPlugin"
        }
    }
}

kotlin {
    jvmToolchain(25)
}

publishing {
    val isFullRelease = System.getenv("IS_FULL_RELEASE") == "true"
    repositories {
        maven {
            val repo = if (isFullRelease) "releases" else "snapshots"
            url = uri("https://maven.operationpotato.com/$repo")
            name = "OperationPotatoMaven"
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_TOKEN")
            }
        }
    }
}
