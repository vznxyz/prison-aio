package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.entity.Player

object SellAllCommand {

    @Command(names = ["sell", "sellall"], description = "Sell your inventory")
    @JvmStatic
    fun execute(player: Player) {
        ShopHandler.sellInventory(player, false)
    }

}