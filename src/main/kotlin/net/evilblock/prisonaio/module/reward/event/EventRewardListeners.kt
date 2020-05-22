package net.evilblock.prisonaio.module.reward.event

import net.evilblock.cubed.util.hook.VaultHook
import net.evilblock.prisonaio.module.reward.RewardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object EventRewardListeners : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val moneyPerBlockBreak = RewardsModule.getMoneyPerBlockBreak()
        if (moneyPerBlockBreak > 0.0) {
            VaultHook.useEconomy { economy -> economy.depositPlayer(event.player, moneyPerBlockBreak.toDouble()) }
        }

        val tokensPerBlockBreak = RewardsModule.getTokensPerBlockBreak()
        if (tokensPerBlockBreak > 0) {
            val user = UserHandler.getUser(event.player.uniqueId)
            user.updateTokensBalance(user.getTokensBalance() + tokensPerBlockBreak)
        }
    }

}