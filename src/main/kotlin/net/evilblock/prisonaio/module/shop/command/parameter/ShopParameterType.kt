/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ShopParameterType : ParameterType<Shop?> {

    override fun transform(sender: CommandSender, source: String): Shop? {
        if (source.equals("__default__", ignoreCase = true)) {
            val defaultShop = ShopHandler.getDefaultShop()
            if (!defaultShop.isPresent) {
                sender.sendMessage("${ChatColor.RED}Couldn't find the default shop.")
            }

            return defaultShop.orElse(null)
        }

        val shop = ShopHandler.getShopById(source)
        if (!shop.isPresent) {
            sender.sendMessage("${ChatColor.RED}Couldn't find a shop by the name `${ChatColor.WHITE}$source${ChatColor.RED}`.")
        }

        return shop.orElse(null)
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        val completions = arrayListOf<String>()
        for (shop in ShopHandler.getShops()) {
            if (shop.id.toLowerCase().startsWith(source.toLowerCase())) {
                completions.add(shop.id)
            }
        }
        return completions
    }

}