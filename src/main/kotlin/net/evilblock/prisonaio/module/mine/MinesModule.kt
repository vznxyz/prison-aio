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
import net.evilblock.prisonaio.module.mine.task.MineEffectsTask
import net.evilblock.prisonaio.module.mine.task.MineResetTask
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
        MineHandler.initialLoad()

        getPluginFramework().server.scheduler.runTaskTimerAsynchronously(getPluginFramework(), MineResetTask, 20L * 10L, 20L * 1L)
        getPluginFramework().server.scheduler.runTaskTimerAsynchronously(getPluginFramework(), MineEffectsTask, 20L, 20L)
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
            MineTeleportCommand::class.java
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
            MineInventoryListeners
        )
    }

    fun getNearbyRadius(): Int {
        return config.getInt("nearby-radius", 5)
    }

}