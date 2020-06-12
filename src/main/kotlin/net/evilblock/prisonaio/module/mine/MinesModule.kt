package net.evilblock.prisonaio.module.mine

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.mine.command.*
import net.evilblock.prisonaio.module.mine.command.parameter.MineParameterType
import net.evilblock.prisonaio.module.mine.listener.MineEventEmitterListeners
import net.evilblock.prisonaio.module.mine.listener.MineInventoryListeners
import net.evilblock.prisonaio.module.mine.listener.MineWandListeners
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

    override fun onEnable() {
        MineHandler.initialLoad()

        getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), MineResetTask, 20L * 10L, 20L * 1L)
        getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), MineEffectsTask, 20L, 20L)
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
            MineWandCommand::class.java
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            Mine::class.java to MineParameterType
        )
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            MineEventEmitterListeners,
            MineInventoryListeners,
            MineWandListeners
        )
    }

    fun getNearbyRadius(): Int {
        return config.getInt("nearby-radius", 5)
    }

}