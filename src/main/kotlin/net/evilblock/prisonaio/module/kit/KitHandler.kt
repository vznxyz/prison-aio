/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.kit

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.kits.Kit
import net.evilblock.prisonaio.module.kit.menu.template.PublicKitsTemplate
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object KitHandler : PluginHandler() {

    private val kitsDataFile: File = File(File(getModule().getPluginFramework().dataFolder, "internal"), "kits.json")
    private val kits: MutableMap<String, Kit> = ConcurrentHashMap()

    private lateinit var template: PublicKitsTemplate
    private val templateDataFile: File = File(File(getModule().getPluginFramework().dataFolder, "internal"), "template.json")

    override fun getModule(): PluginModule {
        return KitsModule
    }

    override fun initialLoad() {
        kitsDataFile.parentFile.mkdirs()
        templateDataFile.parentFile.mkdirs()

        if (kitsDataFile.exists()) {
            Files.copy(kitsDataFile, BackupHandler.findNextBackupFile("kits"))

            Files.newReader(kitsDataFile, Charsets.UTF_8).use { reader ->
                val listType = object : TypeToken<List<Kit>>() {}.type
                val list = Cubed.gson.fromJson(reader, listType) as List<Kit>

                for (kit in list) {
                    trackKit(kit)
                }
            }
        }

        if (templateDataFile.exists()) {
            Files.newReader(templateDataFile, Charsets.UTF_8).use { reader ->
                template = Cubed.gson.fromJson(reader, object : TypeToken<PublicKitsTemplate>() {}.type) as PublicKitsTemplate
            }
        } else {
            template = PublicKitsTemplate()
        }
    }

    override fun saveData() {
        if (!loaded) {
            return
        }

        try {
            Files.write(Cubed.gson.toJson(kits.values), kitsDataFile, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            getModule().getPluginFramework().logger.severe("Failed to save kits.json!")
        }

        try {
            Files.write(Cubed.gson.toJson(template), templateDataFile, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            getModule().getPluginFramework().logger.severe("Failed to save template.json!")
        }
    }

    fun getKits(): List<Kit> {
        return kits.values.toList()
    }

    fun getKitById(id: String): Kit? {
        return kits[id.toLowerCase()]
    }

    fun trackKit(kit: Kit) {
        kits[kit.id.toLowerCase()] = kit
    }

    fun forgetKit(kit: Kit) {
        kits.remove(kit.id.toLowerCase())
    }

    fun getMenuTemplate(): PublicKitsTemplate {
        return template
    }

    fun resetMenuTemplate() {
        template = PublicKitsTemplate()
    }

}