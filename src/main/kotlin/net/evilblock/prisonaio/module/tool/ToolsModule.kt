/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.command.*
import net.evilblock.prisonaio.module.tool.enchant.command.admin.*
import net.evilblock.prisonaio.module.tool.enchant.command.parameter.AbstractEnchantParameterType
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.command.PrestigeEditorCommand
import net.evilblock.prisonaio.module.tool.enchant.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.tool.enchant.salvage.command.SalvagePreventionEditorCommand
import net.evilblock.prisonaio.module.tool.pickaxe.command.*
import net.evilblock.prisonaio.module.tool.pickaxe.listener.PickaxeStatisticsListeners
import org.bukkit.event.Listener

object ToolsModule : PluginModule() {

    override fun getName(): String {
        return "Tools"
    }

    override fun getConfigFileName(): String {
        return "tools"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        PickaxeHandler.initialLoad()
        PickaxePrestigeHandler.initialLoad()
        SalvagePreventionHandler.initialLoad()
        EnchantsManager.loadConfig()
    }

    override fun onReload() {
        super.onReload()
    }

    override fun onDisable() {
        PickaxeHandler.saveData()
        PickaxePrestigeHandler.saveData()
        SalvagePreventionHandler.saveData()
    }

    override fun onAutoSave() {
        PickaxeHandler.saveData()
        PickaxePrestigeHandler.saveData()
        SalvagePreventionHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            EnchantsManager,
            PickaxeStatisticsListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BookCommand.javaClass,
            ForgetPickaxeCommand.javaClass,
            ManageEnchantsCommand.javaClass,
            EnchantCommand.javaClass,
            PickaxeCommand.javaClass,
            PickaxeDebugCommand.javaClass,
            PickaxeSetBlocksMinedCommand.javaClass,
            PickaxeSetLevelCommand.javaClass,
            PickaxeSetPrestigeCommand.javaClass,
            RemoveEnchantCommand.javaClass,
            ToggleEnchantsMessagesCommand.javaClass,
            SalvagePreventionEditorCommand.javaClass,
            PrestigeEditorCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            AbstractEnchant::class.java to AbstractEnchantParameterType
        )
    }

    fun readTokenShopCommand(): String {
        return config.getString("token-shop-command", "openshop tokens")
    }

}