/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.gang.challenge.GangChallengeHandler
import net.evilblock.prisonaio.module.gang.challenge.listener.GangChallengeListeners
import net.evilblock.prisonaio.module.gang.command.GangBoostersCommand
import net.evilblock.prisonaio.module.gang.command.*
import net.evilblock.prisonaio.module.gang.command.admin.*
import net.evilblock.prisonaio.module.gang.command.parameter.GangBoosterParameterType
import net.evilblock.prisonaio.module.gang.command.parameter.GangParameterType
import net.evilblock.prisonaio.module.gang.listener.*
import net.evilblock.prisonaio.module.gang.advertisement.GangAdvertisementHandler
import net.evilblock.prisonaio.module.gang.rules.GangRulesHandler
import net.evilblock.prisonaio.module.gang.command.GangRulesCommand
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object GangsModule : PluginModule() {

    override fun getName(): String {
        return "Gangs"
    }

    override fun getConfigFileName(): String {
        return "gangs"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        GangChallengeHandler.initialLoad()
        GangHandler.initialLoad()
        GangAdvertisementHandler.initialLoad()
        GangRulesHandler.initialLoad()
    }

    override fun onDisable() {
        super.onDisable()

        GangHandler.saveData()
        GangAdvertisementHandler.saveData()
        GangRulesHandler.saveData()
    }

    override fun onAutoSave() {
        super.onAutoSave()

        GangHandler.saveData()
        GangAdvertisementHandler.saveData()
        GangRulesHandler.saveData()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            GangChatListeners,
            GangChallengeListeners,
            GangEntityListeners,
            GangGuideListeners,
            GangSessionListeners,
            GangTrophiesListeners,
            GangWorldListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            GangsCommand.javaClass,
            GangBoostersCommand.javaClass,
            GangChallengesCommand.javaClass,
            GangCreateCommand.javaClass,
            GangDisbandCommand.javaClass,
            GangHelpCommand.javaClass,
            GangHomeCommand.javaClass,
            GangInfoCommand.javaClass,
            GangInviteCommand.javaClass,
            GangJoinCommand.javaClass,
            GangKickCommand.javaClass,
            GangLeaveCommand.javaClass,
            GangRenameCommand.javaClass,
            GangRevokeInviteCommand.javaClass,
            GangSetAnnouncementCommand.javaClass,
            GangSetHomeCommand.javaClass,
            GangSetLeaderCommand.javaClass,
            GangVisitCommand.javaClass,
            GangForceInvitesGiveCommand.javaClass,
            GangForceInvitesTakeCommand.javaClass,
            GangForceInvitesResetAllCommand.javaClass,
            GangForceDisbandCommand.javaClass,
            GangForceJoinCommand.javaClass,
            GangForceKickCommand.javaClass,
            GangForceLeaderCommand.javaClass,
            GangForceResetCommand.javaClass,
            GangRefreshValueCommand.javaClass,
            GangRulesEditorCommand.javaClass,
            GangTrophiesGiveCommand.javaClass,
            GangTrophiesSetCommand.javaClass,
            GangTrophiesTakeCommand.javaClass,
            GangTrophiesResetAllCommand.javaClass,
            GangBoostersGrantCommand.javaClass,
            GangRulesCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Gang::class.java to GangParameterType,
            GangBooster.BoosterType::class.java to GangBoosterParameterType
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

    fun getIslandSchematicFile(): File {
        val schematicsFolder = File(JavaPlugin.getPlugin(WorldEditPlugin::class.java).dataFolder, "schematics")
        return File(schematicsFolder, config.getString("gang.schematic-file"))
    }

    fun getMaxMembers(): Int {
        return config.getInt("gang.max-members", 10)
    }

    fun getMaxNameLength(): Int {
        return config.getInt("gang.max-name-length", 16)
    }

    fun getGuideNPCHologramLines(): List<String> {
        return config.getStringList("gang.guide-npc.hologram-lines").map { ChatColor.translateAlternateColorCodes('&', it) }
    }

    fun getGuideNPCTextureValue(): String {
        return config.getString("gang.guide-npc.texture-value")
    }

    fun getGuideNPCTextureSignature(): String {
        return config.getString("gang.guide-npc.texture-signature")
    }

    fun readTrophyBlockBreakChance(): Double {
        return config.getDouble("trophies.block-break.chance", 0.05)
    }

    fun readTrophyBlockBreakMinAmount(): Int {
        return config.getInt("trophies.block-break.min-amount", 1)
    }

    fun readTrophyBlockBreakMaxAmount(): Int {
        return config.getInt("trophies.block-break.max-amount", 1)
    }

    fun readIncreasedTrophiesChanceMod(): Double {
        return config.getDouble("boosters.increased-trophies.chance-mod", 0.1)
    }

    fun readIncreasedMineCratesChanceMod(): Double {
        return config.getDouble("boosters.increased-mine-crates.chance-mod", 5.0)
    }

    fun readSalesMultiplierMod(): Double {
        return config.getDouble("boosters.sales-multiplier.multiplier-mod")
    }

}