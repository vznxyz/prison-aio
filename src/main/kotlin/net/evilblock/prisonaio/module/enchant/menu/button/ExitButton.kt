/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ExitButton : Button() {

    override fun getName(player: Player): String {
        return ChatColor.GRAY.toString() + "» " + ChatColor.RED + ChatColor.BOLD + "Exit" + ChatColor.GRAY + " «"
    }

    override fun getDescription(player: Player): List<String> {
        return emptyList()
    }

    override fun getMaterial(player: Player): Material {
        return Material.BARRIER
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        val currentlyOpened: Menu? = Menu.currentlyOpenedMenus[player.uniqueId]
        if (currentlyOpened != null) {
            currentlyOpened.manualClose = true
        }

        player.closeInventory()
    }

}