/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object ShopRenameIdCommand {

    @Command(
        names = ["prison shop rename-id"],
        description = "Rename a shop's ID",
        permission = "op"
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "shop") shop: Shop, @Param(name = "newId") newId: String) {
        ShopHandler.forgetShop(shop)
        shop.id = newId
        ShopHandler.trackShop(shop)
        sender.sendMessage("${ChatColor.GREEN}Renamed shop's ID to `$newId`.")
    }

}