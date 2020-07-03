/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.listener

import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import net.evilblock.prisonaio.module.region.event.RegionBlockBreakEvent
import net.evilblock.prisonaio.util.Permissions
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

object RegionListeners : Listener {

    fun bypassCheck(player: Player, cancellable: Cancellable): Boolean {
        if (player.gameMode == GameMode.CREATIVE && (player.hasPermission(Permissions.REGION_BYPASS) || player.isOp)) {
            return if (RegionBypass.hasBypass(player)) {
                if (!RegionBypass.hasReceivedNotification(player)) {
                    RegionBypass.sendNotification(player)
                }

                cancellable.isCancelled = false
                true
            } else {
                player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}WARNING: ${ChatColor.GRAY}You can't use creative mode unless you have region bypass enabled.")
                cancellable.isCancelled = true
                true
            }
        }

        return false
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        val fromRegion = RegionsModule.findRegion(event.from)
        val toRegion = RegionsModule.findRegion(event.to)

        if (fromRegion != toRegion) {
            fromRegion.onLeaveRegion(event.player)
            toRegion.onEnterRegion(event.player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        val fromRegion = RegionsModule.findRegion(event.from)
        val toRegion = RegionsModule.findRegion(event.to)

        if (fromRegion != toRegion) {
            fromRegion.onLeaveRegion(event.player)
            toRegion.onEnterRegion(event.player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        if (PlotUtil.getPlot(event.blockPlaced.location) != null) {
            return
        }

        RegionsModule.findRegion(event.block.location).onBlockPlace(event.player, event.blockPlaced, event)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        if (PlotUtil.getPlot(event.block.location) != null) {
            return
        }

        val region = RegionsModule.findRegion(event.block.location)
        region.onBlockBreak(event.player, event.block, event)

        if (!event.isCancelled) {
            val regionBlockBreakEvent = RegionBlockBreakEvent(event.player, region, event.block)
            regionBlockBreakEvent.call()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBucketEmptyEvent(event: PlayerBucketEmptyEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        val emptiedAt = event.blockClicked.getRelative(event.blockFace)

        if (PlotUtil.getPlot(emptiedAt.location) != null) {
            return
        }

        RegionsModule.findRegion(emptiedAt.location).onBucketEmpty(event.player, emptiedAt, event)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBucketFillEvent(event: PlayerBucketFillEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        val filledFrom = event.blockClicked.getRelative(event.blockFace)

        if (PlotUtil.getPlot(filledFrom.location) != null) {
            return
        }

        RegionsModule.findRegion(filledFrom.location).onBucketFill(event.player, filledFrom, event)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockExplodeEvent(event: BlockExplodeEvent) {
        RegionsModule.findRegion(event.block.location).onBlockExplode(event.block, event)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockIgniteEvent(event: BlockIgniteEvent) {
        RegionsModule.findRegion(event.block.location).onBlockIgnite(event.block, event.ignitingEntity, event.cause, event)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (bypassCheck(event.player, event)) {
                return
            }
        }

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK) {
            val region = RegionsModule.findRegion(event.clickedBlock.location)

            if (event.action == Action.LEFT_CLICK_BLOCK) {
                region.onLeftClickBlock(event.player, event.clickedBlock, event)
            } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                region.onRightClickBlock(event.player, event.clickedBlock, event)
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        RegionsModule.findRegion(event.rightClicked.location).onPlayerInteractEntity(event.player, event.rightClicked, event)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        RegionsModule.findRegion(event.damager.location).onEntityDamageEntity(event.damager, event.entity, event.cause, event.finalDamage, event)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onFoodLevelChangeEvent(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            RegionsModule.findRegion(event.entity.location).onFoodLevelChange(event)

            event.isCancelled = true

            val player = event.entity as Player
            player.foodLevel = 20
            player.saturation = 5F
            player.exhaustion = 0F
            player.updateInventory()
        }
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        RegionsModule.findRegion(event.entity.location).onPlayerDeath(event.entity, event)
    }

}