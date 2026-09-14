package com.operationpotato.catalogs

import org.gradle.api.Plugin
import org.gradle.api.file.FileCollection
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logging
import java.io.File

class VersionedCatalogsPlugin : Plugin<Settings> {
    private val logger = Logging.getLogger(VersionedCatalogsPlugin::class.java)

    override fun apply(target: Settings) {
        val extension = target.extensions.create("versionedCatalogs", VersionedCatalogsSettings::class.java).apply {
            versions.unsetConvention()
            automaticallyDiscover.convention(true)
        }

        target.gradle.settingsEvaluated { afterEvaluation(target, extension) }
    }

    fun afterEvaluation(target: Settings, extension: VersionedCatalogsSettings) {
        val gradleDir = target.rootDir.resolve("gradle")
        if (!gradleDir.isDirectory) {
            logger.error("[Versioned Catalogs] No 'gradle' directory found!")
            return
        }

        val versions = if (extension.versions.isPresent) {
            logger.info("[Versioned Catalogs] Using given list of versions.")
            extension.versions.get()
        } else if (extension.automaticallyDiscover.get()) {
            logger.info("[Versioned Catalogs] Automatically collecting versions.")
            getAllVersions(gradleDir)
        } else {
            logger.error("[Versioned Catalogs] Set `versions` or enable `automaticallyDiscover`")
            emptyList()
        }

        if (versions.isEmpty()) {
            logger.error("[Versioned Catalogs] No versions found!")
            return
        }

        logger.info("[Versioned Catalogs] Creating catalogs for ${versions.size} versions.")
        target.dependencyResolutionManagement.versionCatalogs { catalogs ->
            versions.forEach { version ->
                val catalogName = "versionedLibs${version.replace(".", "")}"
                val fileName = "${version.replace(".", "_")}.versions.toml"

                val file = gradleDir.resolve(fileName)
                if (!file.exists()) {
                    logger.error("[Versioned Catalogs] Catalog for '$version' ('$fileName') was not found!")
                    return@forEach
                }

                catalogs.create(catalogName) { catalog ->
                    catalog.from(getFileCollection(target, gradleDir.resolve(fileName)))
                }
            }
        }
    }

    // jank but this is the easiest way
    @Suppress("UnstableApiUsage")
    fun getFileCollection(target: Settings, file: File): FileCollection {
        return target.layout.rootDirectory.files(file)
    }

    fun getAllVersions(gradleDir: File): List<String> = gradleDir.listFiles {
        it.isFile && it.name.endsWith(".versions.toml") && it.name != "libs.versions.toml"
    }?.mapNotNull { it.name.removeSuffix(".versions.toml").replace("_", ".") }?.toList() ?: emptyList()
}
