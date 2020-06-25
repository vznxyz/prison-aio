/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.minecrate.listener

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.event.RegionBlockBreakEvent
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrate
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object MineCrateListeners : Listener {

    /**
     * Handles when a player breaks a MineCrate.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!(event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK)) {
            return
        }

        if (MineCrateHandler.isAttached(event.clickedBlock.location)) {
            event.isCancelled = true

            val spawnedCrate = MineCrateHandler.getSpawnedCrate(event.clickedBlock.location)

            if (MineCrateHandler.isOnlyOwnerCanOpen()) {
                if (event.player.uniqueId != spawnedCrate.owner) {
                    event.player.sendMessage("${ChatColor.RED}You can't break that MineCrate because you didn't find it!")
                    return
                }
            }

            spawnedCrate.destroy()
            MineCrateHandler.forgetSpawnedCrate(spawnedCrate)

            for (reward in spawnedCrate.rewardSet.pickRewards()) {
                event.player.sendMessage("${RewardsModule.getChatPrefix()}You received ${reward.name} ${ChatColor.GRAY}from the MineCrate!")
                reward.execute(event.player)
            }
        }
    }

    /**
     * Spawns MineCrates randomly when breaking blocks.
     */
    @EventHandler(ignoreCancelled = true)
    fun onRegionBlockBreakEvent(event: RegionBlockBreakEvent) {
        if (!event.region.supportsRewards() || event.region.getBreakableCuboid()?.contains(event.block) == false) {
            return
        }

        if (!MineCrateHandler.isOnCooldown(event.player)) {
            for (rewardSet in MineCrateHandler.getRewardSets().shuffled()) {
                if (Chance.percent(rewardSet.chance)) {
                    MineCrateHandler.resetCooldown(event.player)

                    // wait a tick before updating the block
                    PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                        val mineCrate = MineCrate(event.block.location, event.player.uniqueId, rewardSet)
                        MineCrateHandler.trackSpawnedCrate(mineCrate)
                    }, 1L)

                    event.player.sendMessage("${RewardsModule.getChatPrefix()}You just found a MineCrate!")
                    return
                }
            }
        }
    }

    /**
     * Prevents multi-block-break events from breaking MineCrate blocks.
     *
     * If the player causing the event is the owner of a MineCrate in the block list,
     * the MineCrate will be redeemed by the player.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        val blockIterator = event.blockList.iterator()
        while (blockIterator.hasNext()) {
            val block = blockIterator.next()
            if (block.type == Material.ENDER_CHEST && MineCrateHandler.isAttached(block.location)) {
                blockIterator.remove()

                val spawnedCrate = MineCrateHandler.getSpawnedCrate(block.location)
                if (spawnedCrate.owner == event.player.uniqueId) {
                    spawnedCrate.destroy()
                }
            }
        }
    }

}