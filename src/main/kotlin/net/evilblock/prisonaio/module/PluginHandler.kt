package net.evilblock.prisonaio.module

import java.io.File

interface PluginHandler {

    fun getModule(): PluginModule

    fun hasInternalData(): Boolean {
        return false
    }

    fun getInternalDataFile(): File {
        throw IllegalStateException("Plugin handler does not have internal data")
    }

    fun initialLoad() {
        if (hasInternalData()) {
            ensureResourceExists(getInternalDataFile())
        }
    }

    fun saveData() {}

    fun ensureResourceExists(file: File) {
        file.parentFile.mkdirs()

        if (!file.exists()) {
            try {
                getModule().getPlugin().saveResource(File("internal", file.name).path, false)
            } catch (e: Exception) {} // ignore
        }
    }

}