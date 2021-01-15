/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.shop.Shop
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object OpenShopCommand {

    @Command(
        names = ["shop", "shop open", "openshop"],
        description = "Opens a Shop Menu by ID"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "shop", defaultValue = "__default__") shop: Shop) {
        if (CombatTimerHandler.isOnTimer(player)) {
            player.sendMessage("${ChatColor.RED}You can't open menus while your combat timer is active!")
            return
        }

        if (shop.hasAccess(player)) {
            shop.openMenu(player)
        } else {
            player.sendMessage("${ChatColor.RED}You don't have access to the ${shop.name}${ChatColor.RED} shop.")
        }
    }

}