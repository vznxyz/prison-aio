/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent

object DropPickaxeListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (MechanicsModule.isPickaxe(event.itemDrop.itemStack)) {
            if (!event.player.hasMetadata("CONFIRM_DROP")) {
                event.player.sendMessage("${ChatColor.RED}You must type ${ChatColor.BOLD}/drop confirm ${ChatColor.RED}to drop your pickaxe!")
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        event.player.removeMetadata("CONFIRM_DROP", PrisonAIO.instance)
    }

}