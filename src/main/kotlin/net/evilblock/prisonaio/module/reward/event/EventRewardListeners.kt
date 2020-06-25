/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.event

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object EventRewardListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMineBlockBreakEvent(event: MineBlockBreakEvent) {
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