/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.menu.template

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButtonFactory
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.EditShopMenu
import net.evilblock.prisonaio.module.shop.serialize.ShopReferenceSerializer
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class ShopMenuTemplate(id: String, @JsonAdapter(ShopReferenceSerializer::class) internal var shop: Shop) : MenuTemplate<ShopItem>(id) {

    override fun getName(): String {
        return shop.name
    }

    override fun getListEntries(): List<ShopItem> {
        return shop.items.filter { it.selling }
    }

    override fun createEntryButton(entry: ShopItem): Button {
        return ShopItemButton(entry)
    }

    override fun getTemplateButtonFactories(): List<MenuTemplateButtonFactory> {
        return emptyList()
    }

    override fun onEditorClose(player: Player) {
        Tasks.delayed(1L) {
            EditShopMenu(shop).openMenu(player)
        }
    }

    override fun getAbstractType(): Type {
        return ShopMenuTemplate::class.java
    }

    private inner class ShopItemButton(private val shopItem: ShopItem) : Button() {
        override fun getName(player: Player): String {
            return formatItemName(shopItem.itemStack)
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (shopItem.itemStack.hasItemMeta() && shopItem.itemStack.itemMeta.hasLore()) {
                description.addAll(shopItem.itemStack.itemMeta.lore)
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to buy x1 for ${Formats.formatMoney(shopItem.sellPricePerUnit)}")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.GREEN}to buy x64 for ${Formats.formatMoney(shopItem.sellPricePerUnit * 64)}")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}SHIFT-CLICK ${ChatColor.YELLOW}to buy custom amount")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return shopItem.itemStack.type
        }

        override fun getDamageValue(player: Player): Byte {
            return shopItem.itemStack.durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {

            }
        }
    }

    companion object {
        fun formatItemName(itemStack: ItemStack): String {
            return if (itemStack.itemMeta.hasDisplayName()) {
                itemStack.itemMeta.displayName
            } else {
                "${ChatColor.AQUA}${ItemUtils.getName(itemStack)}"
            }
        }
    }

}