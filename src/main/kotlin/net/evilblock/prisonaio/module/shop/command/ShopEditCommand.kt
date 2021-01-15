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
import net.evilblock.prisonaio.module.shop.menu.EditShopMenu
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.entity.Player

object ShopEditCommand {

    @Command(
        names = ["prison shop edit"],
        description = "Opens the Shop Editor for a specific shop",
        permission = Permissions.SHOP_EDITOR
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "shop") shop: Shop) {
        EditShopMenu(shop).openMenu(player)
    }

}