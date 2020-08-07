/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.timer.listener

import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent

object CombatTimerListeners : Listener {

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val timer = CombatTimerHandler.getTimer(event.player.uniqueId)
        if (timer != null) {
            CombatTimerHandler.forgetTimer(timer)
        }
    }

    @EventHandler
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        val combatTimer = CombatTimerHandler.getTimer(event.player.uniqueId)
        if (combatTimer != null && !combatTimer.hasExpired()) {
            for (blockedCommand in BLOCKED_COMMANDS) {
                if (event.message.startsWith(blockedCommand, ignoreCase = true)) {
                    event.isCancelled = true
                    event.player.sendMessage("${ChatColor.RED}You can't execute that command while in combat!")
                    return
                }
            }
        }
    }

    val BLOCKED_COMMANDS = arrayListOf(
        "/spawn",
        "/prisonaio:spawn",
        "/gang",
        "/prisonaio:gang",
        "/p h",
        "/p home",
        "/plot h",
        "/plot home",
        "/plotsquared:p h",
        "/plotsquared:p home",
        "/plotsquared:plot h",
        "/plotsquared:plot home",
        "/p",
        "/plot",
        "/plots",
        "/plotsquared:p",
        "/plotsquared:plot",
        "/plotsquared:plots",
        "/warp",
        "/essentials:warp",
        "/ewarp",
        "/essentials:ewarp",
        "/home",
        "/essentials:home",
        "/essentials:spawn",
        "/feed",
        "/essentials:feed",
        "/heal",
        "/essentials:heal",
        "/fly",
        "/prisonaio:fly",
        "/essentials:fly",
        "/speed",
        "/essentials:speed",
        "/cubed:speed",
        "/shop",
        "/shopgui",
        "/echest",
        "/essentials:echest",
        "/eechest",
        "/essentials:eechest",
        "/craft",
        "/essentials:craft",
        "/ecraft",
        "/essentials:ecraft",
        "/pmine",
        "/prisonaio:pmine",
        "/privatemine",
        "/prisonaio:privatemine"
    )

}