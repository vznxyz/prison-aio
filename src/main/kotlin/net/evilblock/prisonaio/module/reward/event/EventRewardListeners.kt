/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.event

import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
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
            val user = UserHandler.getUser(event.player.uniqueId)

            val moneyPerBlockBreak = RewardsModule.getMoneyPerBlockBreak()
            if (moneyPerBlockBreak > 0.0) {
                user.addMoneyBalance(moneyPerBlockBreak)
            }

            val tokensPerBlockBreak = RewardsModule.getTokensPerBlockBreak()
            if (tokensPerBlockBreak > 0) {
                val tokenMultiplier = if (AbilityArmorHandler.getEquippedSet(event.player) != null) {
                    2.0
                } else {
                    1.0
                }

                user.addTokensBalance((tokensPerBlockBreak * tokenMultiplier).toLong())
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
        val region = RegionHandler.findRegion(event.block.location)
        if (region.supportsRewards()) {
            val user = UserHandler.getUser(event.player.uniqueId)

            val moneyPerBlockBreak = RewardsModule.getMoneyPerBlockBreak()
            if (moneyPerBlockBreak > 0.0) {
                user.addMoneyBalance(moneyPerBlockBreak * event.blockList.size)
            }

            val tokensPerBlockBreak = RewardsModule.getTokensPerBlockBreak()
            if (tokensPerBlockBreak > 0) {
                val tokenMultiplier = if (AbilityArmorHandler.getEquippedSet(event.player) != null) {
                    2.0
                } else {
                    1.0
                }

                user.addTokensBalance(((tokensPerBlockBreak * event.blockList.size) * tokenMultiplier).toLong())
            }
        }
    }

}