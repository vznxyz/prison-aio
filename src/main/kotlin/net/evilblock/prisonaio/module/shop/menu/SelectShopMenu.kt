/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

open class SelectShopMenu(
    private val title: String = "Select Shop",
    private val filtered: Collection<Shop> = emptyList(),
    private val select: (Shop) -> Unit
) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return title
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->

            for (shop in ShopHandler.getShops()) {
                if (filtered.contains(shop)) {
                    continue
                }

                buttons[buttons.size] = ShopButton(shop)
            }
        }
    }

    private inner class ShopButton(private val shop: Shop) : Button() {
        override fun getName(player: Player): String {
            return shop.name
        }

        override fun getDescription(player: Player): List<String> {
            return listOf("${ChatColor.GRAY}Click to select this shop")
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()
                select.invoke(shop)
            }
        }
    }

}