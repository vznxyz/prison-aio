/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category.template

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.warp.WarpsModule
import java.io.File

object CategoriesTemplateHandler : PluginHandler() {

    var template: CategoriesMenuTemplate = CategoriesMenuTemplate()

    override fun getModule(): PluginModule {
        return WarpsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "warps-menu-template.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                try {
                    template = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<CategoriesMenuTemplate>() {}.type) as CategoriesMenuTemplate
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(template, object : TypeToken<CategoriesMenuTemplate>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

}