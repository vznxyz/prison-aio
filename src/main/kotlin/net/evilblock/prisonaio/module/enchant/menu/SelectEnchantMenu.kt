/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectEnchantMenu(
    private val filtered: Collection<AbstractEnchant> = emptyList(),
    private val select: (AbstractEnchant) -> Unit
) : Menu() {

    override fun getTitle(player: Player): String {
        return "Select Enchant"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (enchant in EnchantsManager.getRegisteredEnchants().sortedWith(EnchantsManager.ENCHANT_COMPARATOR)) {
            if (filtered.contains(enchant)) {
                continue
            }

            buttons[buttons.size] = EnchantButton(enchant)
        }

        return buttons
    }

    private inner class EnchantButton(private val enchant: AbstractEnchant) : Button() {
        override fun getName(player: Player): String {
            return "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf("${ChatColor.GRAY}Click to select this enchant")
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()
                select.invoke(enchant)
            }
        }
    }

}