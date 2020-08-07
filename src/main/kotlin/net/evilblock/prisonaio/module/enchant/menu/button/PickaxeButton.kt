/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.util.economy.Economy
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class PickaxeButton(
    private val pickaxeItem: ItemStack,
    private val pickaxeData: PickaxeData,
    private val returnFunc: (Player) -> Unit
) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        val extraLore = arrayListOf<String>()
        extraLore.add("")

        val nextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)
        if (nextPrestige != null) {
            extraLore.add("${ChatColor.RED}${ChatColor.BOLD}PRESTIGE REQUIREMENTS")
            extraLore.addAll(nextPrestige.renderRequirements(player))

            if (nextPrestige.meetsRequirements(player)) {
                extraLore.add("")
                extraLore.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO PRESTIGE")
            }
        } else {
            extraLore.add("${ChatColor.RED}${ChatColor.BOLD}MAXED")
            extraLore.addAll(TextSplitter.split(
                length = 40,
                text = "It appears you've maxed your pickaxe prestige.",
                linePrefix = ChatColor.RED.toString()
            ))
        }

        return ItemBuilder.copyOf(pickaxeItem)
            .addToLore(*extraLore.toTypedArray())
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        val nextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)
        if (nextPrestige != null) {
            if (nextPrestige.meetsRequirements(player)) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        nextPrestige.purchase(player, pickaxeItem, pickaxeData)
                    }

                    returnFunc.invoke(player)
                }.openMenu(player)
            }
        } else {
            player.sendMessage("${ChatColor.RED}It appears you've maxed your pickaxe prestige.")
        }
    }

}