# Versioned Catalogs
`libs.versions.toml` but for multi-version.

## Setup
```kts
// settings.gradle.kts
pluginManagement {
    repositories {
        // add Maven repository
        maven("https://maven.operationpotato.com/releases")
    }
}

plugins {
    // add plugin
    id("com.operationpotato.versioned-catalogs") version "1.0.0"
}

// Optional Configuration
versionedCatalogs {
    // if no versions are specified, they are automatically found from the `gradle/` folder.
    versions.addAll("26.1", "26.2")
}
```

```kts
// build.gradle.kts
import com.operationpotato.catalogs.CatalogExtensions.versionedLibs
import com.operationpotato.catalogs.CatalogExtensions.library
import com.operationpotato.catalogs.CatalogExtensions.version

dependencies {
    // for dependencies:
    implementation(versionedLibs.library("fabric.api"))
    // note: this uses the project name, which should be the Minecraft version
    // ...
}

tasks.processResources {
    // for versions:
    inputs.property("fabric-api", versionedLibs.version("fabric-api"))
    // ...
}
```

Create a `<version>.versions.toml` for each version you plan to use.
* `<version>` should be escaped, i.e. `26.1` -> `26_1`
* Each file must define all the dependencies used.

```toml
# 26_2.versions.toml
[versions]
fabric-api = "0.160.0+26.2"

[libraries]
fabric-api = { module = "net.fabricmc.fabric-api:fabric-api", version.ref = "fabric-api" }
```

```toml
# 26_1.versions.toml
[versions]
fabric-api = "0.155.3+26.1.2"

[libraries]
fabric-api = { module = "net.fabricmc.fabric-api:fabric-api", version.ref = "fabric-api" }
```
