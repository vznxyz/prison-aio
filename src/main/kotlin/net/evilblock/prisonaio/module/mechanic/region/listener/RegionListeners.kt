package net.evilblock.prisonaio.module.mechanic.region.listener

import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import net.evilblock.prisonaio.module.mechanic.region.Regions
import net.evilblock.prisonaio.module.mechanic.region.bypass.RegionBypass
import net.evilblock.prisonaio.util.Permissions
import net.evilblock.prisonaio.util.plot.PlotUtil
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object RegionListeners : Listener {

    private fun bypassCheck(player: Player, cancellable: Cancellable): Boolean {
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

    private fun worldGuardCheck(player: Player, location: Location, cancellable: Cancellable): Boolean {
        var canBuild = WorldGuardPlugin.inst().canBuild(player, location)

        if (canBuild) {
            val applicableRegions = WorldGuardPlugin.inst().getRegionManager(location.world).getApplicableRegions(location)
            if (applicableRegions.regions.isEmpty()) {
                // this means there's no region at location, meaning use GLOBAL rules (no building here!)
                canBuild = false
            }
        }

        if (!canBuild) {
            player.sendMessage("${ChatColor.RED}You can't build here!")
            cancellable.isCancelled = true
        }

        return canBuild
    }

    private fun plotCheck(player: Player, location: Location): Boolean {
        val plot = PlotUtil.getPlot(location) ?: return false

        if (plot.denied.contains(player.uniqueId)) {
            return false
        }

        if (plot.owners.contains(player.uniqueId)) {
            return true
        }

        if (plot.members.contains(player.uniqueId)) {
            return true
        }

        return false
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        if (plotCheck(event.player, event.blockPlaced.location)) {
            return
        }

        val region = Regions.findRegion(event.block.location)
        if (region != null) {
            region.onBlockPlace(event.player, event.blockPlaced, event)
            return
        }

        if (!worldGuardCheck(event.player, event.blockPlaced.location, event)) {
            return
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        if (plotCheck(event.player, event.block.location)) {
            return
        }

        val region = Regions.findRegion(event.block.location)
        if (region != null) {
            region.onBlockBreak(event.player, event.block, event)
            return
        }

        if (!worldGuardCheck(event.player, event.block.location, event)) {
            return
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBucketEmptyEvent(event: PlayerBucketEmptyEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        val emptiedAt = event.blockClicked.getRelative(event.blockFace)

        if (plotCheck(event.player, emptiedAt.location)) {
            return
        }

        val region = Regions.findRegion(emptiedAt.location)
        if (region != null) {
            region.onBucketEmpty(event.player, emptiedAt, event)
            return
        }

        if (!worldGuardCheck(event.player, emptiedAt.location, event)) {
            return
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBucketFillEvent(event: PlayerBucketFillEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        val filledFrom = event.blockClicked.getRelative(event.blockFace)

        if (plotCheck(event.player, filledFrom.location)) {
            return
        }

        val region = Regions.findRegion(filledFrom.location)
        if (region != null) {
            region.onBucketFill(event.player, filledFrom, event)
            return
        }

        if (!worldGuardCheck(event.player, filledFrom.location, event)) {
            return
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockExplodeEvent(event: BlockExplodeEvent) {
        val region = Regions.findRegion(event.block.location)
        if (region != null) {
            region.onBlockExplode(event.block, event)
            return
        }

        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockIgniteEvent(event: BlockIgniteEvent) {
        val region = Regions.findRegion(event.block.location)
        if (region != null) {
            region.onBlockIgnite(event.block, event.ignitingEntity, event.cause, event)
            return
        }

        if (event.player != null) {
            if (!worldGuardCheck(event.player, event.block.location, event)) {
                return
            }
        }

        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (bypassCheck(event.player, event)) {
            return
        }

        if (plotCheck(event.player, event.clickedBlock.location)) {
            return
        }

        val region = Regions.findRegion(event.clickedBlock.location)
        if (region != null) {
            if (event.action == Action.LEFT_CLICK_BLOCK) {
                region.onLeftClickBlock(event.player, event.clickedBlock, event)
            } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                region.onRightClickBlock(event.player, event.clickedBlock, event)
            }
        } else {
            if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock.type == Material.ENDER_CHEST) {
                event.isCancelled = false
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        if (event.rightClicked is ItemFrame) {
            if (bypassCheck(event.player, event)) {
                return
            }
        }

        val region = Regions.findRegion(event.rightClicked.location)
        if (region != null) {
            region.onPlayerInteractEntity(event.player, event.rightClicked, event)
            return
        }

        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is ItemFrame) {
            if (bypassCheck(event.damager as Player, event)) {
                return
            }

            if (plotCheck(event.damager as Player, event.entity.location)) {
                return
            }
        }

        val region = Regions.findRegion(event.damager.location)
        if (region != null) {
            region.onEntityDamageEntity(event.damager, event.entity, event.cause, event.finalDamage, event)
            return
        }

        event.isCancelled = true
    }

}