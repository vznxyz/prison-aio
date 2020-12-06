/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class ToggleEnchantMessagesMenu(private val user: User) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Toggle Enchant Messages"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0..7) {
                buttons[i] = GlassButton(7)
            }

            buttons[8] = ToggleAllButton()

            for ((index, enchant) in EnchantsManager.getRegisteredEnchants().withIndex()) {
                buttons[9 + index] = ToggleEnchantButton(enchant)
            }
        }
    }

    private inner class ToggleEnchantButton(private val enchant: AbstractEnchant) : Button() {
        override fun getName(player: Player): String {
            return if (user.settings.isEnchantMessagesDisabled(enchant)) {
                "${ChatColor.GREEN}${ChatColor.BOLD}${enchant.enchant}"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${enchant.enchant}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return if (user.settings.isEnchantMessagesDisabled(enchant)) {
                TextSplitter.split(text = "Click to enable ${enchant.enchant} messages.")
            } else {
                TextSplitter.split(text = "Click to disable ${enchant.enchant} messages.")
            }
        }
    }

    private inner class ToggleAllButton : Button() {
        override fun getName(player: Player): String {
            val anyEnabled = EnchantsManager.getRegisteredEnchants().any { !user.settings.isEnchantMessagesDisabled(it) }
            return if (anyEnabled) {
                "${ChatColor.RED}${ChatColor.BOLD}Disable All"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}Enable All"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val anyEnabled = EnchantsManager.getRegisteredEnchants().any { !user.settings.isEnchantMessagesDisabled(it) }
            return if (anyEnabled) {
                TextSplitter.split(text = "Click to disable all enchant messages.")
            } else {
                TextSplitter.split(text = "Click to enable all enchant messages.")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val anyEnabled = EnchantsManager.getRegisteredEnchants().any { !user.settings.isEnchantMessagesDisabled(it) }
            if (anyEnabled) {
                for (enchant in EnchantsManager.getRegisteredEnchants()) {
                    if (!user.settings.isEnchantMessagesDisabled(enchant)) {
                        user.settings.toggleEnchantMessages(enchant)
                    }
                }
            } else {
                for (enchant in EnchantsManager.getRegisteredEnchants()) {
                    if (user.settings.isEnchantMessagesDisabled(enchant)) {
                        user.settings.toggleEnchantMessages(enchant)
                    }
                }
            }
        }
    }

}