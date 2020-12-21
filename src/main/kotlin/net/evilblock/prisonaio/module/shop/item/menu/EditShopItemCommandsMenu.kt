/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.item.menu

import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.EditShopMenu
import org.bukkit.entity.Player

class EditShopItemCommandsMenu(private val shop: Shop, private val shopItem: ShopItem) : TextEditorMenu(lines = shopItem.commands) {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Shop Item Commands"
    }

    override fun onClose(player: Player) {
        Tasks.delayed(1L) {
            EditShopMenu(shop).openMenu(player)
        }
    }

    override fun onSave(player: Player, list: List<String>) {
        shopItem.commands = ArrayList(list)
    }

}