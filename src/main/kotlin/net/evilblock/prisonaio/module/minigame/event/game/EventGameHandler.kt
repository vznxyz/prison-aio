/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game

import net.evilblock.cubed.plugin.PluginHandler
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.prisonaio.module.minigame.MinigamesModule
import net.evilblock.prisonaio.module.minigame.event.EventUtils
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArena
import net.evilblock.prisonaio.module.minigame.event.game.arena.EventGameArenaHandler
import net.evilblock.prisonaio.module.minigame.event.game.sumo.SumoEventGame
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object EventGameHandler : PluginHandler {

    private val GAME_COOLDOWN = TimeUnit.MINUTES.toMillis(5L)

    private var ongoingGame: EventGame? = null
    private var nextRuntime = System.currentTimeMillis() - GAME_COOLDOWN

    var disabled = false

    override fun initialLoad() {
    }

    override fun getModule(): PluginModule {
        return MinigamesModule
    }

    fun getOngoingGame(): EventGame? {
        return ongoingGame
    }

    fun isOngoingGame(): Boolean {
        return ongoingGame != null
    }

    fun isJoinable(): Boolean {
        return ongoingGame != null && ongoingGame!!.state == EventGameState.WAITING
    }

    fun canStartGame(player: Player, gameType: EventGameType): Boolean {
        if (disabled) {
            player.sendMessage("${ChatColor.RED}Events are currently disabled.")
            return false
        }

        if (!gameType.canHost(player)) {
            player.sendMessage("${ChatColor.RED}You don't have permission to host ${gameType.displayName} events.")
            return false
        }

        if (isOngoingGame()) {
            player.sendMessage("${ChatColor.RED}There is an ongoing game, and only one game can run at a time.")
            return false
        }

        if (findArenas(gameType).isEmpty()) {
            player.sendMessage("${ChatColor.RED}There are no arenas compatible with that game type!")
            return false
        }

        if (!player.hasPermission(Permissions.EVENTS_HOST_COOLDOWN_BYPASS) && System.currentTimeMillis() < nextRuntime) {
            val remainingSeconds = (nextRuntime - System.currentTimeMillis()) / 1000f
            player.sendMessage("${ChatColor.RED}Another game can't be hosted for another " + (10.0 * remainingSeconds.toDouble()).roundToInt().toDouble() / 10.0 + "s.")
            return false
        }

        if (!EventUtils.hasEmptyInventory(player)) {
            player.sendMessage("${ChatColor.RED}You need to have an empty inventory to join the event.")
            return false
        }

        return true
    }

    @Throws(IllegalStateException::class)
    fun startGame(host: Player, gameType: EventGameType): EventGame? {
        check(ongoingGame == null) { "There is an ongoing game!" }

        if (gameType === EventGameType.SUMO) {
            ongoingGame = SumoEventGame(host.uniqueId, findArenas(gameType))
        } else {
            throw IllegalStateException("Game type not supported yet!")
        }

        ongoingGame!!.startGame()

        return ongoingGame
    }

    fun endGame() {
        ongoingGame = null
        nextRuntime = System.currentTimeMillis() + GAME_COOLDOWN
    }

    fun findArenas(gameType: EventGameType): List<EventGameArena> {
        val compatible = arrayListOf<EventGameArena>()
        for (arena in EventGameArenaHandler.getArenas()) {
            if (arena.isSetup() && arena.isCompatible(gameType)) {
                compatible.add(arena)
            }
        }
        return compatible
    }

}