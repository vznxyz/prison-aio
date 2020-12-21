package net.evilblock.prisonaio.module.kit

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.kits.Kit
import net.evilblock.kits.command.KitCommand
import net.evilblock.prisonaio.module.kit.command.KitsCommand
import net.evilblock.kits.command.parameter.KitParameterType
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.kit.command.admin.*
import org.bukkit.ChatColor

object KitsModule : PluginModule() {

    override fun getName(): String {
        return "Kits"
    }

    override fun getConfigFileName(): String {
        return "kits"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        super.onEnable()

        KitHandler.initialLoad()
    }

    override fun onDisable() {
        super.onDisable()

        KitHandler.saveData()
    }

    override fun onAutoSave() {
        super.onAutoSave()

        KitHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            EditorCommand.javaClass,
            CreateCommand.javaClass,
            GiveCommand.javaClass,
            LoadCommand.javaClass,
            SaveCommand.javaClass,
            ResetCooldownCommand.javaClass,
            KitCommand.javaClass,
            KitsCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(Kit::class.java to KitParameterType)
    }


    fun getChatPrefix(): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix"))
    }

    fun getMenuTitle(): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("menu-title"))
    }

}