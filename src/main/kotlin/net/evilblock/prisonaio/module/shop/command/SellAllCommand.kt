package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SellAllCommand {

    @Command(names = ["sell", "sellall"], description = "Sell your inventory")
    @JvmStatic
    fun execute(player: Player) {
        try {
            ShopHandler.sellInventory(player, false)
        } catch (e: IllegalStateException) {
            player.sendMessage("${ChatColor.RED}${e.message}!")
        }
    }

}