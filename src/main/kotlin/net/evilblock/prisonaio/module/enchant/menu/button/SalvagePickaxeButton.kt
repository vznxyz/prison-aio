/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.enchant.menu.SalvagePickaxeMenu
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class SalvagePickaxeButton(private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.RED}${ChatColor.BOLD}Salvage Pickaxe ${ChatColor.GRAY}${Constants.DOUBLE_ARROW_LEFT}"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf("${ChatColor.GRAY}Click here to salvage your pickaxe")
    }

    override fun getMaterial(player: Player): Material {
        return Material.ANVIL
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        return itemMeta
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType == ClickType.LEFT) {
            player.closeInventory()
            SalvagePickaxeMenu(pickaxeItem, pickaxeData).openMenu(player)
        }
    }

}