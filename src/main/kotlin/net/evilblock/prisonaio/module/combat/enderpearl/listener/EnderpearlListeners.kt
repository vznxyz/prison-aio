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
import org.bukkit.block.BlockFace
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
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

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onDamage(event: ProjectileHitEvent) {
        if (event.entity !is EnderPearl) {
            return
        }

        val enderPearl = event.entity as EnderPearl
        if (enderPearl.shooter !is Player) {
            return
        }

        val shooter = enderPearl.shooter as Player

        val targetLocation = when {
            event.hitEntity != null -> {
                event.hitEntity.location.clone()
            }
            event.hitBlock != null -> {
                event.hitBlock.location.clone()
            }
            else -> {
                event.entity.location.clone()
            }
        }

        val blockFace: BlockFace = getDirection(shooter)
        if (blockFace == BlockFace.NORTH) {
            targetLocation.z = targetLocation.z + 0.5
        }

        if (blockFace == BlockFace.SOUTH) {
            targetLocation.z = targetLocation.z - 0.5
        }

        if (blockFace == BlockFace.WEST) {
            targetLocation.x = targetLocation.x + 0.5
        }

        if (blockFace == BlockFace.EAST) {
            targetLocation.x = targetLocation.x - 0.5
        }

        if (targetLocation.block.type != Material.AIR) {
            return
        }
    }

    private fun getDirection(player: Player): BlockFace {
        var yaw = player.location.yaw
        if (yaw < 0) {
            yaw += 360f
        }

        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH
        } else if (yaw < 135) {
            return BlockFace.WEST
        } else if (yaw < 225) {
            return BlockFace.NORTH
        } else if (yaw < 315) {
            return BlockFace.EAST
        }

        return BlockFace.NORTH
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