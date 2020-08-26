/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.news.listener

import mkremins.fanciful.FancyMessage
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.news.NewsHandler
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object NewsListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val user = UserHandler.getUser(event.player.uniqueId)

        val latestPost = NewsHandler.getLatestNews()
        if (latestPost != null) {
            if (!user.hasReadNewsPost(latestPost)) {
                event.player.sendMessage("")
                event.player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}Heads up! ${ChatColor.GRAY}There's a new server announcement!")

                FancyMessage("${ChatColor.GRAY}Click to read the latest post: ")
                    .then("${ChatColor.RESET}${latestPost.title}")
                    .formattedTooltip(FancyMessage("${ChatColor.YELLOW}Click to view the latest announcement."))
                    .command("/news")
                    .send(event.player)

                event.player.sendMessage("")
            }
        }
    }

}