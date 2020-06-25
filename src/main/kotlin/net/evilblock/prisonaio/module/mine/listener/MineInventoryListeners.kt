/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.listener

import com.destroystokyo.paper.Title
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

object MineInventoryListeners : Listener {

    /**
     * Handles teleporting a player to a [Mine]'s spawn-point when the player toggles sneak
     * while located within a [Mine] and has a full-inventory.
     */
    @EventHandler
    fun onPlayerToggleSneakEvent(event: PlayerToggleSneakEvent) {
        val user = UserHandler.getUser(event.player.uniqueId)
        if (user.getSettingOption(UserSetting.SNEAK_TO_TELEPORT).getValue()) {
            if (event.isSneaking && event.player.inventory.firstEmpty() == -1) {
                val optionalMine = MineHandler.getNearbyMine(event.player)
                if (optionalMine.isPresent) {
                    if (optionalMine.get().spawnPoint != null) {
                        event.player.teleport(optionalMine.get().spawnPoint)
                    }
                }
            }
        }
    }

    /**
     * Sends a [Title] to the player if their inventory is full while inside a mine.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.player.inventory.firstEmpty() == -1) {
            val optionalMine = MineHandler.getNearbyMine(event.player)
            if (optionalMine.isPresent) {
                event.player.sendTitle(Title("${ChatColor.RED}Inventory full!", "Sneak to teleport to the shop.", 10, 20, 10))
            }
        }
    }

}