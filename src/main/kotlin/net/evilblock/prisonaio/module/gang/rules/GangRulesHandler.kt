/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.rules

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.gang.GangsModule
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object GangRulesHandler : PluginHandler() {

    val rulesFile = File(File(getModule().getPluginFramework().dataFolder, "internal"), "gang-rules.json")
    val rules: MutableSet<GangRule> = ConcurrentHashMap.newKeySet()

    val menuTemplateFile = File(File(getModule().getPluginFramework().dataFolder, "internal"), "gang-rules-menu-template.json")
    var menuTemplate: MenuTemplate<GangRule>? = null

    override fun getModule(): PluginModule {
        return GangsModule
    }

    override fun getInternalDataFile(): File {
        return rulesFile
    }

    override fun initialLoad() {
        super.initialLoad()

        rulesFile.parentFile.mkdirs()
        if (rulesFile.exists()) {
            try {
                Files.newReader(rulesFile, Charsets.UTF_8).use { reader ->
                    val set = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<Set<GangRule>>() {}.type) as Set<GangRule>
                    for (rule in set) {
                        rules.add(rule)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        menuTemplateFile.parentFile.mkdirs()
        if (menuTemplateFile.exists()) {
            try {
                Files.newReader(rulesFile, Charsets.UTF_8).use { reader ->
                    menuTemplate = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<MenuTemplate<GangRule>>() {}.type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(rules, object : TypeToken<Set<GangRule>>() {}.type), rulesFile, Charsets.UTF_8)

        if (menuTemplate != null) {
            Files.write(Cubed.gson.toJson(menuTemplate, object : TypeToken<MenuTemplate<GangRule>>() {}.type), menuTemplateFile, Charsets.UTF_8)
        }
    }

}