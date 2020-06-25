/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.privatemine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.privatemine.PrivateMinesModule
import net.evilblock.prisonaio.module.privatemine.PrivateMine
import net.evilblock.prisonaio.module.privatemine.PrivateMineHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.NumberFormatException

class SettingsMenu(private val mine: PrivateMine) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return mine.translateVariables(PrivateMinesModule.getMenuTitle("settings-menu"))
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[11] = PlayersMenuButton(mine)
        buttons[13] = PublicAccessButton(mine)
        buttons[15] = TaxButton(mine)

        for (i in 0..26) {
            if (!buttons.containsKey(i)) {
                if (BORDER_SLOTS.contains(i)) {
                    buttons[i] = Button.Companion.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
                } else {
                    buttons[i] = Button.Companion.placeholder(Material.STAINED_GLASS_PANE, 10, " ")
                }
            }
        }

        return buttons
    }

    companion object {
        private val BORDER_SLOTS = listOf(0, 8, 9, 17, 18, 26)
    }

    private class PlayersMenuButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            return PrivateMinesModule.getButtonTitle("settings-menu", "players")
        }

        override fun getDescription(player: Player): List<String> {
            return PrivateMinesModule.getButtonLore("settings-menu", "players")
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            (itemMeta as SkullMeta).owner = player.name
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            PlayersMenu(mine).openMenu(player)
        }
    }

    private class PublicAccessButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            val state = if (mine.public) { "ON" } else "OFF"
            val color = if (mine.public) { ChatColor.GREEN } else { ChatColor.RED }

            return PrivateMinesModule.getButtonTitle("settings-menu", "public-access")
                    .replace("{state}", state)
                    .replace("{stateWithColor}", color.toString() + state)
        }

        override fun getDescription(player: Player): List<String> {
            val state = if (mine.public) { "ON" } else "OFF"
            val color = if (mine.public) { ChatColor.GREEN } else { ChatColor.RED }

            return PrivateMinesModule.getButtonLore("settings-menu", "public-access").map {
                it.replace("{state}", state)
                it.replace("{stateWithColor}", color.toString() + state)
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.WOOD_DOOR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            mine.public = !mine.public
        }
    }

    private class TaxButton(private val mine: PrivateMine) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Sales Tax: ${ChatColor.GRAY}${mine.salesTax}%"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                    "",
                    "${ChatColor.YELLOW}Click to edit the sales tax."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            EzPrompt.Builder()
                .promptText("${ChatColor.GREEN}Please input the new sales tax.\n${ChatColor.GRAY}Accepted range for this tier is ${ChatColor.GREEN}${mine.tier.salesTaxRange.minimumDouble}-${mine.tier.salesTaxRange.maximumDouble}%${ChatColor.GRAY}.")
                .acceptInput { player, input ->
                    try {
                        val inputNumber = input.toDouble()
                        if (!mine.tier.salesTaxRange.containsDouble(inputNumber)) {
                            player.sendMessage("${ChatColor.RED}The input you entered is not within the accept range.")
                            return@acceptInput
                        }

                        mine.salesTax = inputNumber
                        PrivateMineHandler.saveGrid()
                        player.sendMessage("${ChatColor.GREEN}You updated your Tier ${mine.tier.number} Private Mine's sales tax to ${mine.salesTax}%.")
                    } catch (e: NumberFormatException) {
                        player.sendMessage("${ChatColor.RED}The input \"$input\" isn't a valid number.")
                    }
                }
                .build()
                .start(player)
        }
    }
}