/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.minecrate.listener

import net.evilblock.cubed.util.Chance
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.type.Luck
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.GangModule
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.event.RegionBlockBreakEvent
import net.evilblock.prisonaio.module.reward.minecrate.MineCrateHandler
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.minecrate.MineCrate
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.setting.UserSetting
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

            val sendMessages = UserHandler.getUser(event.player.uniqueId).getSettingOption(UserSetting.REWARD_MESSAGES).getValue<Boolean>()

            for (reward in spawnedCrate.rewardSet.pickRewards()) {
                if (sendMessages) {
                    event.player.sendMessage("${RewardsModule.getChatPrefix()}You received ${reward.name} ${ChatColor.GRAY}from the MineCrate!")
                }

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

        var chanceModifier = 0.0

        // apply luck multiplier
        if (event.region.supportsPassiveEnchants()) {
            val map = EnchantsManager.handleItemSwitch(event.player, event.player.itemInHand, event)
            if (map.isNotEmpty() && map.containsKey(Luck)) {
                val luckLevel = map.getValue(Luck)
                chanceModifier += (luckLevel * 2)
            }
        }

        // apply gang booster multiplier
        val assumedGang = GangHandler.getAssumedGang(event.player.uniqueId)
        if (assumedGang != null && assumedGang.hasBooster(GangBooster.BoosterType.INCREASED_MINE_CRATES)) {
            chanceModifier += GangModule.readIncreasedMineCratesChanceMod()
        }

        if (!MineCrateHandler.isOnCooldown(event.player)) {
            for (rewardSet in MineCrateHandler.getRewardSets().shuffled()) {
                if (Chance.percent(rewardSet.chance)) {
                    MineCrateHandler.resetCooldown(event.player)

                    // wait a tick before updating the block
                    Tasks.delayed(1L) {
                        val mineCrate = MineCrate(event.block.location, event.player.uniqueId, rewardSet)
                        MineCrateHandler.trackSpawnedCrate(mineCrate)

                        mineCrate.hologram.spawn(event.player)
                    }

                    val user = UserHandler.getUser(event.player.uniqueId)
                    if (user.getSettingOption(UserSetting.REWARD_MESSAGES).getValue()) {
                        event.player.sendMessage("${RewardsModule.getChatPrefix()}You just found a MineCrate!")
                    }

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