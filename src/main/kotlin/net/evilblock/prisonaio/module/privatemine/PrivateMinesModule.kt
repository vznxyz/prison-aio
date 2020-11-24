/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.privatemine.command.*
import net.evilblock.prisonaio.module.privatemine.service.ResetMineService
import net.evilblock.prisonaio.module.privatemine.listener.*
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object PrivateMinesModule : PluginModule() {

    override fun getName(): String {
        return "PrivateMines"
    }

    override fun getConfigFileName(): String {
        return "private-mines"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        PrivateMineHandler.initialLoad()

        ServiceRegistry.register(ResetMineService, 20L)
    }

    override fun onReload() {
        super.onReload()

        PrivateMineConfig.load()
    }

    override fun onAutoSave() {
        PrivateMineHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CreateCommand.javaClass,
            KickCommand.javaClass,
            HelpCommand.javaClass,
            MenuCommand.javaClass,
            ResetCommand.javaClass
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            PrivateMineWorldListeners,
            PrivateMineInventoryListeners,
            PrivateMineShopListeners,
            PrivateMineInternalDataListeners
        )
    }

    fun getGridWorldName(): String {
        return config.getString("grid.world-name")
    }

    fun getGridGutter(): Int {
        return config.getInt("grid.gutter")
    }

    fun getGridDimensions(): Int {
        return config.getInt("grid.dimensions")
    }

    fun getNotificationLines(type: String): List<String> {
        return config.getStringList("language.notifications.$type").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

}