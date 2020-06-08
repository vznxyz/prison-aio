package net.evilblock.prisonaio.module.cell

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.cell.command.*
import net.evilblock.prisonaio.module.cell.command.admin.RefreshCellValueCommand
import net.evilblock.prisonaio.module.cell.command.parameter.CellParameterType
import net.evilblock.prisonaio.module.cell.listener.*
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object CellsModule : PluginModule() {

    override fun getName(): String {
        return "Cells"
    }

    override fun getConfigFileName(): String {
        return "cells"
    }

    override fun onEnable() {
        CellHandler.initialLoad()

        getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), {
            for (cell in CellHandler.getAllCells()) {
                cell.expireInvitations()
            }
        }, 20L, 20L * 15)
    }

    override fun requiresLateLoad(): Boolean {
        return true
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            CellChatListeners,
            CellEntityListeners,
            CellJerryListeners,
            CellSessionListeners,
            CellWorldListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CellCreateCommand.javaClass,
            CellDisbandCommand.javaClass,
            CellHelpCommand.javaClass,
            CellHomeCommand.javaClass,
            CellHomesCommand.javaClass,
            CellInfoCommand.javaClass,
            CellInviteCommand.javaClass,
            CellJoinCommand.javaClass,
            CellKickCommand.javaClass,
            CellLeaveCommand.javaClass,
            CellRenameCommand.javaClass,
            CellRevokeInviteCommand.javaClass,
            CellSetAnnouncementCommand.javaClass,
            CellSetHomeCommand.javaClass,
            CellVisitCommand.javaClass,
            RefreshCellValueCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Cell::class.java to CellParameterType
        )
    }

    fun getGridWorldName(): String {
        return config.getString("grid.world-name")
    }

    fun getGridColumns(): Int {
        return config.getInt("grid.columns")
    }

    fun getGridGutterWidth(): Int {
        return config.getInt("grid.gutter-width")
    }

    fun getCellSchematicFile(): File {
        val schematicsFolder = File(JavaPlugin.getPlugin(WorldEditPlugin::class.java).dataFolder, "schematics")
        return File(schematicsFolder, config.getString("cell.schematic-file"))
    }

    fun getMaxMembers(): Int {
        return config.getInt("cell.max-members", 10)
    }

    fun getMaxNameLength(): Int {
        return config.getInt("cell.max-name-length", 32)
    }

    fun getMaxCellsPerPlayer(): Int {
        return config.getInt("cell.max-cells-per-player", 1)
    }

    fun getJerryHologramLines(): List<String> {
        return config.getStringList("cell.jerry.hologram-lines").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun getJerryChangeLog(): List<String> {
        return config.getStringList("cell.jerry.change-log").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun getJerryTextureValue(): String {
        return config.getString("cell.jerry.texture-value")
    }

    fun getJerryTextureSignature(): String {
        return config.getString("cell.jerry.texture-signature")
    }

}