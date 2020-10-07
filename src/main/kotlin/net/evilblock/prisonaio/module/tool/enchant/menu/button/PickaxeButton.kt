/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeHandler
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
            extraLore.add("${ChatColor.RED}${ChatColor.BOLD}Prestige Requirements")
            extraLore.addAll(nextPrestige.renderRequirements(player, pickaxeData))

            if (nextPrestige.meetsRequirements(player, pickaxeData)) {
                extraLore.add("")
                extraLore.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO PRESTIGE")
            }
        } else {
            extraLore.add("${ChatColor.RED}${ChatColor.BOLD}MAXED")
            extraLore.addAll(TextSplitter.split(text = "It appears you've maxed your pickaxe prestige.", linePrefix = ChatColor.RED.toString()))
        }

        return ItemBuilder.copyOf(pickaxeItem)
            .addToLore(*extraLore.toTypedArray())
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        val nextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)
        if (nextPrestige != null) {
            if (!nextPrestige.meetsRequirements(player, pickaxeData)) {
                player.sendMessage("${ChatColor.RED}You don't meet the requirements to prestige your pickaxe!")
                return
            }

            ConfirmMenu { confirmed ->
                if (confirmed) {
                    nextPrestige.purchase(player, pickaxeItem, pickaxeData)

                    val newNextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)

                    player.sendMessage("")
                    player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Acquired Pickaxe Prestige ${pickaxeData.prestige}")
                    player.sendMessage(" ${ChatColor.GRAY}Your pickaxe has reached the next prestige!")

                    if (newNextPrestige != null) {
                        if (newNextPrestige.enchantLimits.isNotEmpty()) {
                            player.sendMessage("")
                            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}NEW ENCHANT LIMITS")

                            for ((enchant, level) in newNextPrestige.enchantLimits.entries.sortedWith(EnchantsManager.MAPPED_ENCHANT_COMPARATOR)) {
                                if (!nextPrestige.enchantLimits.containsKey(enchant) || nextPrestige.enchantLimits.getValue(enchant) != level) {
                                    player.sendMessage(" ${enchant.lorified()} ${ChatColor.GRAY}${NumberUtils.format(level)}")
                                }
                            }
                        }
                    }

                    player.sendMessage("")
                }

                returnFunc.invoke(player)
            }.openMenu(player)
        } else {
            player.sendMessage("${ChatColor.RED}It appears you've maxed your pickaxe prestige.")
        }
    }

}