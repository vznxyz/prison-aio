/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.minigame.coinflip.command.CoinFlipToggleCommand
import net.evilblock.prisonaio.module.minigame.coinflip.command.CoinFlipBrowseCommand
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameType
import net.evilblock.prisonaio.module.minigame.event.EventConfig
import net.evilblock.prisonaio.module.minigame.event.command.SetLobbyCommand
import net.evilblock.prisonaio.module.minigame.event.command.ToggleCommand
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArenaHandler
import net.evilblock.prisonaio.module.minigame.event.game.arena.command.EventArenaEditorCommand
import net.evilblock.prisonaio.module.minigame.event.game.arena.command.EventArenaSetPointCommand
import net.evilblock.prisonaio.module.minigame.event.game.arena.command.EventArenaSetSpectatorCommand
import net.evilblock.prisonaio.module.minigame.event.game.arena.command.parameter.EventGameArenaParameterType
import net.evilblock.prisonaio.module.minigame.event.game.command.*
import net.evilblock.prisonaio.module.minigame.event.game.command.admin.ForceEndCommand
import net.evilblock.prisonaio.module.minigame.event.game.command.admin.ForceJoinCommand
import net.evilblock.prisonaio.module.minigame.event.game.command.admin.ForceStartCommand
import net.evilblock.prisonaio.module.minigame.event.game.command.admin.SetMaxPlayersCommand
import net.evilblock.prisonaio.module.minigame.event.game.command.parameter.EventGameTypeParameterType
import net.evilblock.prisonaio.module.minigame.event.game.sumo.SumoEventGameListeners
import net.evilblock.prisonaio.module.minigame.event.game.listener.EventGameListeners
import org.bukkit.event.Listener

object MinigamesModule : PluginModule() {

    override fun getName(): String {
        return "Minigames"
    }

    override fun getConfigFileName(): String {
        return "minigames"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        CoinFlipHandler.initialLoad()
        EventConfig.load()
        EventGameHandler.initialLoad()
        EventGameArenaHandler.initialLoad()
    }

    override fun onDisable() {
        CoinFlipHandler.cancelGames()
    }

    override fun getListeners(): List<Listener> {
        return listOf(
            EventGameListeners,
            SumoEventGameListeners
        )
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CoinFlipToggleCommand.javaClass,
            CoinFlipBrowseCommand.javaClass,
            EventArenaEditorCommand.javaClass,
            EventArenaSetPointCommand.javaClass,
            EventArenaSetSpectatorCommand.javaClass,
            ForceEndCommand.javaClass,
            ForceJoinCommand.javaClass,
            ForceStartCommand.javaClass,
            HostCommand.javaClass,
            JoinCommand.javaClass,
            LeaveCommand.javaClass,
            SetLobbyCommand.javaClass,
            SetMaxPlayersCommand.javaClass,
            ToggleCommand.javaClass
        )
    }

    override fun getCommandParameterTypes(): Map<Class<*>, ParameterType<*>> {
        return mapOf(
            EventGameArena::class.java to EventGameArenaParameterType(),
            EventGameType::class.java to EventGameTypeParameterType()
        )
    }

}