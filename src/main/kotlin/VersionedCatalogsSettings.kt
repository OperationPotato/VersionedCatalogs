package com.operationpotato.catalogs

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface VersionedCatalogsSettings {
    val automaticallyDiscover: Property<Boolean>
    val versions: ListProperty<String>
}
