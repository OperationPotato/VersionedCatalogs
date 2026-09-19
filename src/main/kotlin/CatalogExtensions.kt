package com.operationpotato.catalogs

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider

@Suppress("unused")
object CatalogExtensions {
    val Project.versionedLibs: VersionCatalog
        get() = versionedLibs()

    fun Project.versionedLibs(): VersionCatalog {
        return versionedLibs(project.name)
    }

    fun Project.versionedLibs(version: String): VersionCatalog {
        val key = "versionedLibs${version.replace(".", "")}"
        return extensions.getByType(VersionCatalogsExtension::class.java).find(key)
            .orElseThrow { IllegalArgumentException("Versioned Catalog is missing for '$version' ('$key')") }
    }

    fun VersionCatalog.library(name: String): Provider<MinimalExternalModuleDependency> =
        this.findLibrary(name).orElseThrow { Exception("Failed to find library '${name}' in '${this.name}'") }

    fun VersionCatalog.version(name: String): VersionConstraint =
        this.findVersion(name).orElseThrow { Exception("Failed to find version '${name}' in '${this.name}'") }
}
