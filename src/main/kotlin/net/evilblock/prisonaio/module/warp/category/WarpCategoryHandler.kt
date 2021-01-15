/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.warp.category

import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.template.menu.TemplateMenu
import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.warp.WarpsModule
import net.evilblock.prisonaio.module.warp.category.template.CategoriesTemplateHandler
import org.bukkit.entity.Player
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object WarpCategoryHandler : PluginHandler() {

    private val categories: MutableMap<String, WarpCategory> = ConcurrentHashMap()

    override fun getModule(): PluginModule {
        return WarpsModule
    }

    override fun getInternalDataFile(): File {
        return File(File(getModule().getPluginFramework().dataFolder, "internal"), "warp-categories.json")
    }

    override fun initialLoad() {
        super.initialLoad()

        val dataFile = getInternalDataFile()
        if (dataFile.exists()) {
            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val categories = Cubed.gson.fromJson(reader.readLine(), object : TypeToken<Set<WarpCategory>>() {}.type) as Set<WarpCategory>
                for (category in categories) {
                    trackCategory(category)
                }
            }
        }

        loaded = true
    }

    override fun saveData() {
        super.saveData()

        Files.write(Cubed.gson.toJson(categories.values, object : TypeToken<Set<WarpCategory>>() {}.type), getInternalDataFile(), Charsets.UTF_8)
    }

    fun getCategories(): Collection<WarpCategory> {
        return categories.values
    }

    fun getCategoryById(id: String): WarpCategory? {
        return categories[id.toLowerCase()]
    }

    fun trackCategory(category: WarpCategory) {
        categories[category.id.toLowerCase()] = category
    }

    fun forgetCategory(category: WarpCategory) {
        categories.remove(category.id.toLowerCase())
    }

    fun openMenu(player: Player) {
        TemplateMenu(CategoriesTemplateHandler.template).openMenu(player)
    }

}