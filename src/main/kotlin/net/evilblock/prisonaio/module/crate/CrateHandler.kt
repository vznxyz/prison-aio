/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import java.io.File

object CrateHandler : PluginHandler {

    private val crates = hashMapOf<String, Crate>()

    override fun getModule(): PluginModule {
        return CratesModule
    }

    override fun hasInternalData(): Boolean {
        return true
    }

    override fun getInternalDataFile(): File {
        return File(File(PrisonAIO.instance.dataFolder, "internal"), "crates.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        if (getInternalDataFile().exists()) {
            Files.newReader(getInternalDataFile(), Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Crate>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Crate>

                for (crate in list) {
                    crates[crate.id.toLowerCase()] = crate
                }
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(crates.values), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getCrates(): List<Crate> {
        return crates.values.toList()
    }

    fun findCrate(id: String): Crate? {
        return crates[id.toLowerCase()]
    }

    fun trackCrate(crate: Crate) {
        crates[crate.id.toLowerCase()] = crate
    }

    fun forgetCrate(crate: Crate) {
        crates.remove(crate.id)
    }

}