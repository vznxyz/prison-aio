/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.menu.template

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButtonFactory
import net.evilblock.cubed.util.bukkit.InventoryUtils
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.EditShopMenu
import net.evilblock.prisonaio.module.shop.receipt.ShopReceiptItem
import net.evilblock.prisonaio.module.shop.serialize.ShopReferenceSerializer
import net.evilblock.prisonaio.module.shop.transaction.TransactionResult
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.ChatColor
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
        return shop.items.filter { it.isSelling() }.sortedBy { it.order }
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
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(shopItem.itemStack).let {
                it.addToLore(
                    "",
                    "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to buy x1 for ${shop.currency.format(shopItem.sellPricePerUnit)}"
                )

                if (shop.currency == Currency.Type.MONEY) {
                    it.addToLore(
                        "${ChatColor.YELLOW}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.YELLOW}to buy x64 for ${shop.currency.format(shopItem.sellPricePerUnit * 64)}",
                        "${ChatColor.AQUA}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.AQUA}to buy custom amount"
                    )
                }

                it.build()
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && clickType.isShiftClick && shop.currency == Currency.Type.MONEY) {
                NumberPrompt("${ChatColor.GREEN}Please input a quantity.") { quantity ->
                    if (quantity.toInt() < 0) {
                        player.sendMessage("${ChatColor.RED}Quantity must be more than 0.")
                        return@NumberPrompt
                    }

                    ConfirmMenu("Purchase x${quantity}?") { confirmed ->
                        if (confirmed) {
                            val maxItemsSize = InventoryUtils.getMaxItemsSize(player, shopItem.itemStack)
                            if (quantity.toInt() >= maxItemsSize) {
                                player.sendMessage("${ChatColor.RED}You don't have enough space in your inventory to purchase that many items.")
                                return@ConfirmMenu
                            }

                            val receipt = shop.buyItems(player, setOf(ShopReceiptItem(shopItem, ItemBuilder.copyOf(shopItem.itemStack).amount(quantity.toInt()).build())))
                            if (receipt.result != TransactionResult.SUCCESS) {
                                player.sendMessage("${ChatColor.RED}${receipt.result.defaultMessage}!")
                            }
                        }

                        shop.openMenu(player)
                    }.openMenu(player)
                }.start(player)

                return
            }

            if (clickType.isLeftClick) {
                val receipt = shop.buyItems(player, setOf(ShopReceiptItem(shopItem, ItemBuilder.copyOf(shopItem.itemStack).amount(1).build())))
                if (receipt.result != TransactionResult.SUCCESS) {
                    player.sendMessage("${ChatColor.RED}${receipt.result.defaultMessage}!")
                }
                return
            }

            if (clickType.isRightClick && shop.currency == Currency.Type.MONEY) {
                val receipt = shop.buyItems(player, setOf(ShopReceiptItem(shopItem, ItemBuilder.copyOf(shopItem.itemStack).amount(64).build())))
                if (receipt.result != TransactionResult.SUCCESS) {
                    player.sendMessage("${ChatColor.RED}${receipt.result.defaultMessage}!")
                }
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