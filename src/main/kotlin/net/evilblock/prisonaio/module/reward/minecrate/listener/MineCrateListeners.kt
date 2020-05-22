package net.evilblock.prisonaio.module.reward.minecrate.listener

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.mechanic.region.Regions
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrate
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
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
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onBlockBreakEventHigh(event: BlockBreakEvent) {
        val region = Regions.findRegion(event.block.location)
        if (region == null || !region.supportsRewards()) {
            return
        }

        if (!MineCrateHandler.isOnCooldown(event.player)) {
            for (rewardSet in MineCrateHandler.getRewardSets().filter { it.worlds.contains(event.block.world.name) }.shuffled()) {
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

}