/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class SalvagePickaxeMenu(internal val pickaxe: ItemStack) : Menu() {

    init {
        this.updateAfterClick = true
        this.placeholder = true
        this.keepBottomMechanics = false
    }

    override fun getTitle(player: Player): String {
        return ChatColor.GRAY.toString() + "[" + ChatColor.RED + "*" + ChatColor.GRAY + "] " + ChatColor.RED + ChatColor.BOLD + "Salvage Menu " + ChatColor.GRAY + "[" + ChatColor.RED + "*" + ChatColor.GRAY + "]"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        // header buttons
        buttons[0] = TokenShopButton()
        buttons[2] = TokenBalanceButton()
        buttons[4] = PickaxeInHandButton(pickaxe.clone()) // make sure to clone the item
        buttons[6] = PurchaseEnchantsMenuButton()
        buttons[8] = ExitButton()

        // footer buttons
        buttons[49] = RedirectRefundEnchantsMenuButton()

        // middle button
        buttons[22] = ConfirmSalvageButton(this)

        return buttons
    }

    private inner class PurchaseEnchantsMenuButton : Button() {
        override fun getName(player: Player): String {
            return ChatColor.GRAY.toString() + "» " + ChatColor.RED + ChatColor.BOLD + "Purchase Enchants" + ChatColor.GRAY + " «"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(ChatColor.GRAY.toString() + "Click here to purchase enchants")
        }

        override fun getMaterial(player: Player): Material {
            return Material.MAGMA_CREAM
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true) // b = ignoreLevelRestrictions (same as addUnsafeEnchantment)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.LEFT) {
                player.closeInventory()
                PurchaseEnchantMenu(pickaxe).openMenu(player)
            }
        }
    }

}