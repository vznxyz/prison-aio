/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import mkremins.fanciful.FancyMessage
import net.evilblock.cubed.logging.ErrorHandler
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object UserLoadListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        UserHandler.getUser(event.player.uniqueId).let { user ->
            user.applyPermissions(event.player)

            val unreadProfileComments = user.getProfileComments().filter { !it.read }
            if (unreadProfileComments.isNotEmpty()) {
                FancyMessage("${ChatColor.YELLOW}You have ${unreadProfileComments.size} un-read comments left on your profile! ")
                    .then("${ChatColor.GREEN}${ChatColor.BOLD}[VIEW]")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to view your profile comments."))
                    .command("/profile ${event.player.name} comments")
                    .send(event.player)
            }
        }
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        try {
            val user = UserHandler.getOrLoadAndCacheUser(event.uniqueId, lookup = false)
            user.cacheExpiry = null
            user.statistics.lastPlayTimeSync = System.currentTimeMillis()

            UserHandler.cacheUser(user)
        } catch (exception: Exception) {
            exception.printStackTrace()

            val eventDetails = mapOf(
                "Player Name" to event.name,
                "Player UUID" to event.uniqueId.toString(),
                "Player IP" to event.address.hostAddress
            )

            val logId = ErrorHandler.generateErrorLog("loginEvent", eventDetails, exception)

            val kickMessage = StringBuilder()
                .append("${ChatColor.RED}${ChatColor.BOLD}Sorry about that...")
                .append("\n")
                .append("${ChatColor.GRAY}We failed to load your user data. Please try again later.")
                .append("\n")
                .append("${ChatColor.GRAY}If this error persists, please contact an admin and")
                .append("\n")
                .append("${ChatColor.GRAY}provide them this error ID: ${ChatColor.WHITE}$logId")

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage.toString())
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val user = UserHandler.forgetUser(event.player.uniqueId)

        if (user?.requiresSave() == true) {
            Tasks.async {
                UserHandler.saveUser(user)
            }
        }
    }

}