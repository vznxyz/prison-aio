/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.event

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.event.RegionBlockBreakEvent
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object EventRewardListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onRegionBlockBreakEvent(event: RegionBlockBreakEvent) {
        if (event.region.supportsRewards()) {
            val moneyPerBlockBreak = RewardsModule.getMoneyPerBlockBreak()
            if (moneyPerBlockBreak > 0.0) {
                VaultHook.useEconomy { economy -> economy.depositPlayer(event.player, moneyPerBlockBreak) }
            }

            val tokensPerBlockBreak = RewardsModule.getTokensPerBlockBreak()
            if (tokensPerBlockBreak > 0) {
                val user = UserHandler.getUser(event.player.uniqueId)
                user.addTokensBalance(tokensPerBlockBreak)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        val region = RegionHandler.findRegion(event.block.location)
        if (region.supportsRewards()) {
            val moneyPerBlockBreak = RewardsModule.getMoneyPerBlockBreak()
            if (moneyPerBlockBreak > 0.0) {
                VaultHook.useEconomy { economy -> economy.depositPlayer(event.player, moneyPerBlockBreak * event.blockList.size) }
            }

            val tokensPerBlockBreak = RewardsModule.getTokensPerBlockBreak()
            if (tokensPerBlockBreak > 0) {
                val user = UserHandler.getUser(event.player.uniqueId)
                user.addTokensBalance(tokensPerBlockBreak * event.blockList.size)
            }
        }
    }

}