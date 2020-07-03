/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.crate.command.*
import net.evilblock.prisonaio.module.crate.command.parameter.CrateParameterType
import net.evilblock.prisonaio.module.crate.key.CrateKeyHandler
import net.evilblock.prisonaio.module.crate.key.listener.CrateKeyDupeListeners
import net.evilblock.prisonaio.module.crate.listener.CrateMechanicsListeners
import net.evilblock.prisonaio.module.crate.listener.CrateSetupListeners
import net.evilblock.prisonaio.module.crate.placed.PlacedCrateHandler
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object CratesModule : PluginModule() {

    override fun getName(): String {
        return "Crates"
    }

    override fun getConfigFileName(): String {
        return "crates"
    }

    override fun onEnable() {
        CrateHandler.initialLoad()
        CrateKeyHandler.initialLoad()
        PlacedCrateHandler.initialLoad()
    }

    override fun onDisable() {
        CrateHandler.saveData()
        CrateKeyHandler.saveData()
        PlacedCrateHandler.saveData()
    }

    override fun onAutoSave() {
        CrateHandler.saveData()
        CrateKeyHandler.saveData()
        PlacedCrateHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CrateEditorCommand.javaClass,
            CrateGiveKeyAllCommand.javaClass,
            CrateGiveKeyToCommand.javaClass,
            CratePlaceCommand.javaClass,
            CrateUnlinkCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Crate::class.java to CrateParameterType
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            CrateKeyDupeListeners,
            CrateMechanicsListeners,
            CrateSetupListeners
        )
    }

    fun getChatPrefix(): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix"))
    }

    fun getDefaultHologramLines(): List<String> {
        return config.getStringList("default-hologram-lines").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun isShowChancesInPreviewMenu(): Boolean {
        return config.getBoolean("show-chances-preview")
    }

}