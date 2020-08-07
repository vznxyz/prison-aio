/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bypass

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object RegionBypass : Listener {

    private const val METADATA_KEY = "REGION_BYPASS"
    private val noticeSent = hashSetOf<UUID>()

    @JvmStatic
    fun hasBypass(player: Player): Boolean {
        return player.hasMetadata(METADATA_KEY)
    }

    @JvmStatic
    fun setBypass(player: Player, bypass: Boolean) {
        noticeSent.remove(player.uniqueId)

        if (bypass) {
            player.setMetadata(METADATA_KEY, FixedMetadataValue(PrisonAIO.instance, true))
        } else {
            player.removeMetadata(METADATA_KEY, PrisonAIO.instance)
        }
    }

    @JvmStatic
    fun attemptNotify(player: Player) {
        if (noticeSent.contains(player.uniqueId)) {
            return
        }

        noticeSent.add(player.uniqueId)
        player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}WARNING: ${ChatColor.GRAY}Region bypass is currently enabled.")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        Tasks.delayed(10L) {
            if (hasBypass(event.player)) {
                if (!event.player.hasPermission(Permissions.REGION_BYPASS)) {
                    setBypass(event.player, false)
                } else {
                    setBypass(event.player, true)
                    attemptNotify(event.player)

                    if (event.player.gameMode != GameMode.CREATIVE) {
                        event.player.gameMode = GameMode.CREATIVE
                    }
                }
            }
        }
    }

}