package net.evilblock.prisonaio.module.privatemine

import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.privatemine.command.CreateCommand
import net.evilblock.prisonaio.module.privatemine.command.HelpCommand
import net.evilblock.prisonaio.module.privatemine.command.KickCommand
import net.evilblock.prisonaio.module.privatemine.command.MenuCommand
import net.evilblock.prisonaio.module.privatemine.task.PrivateMineResetTask
import net.evilblock.prisonaio.module.privatemine.data.PrivateMineTier
import net.evilblock.prisonaio.module.privatemine.listener.*
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object PrivateMinesModule : PluginModule() {

    override fun getName(): String {
        return "PrivateMines"
    }

    override fun getConfigFileName(): String {
        return "private-mines"
    }

    override fun onEnable() {
        PrivateMineTier.initialLoad()
        PrivateMineHandler.initialLoad()

        getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), PrivateMineResetTask, 20L, 20L)
    }

    override fun onAutoSave() {
        PrivateMineHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CreateCommand.javaClass,
            KickCommand.javaClass,
            HelpCommand.javaClass,
            MenuCommand.javaClass
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

    fun getMenuTitle(menu: String): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("language.menu.$menu.title"))
    }

    fun getButtonTitle(menu: String, button: String): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("language.menu.$menu.buttons.$button.title"))
    }

    fun getButtonLore(menu: String, button: String): List<String> {
        return config.getStringList("language.menu.$menu.buttons.$button.lore").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

}