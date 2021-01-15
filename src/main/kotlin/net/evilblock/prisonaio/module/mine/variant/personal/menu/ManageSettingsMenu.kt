/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMine
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineConfig
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.NumberFormatException

class ManageSettingsMenu(private val previous: Menu, private val mine: PrivateMine) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Settings"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[11] = PlayersMenuButton(mine)
        buttons[13] = PublicAccessButton(mine)
        buttons[15] = EditTaxButton(mine)

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                previous.openMenu(player)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 27
    }

    private class PlayersMenuButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Manage Access"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(text = "Grant or revoke access to players."))
            description.add("")
            description.add("${ChatColor.YELLOW}Click to manage access")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            (itemMeta as SkullMeta).owner = player.name
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            ManageAccessMenu(mine).openMenu(player)
        }
    }

    private class PublicAccessButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            val state = if (mine.public) { "ON" } else "OFF"
            val color = if (mine.public) { ChatColor.GREEN } else { ChatColor.RED }

            return "${ChatColor.YELLOW}${ChatColor.BOLD}Public Access: ${color}${state}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(text = "This setting controls who can access your private mine."))
            description.add("")
            description.add("${ChatColor.YELLOW}Click to toggle public access")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOD_DOOR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            mine.public = !mine.public
        }
    }

    private class EditTaxButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Sales Tax: ${ChatColor.GRAY}${mine.salesTax}%"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(text = "Control the sales tax that players must contribute to you in return for using your private mine."))
            description.add("")
            description.add("${ChatColor.YELLOW}Click to edit the sales tax")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            NumberPrompt()
                .withText("${ChatColor.GREEN}Please input a new sales tax percentage. ${ChatColor.GRAY}(Accepted range is 0.0-${PrivateMineConfig.salesTaxRange.maximumDouble}%)")
                .acceptInput { input ->
                    try {
                        val inputNumber = input.toDouble()
                        if (!PrivateMineConfig.salesTaxRange.containsDouble(inputNumber)) {
                            player.sendMessage("${ChatColor.RED}The input you entered is not within the accept range.")
                            return@acceptInput
                        }

                        mine.salesTax = inputNumber

                        Tasks.async {
                            PrivateMineHandler.saveData()
                        }

                        player.sendMessage("${ChatColor.GREEN}You updated your Private Mine's sales tax to ${mine.salesTax}%.")
                    } catch (e: NumberFormatException) {
                        player.sendMessage("${ChatColor.RED}The input \"$input\" isn't a valid number.")
                    }
                }
                .start(player)
        }
    }
}