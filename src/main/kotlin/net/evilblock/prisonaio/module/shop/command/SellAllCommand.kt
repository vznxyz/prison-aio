/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.entity.Player

object SellAllCommand {

    @Command(
        names = ["sell", "sellall"],
        description = "Sell your inventory"
    )
    @JvmStatic
    fun execute(player: Player) {
        ShopHandler.sellInventory(player, false)
    }

}