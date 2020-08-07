/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ShopResetPrioritiesCommand {

    @Command(
        names = ["shop reset-priorities"],
        description = "Reset shop priorities",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
        val singleLetterShops = ShopHandler.getShops().filter { it.id.length == 1 }

        for (shop in ShopHandler.getShops()) {
            shop.priority = singleLetterShops.size + 1
        }

        for ((index, shop) in singleLetterShops.sortedBy { it.id }.withIndex()) {
            shop.priority = index
        }

        ShopHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Finished!")
    }

}