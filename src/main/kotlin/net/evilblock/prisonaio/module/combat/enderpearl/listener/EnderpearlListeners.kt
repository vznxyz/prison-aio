/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.enderpearl.listener

import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldown
import net.evilblock.prisonaio.module.combat.enderpearl.EnderpearlCooldownHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

object EnderpearlListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if (event.entityType != EntityType.ENDER_PEARL) {
            return
        }

        if (event.entity.shooter != null && event.entity.shooter is Player) {
            val shooter = event.entity.shooter as Player
            val cooldown = EnderpearlCooldownHandler.getCooldown(shooter.uniqueId)
            if (cooldown == null) {
                EnderpearlCooldownHandler.trackCooldown(EnderpearlCooldown(shooter.uniqueId))
            } else {
                cooldown.reset()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!event.hasItem() || event.item.type != Material.ENDER_PEARL || !(event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR)) {
            return
        }

        val cooldown = EnderpearlCooldownHandler.getCooldown(event.player.uniqueId)
        if (cooldown != null && !cooldown.hasExpired()) {
            event.isCancelled = true
            event.player.sendMessage("${ChatColor.RED}You can't use this for another ${ChatColor.BOLD}${cooldown.getRemainingSeconds()} ${ChatColor.RED}seconds!")
            event.player.updateInventory()
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val cooldown = EnderpearlCooldownHandler.getCooldown(event.player.uniqueId)
        if (cooldown != null) {
            EnderpearlCooldownHandler.forgetCooldown(cooldown)
        }
    }

}