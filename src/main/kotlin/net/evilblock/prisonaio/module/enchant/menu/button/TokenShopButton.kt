package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class TokenShopButton : Button() {

    override fun getName(player: Player): String {
        return ChatColor.GRAY.toString() + "» " + ChatColor.GOLD + ChatColor.BOLD + "Token Shop" + ChatColor.GRAY + " «"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf(ChatColor.GRAY.toString() + "Click to view the token shop")
    }

    override fun getMaterial(player: Player): Material {
        return Material.ENCHANTED_BOOK
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        player.sendMessage(ChatColor.GOLD.toString() + "Opening the Token Shop...")
        player.closeInventory()
        player.performCommand("tokenshop")
    }

}