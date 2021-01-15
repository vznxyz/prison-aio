/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.event

import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.impl.WardenArmorSet
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyMine
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.event.RegionBlockBreakEvent
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierType
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
                var tokenMultiplier = when {
                    event.region is MinePartyMine -> {
                        3.0
                    }
                    AbilityArmorHandler.getEquippedSet(event.player)?.hasAbility(WardenArmorSet) == true -> {
                        2.0
                    }
                    else -> {
                        1.0
                    }
                }

                val globalTokenMulti = GlobalMultiplierHandler.getEvent(GlobalMultiplierType.TOKEN)
                if (globalTokenMulti != null) {
                    tokenMultiplier += globalTokenMulti.multiplier
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
                val equippedSet = AbilityArmorHandler.getEquippedSet(event.player)

                val tokenMultiplier = if (equippedSet != null && equippedSet.hasAbility(WardenArmorSet)) {
                    2.0
                } else {
                    1.0
                }

                user.addTokensBalance(((tokensPerBlockBreak * event.blockList.size) * tokenMultiplier).toLong())
            }
        }
    }

}