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
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.enchant.command.admin.*
import net.evilblock.prisonaio.module.tool.enchant.command.parameter.AbstractEnchantParameterType
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.command.PrestigeEditorCommand
import net.evilblock.prisonaio.module.tool.pickaxe.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.tool.pickaxe.salvage.command.SalvagePreventionEditorCommand
import net.evilblock.prisonaio.module.tool.pickaxe.command.*
import net.evilblock.prisonaio.module.tool.pickaxe.command.PickaxeCommand
import net.evilblock.prisonaio.module.tool.pickaxe.command.admin.*
import net.evilblock.prisonaio.module.tool.pickaxe.listener.PickaxeStatisticsListeners
import net.evilblock.prisonaio.module.tool.rename.command.GiveRenameTagCommand
import net.evilblock.prisonaio.module.tool.rename.listener.RenameTagListeners
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
        EnchantHandler.initialLoad()
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
        EnchantHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            EnchantHandler,
            PickaxeStatisticsListeners,
            RenameTagListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            BookCommand.javaClass,
            ForgetPickaxeCommand.javaClass,
            ManageEnchantsCommand.javaClass,
            EnchantCommand.javaClass,
            PickaxeCommand.javaClass,
            GivePickaxeCommand.javaClass,
            GiveRenameTagCommand.javaClass,
            PickaxeDebugCommand.javaClass,
            PickaxeSetBlocksMinedCommand.javaClass,
            PickaxeSetLevelCommand.javaClass,
            PickaxeSetPrestigeCommand.javaClass,
            RemoveEnchantCommand.javaClass,
            SalvagePreventionEditorCommand.javaClass,
            PrestigeEditorCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Enchant::class.java to AbstractEnchantParameterType
        )
    }

}