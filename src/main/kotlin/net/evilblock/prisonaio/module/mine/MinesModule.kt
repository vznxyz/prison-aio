/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mine.command.*
import net.evilblock.prisonaio.module.mine.command.parameter.MineParameterType
import net.evilblock.prisonaio.module.mine.listener.MineChunkListeners
import net.evilblock.prisonaio.module.mine.listener.MineInventoryListeners
import net.evilblock.prisonaio.module.mine.listener.MineShopListeners
import net.evilblock.prisonaio.module.mine.service.MineResetService
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.command.LuckyBlockEditorCommand
import net.evilblock.prisonaio.module.mine.variant.luckyblock.command.LuckyBlockToggleCommand
import net.evilblock.prisonaio.service.ServiceRegistry
import org.bukkit.ChatColor
import org.bukkit.event.Listener

object MinesModule : PluginModule() {

    override fun getName(): String {
        return "Mines"
    }

    override fun getConfigFileName(): String {
        return "mines"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        LuckyBlockHandler.initialLoad()
        MineHandler.initialLoad()

        ServiceRegistry.register(MineResetService, 200L, 20L)
    }

    override fun onDisable() {
        MineHandler.saveData()
    }

    override fun onAutoSave() {
        MineHandler.saveData()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            MineCreateCommand::class.java,
            MineDeleteCommand::class.java,
            MineManageCommand::class.java,
            MineResetCommand::class.java,
            MineSetRegionCommand::class.java,
            MineSetSpawnCommand::class.java,
            MineListCommand::class.java,
            MineTeleportCommand::class.java,
            LuckyBlockEditorCommand::class.java,
            LuckyBlockToggleCommand::class.java
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Mine::class.java to MineParameterType()
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            MineChunkListeners,
            MineInventoryListeners,
            MineShopListeners,
            LuckyBlockHandler.spawnSelectionHandler
        )
    }

    fun getNearbyRadius(): Int {
        return config.getInt("nearby-radius", 5)
    }

    fun getLuckyBlockChatPrefix(): String {
        return ChatColor.translateAlternateColorCodes('&', config.getString("lucky-block-mines.chat-prefix", ""))
    }

    fun getLuckyBlockMineMaxSpawns(): Int {
        return config.getInt("lucky-block-mines.max-spawns", 16)
    }

    fun getLuckyBlockRegenTime(): Long {
        return config.getLong("lucky-block-mines.regen-time", 15_000L)
    }

}