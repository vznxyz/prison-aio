/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.menus.ExitButton
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.prompt.MineEditResetIntervalPrompt
import net.evilblock.prisonaio.module.mine.variant.normal.NormalMine
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class MineManageResetMenu(private val mine: NormalMine) : Menu() {

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Reset - ${mine.id}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        // toolbar
        buttons[0] = BackButton { _player -> MineEditMenu(mine).openMenu(_player) }
        buttons[8] = ExitButton()

        // toolbar separator
        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
        }

        // first row
        buttons[30] = ResetNowButton()
        buttons[32] = EditIntervalButton()

        // second row
        buttons[38] = ToggleResetMessagesButton()
        buttons[40] = ToggleBroadcastButton()
        buttons[42] = ToggleIntervalMessagesButton()

        // make empty row on bottom
        buttons[53] = Button.placeholder(Material.AIR)

        return buttons
    }

    private inner class ResetNowButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Reset Now"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Force resets the mine when clicked."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.STONE_BUTTON
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            try {
                Tasks.async {
                    mine.resetRegion()
                }

                player.sendMessage("${ChatColor.GREEN}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.GREEN}has been forcefully reset.")
            } catch (e: Exception) {
                player.sendMessage("${ChatColor.RED}${e.message}.")
            }
        }
    }

    private inner class EditIntervalButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Reset Interval: ${ChatColor.GRAY}${TimeUtil.formatIntoAbbreviatedString(mine.resetConfig.resetInterval)}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The interval between each mine reset.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit the interval"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                // close menu
                player.closeInventory()

                // start conversation
                ConversationUtil.startConversation(player, MineEditResetIntervalPrompt(mine))
            }
        }
    }

    private inner class ToggleResetMessagesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Reset Messages Enabled: ${ChatColor.GRAY}${mine.resetConfig.resetMessagesEnabled}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}If any messages are sent to players about",
                "${ChatColor.GRAY}the mine reset.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to toggle"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                // update the mine's reset config
                mine.resetConfig.resetMessagesEnabled = !mine.resetConfig.resetMessagesEnabled

                // save changes to file
                MineHandler.saveData()

                // send update message
                if (mine.resetConfig.resetMessagesEnabled) {
                    player.sendMessage("${ChatColor.GREEN}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.GREEN}will now send reset messages.")
                } else {
                    player.sendMessage("${ChatColor.RED}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.RED}will no longer send reset messages.")
                }
            }
        }
    }

    private inner class ToggleBroadcastButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Broadcast Messages Enabled: ${ChatColor.GRAY}${mine.resetConfig.broadcastResetMessages}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}If reset messages are broadcast to all players",
                "${ChatColor.GRAY}instead of only to players nearby this mine.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to toggle"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                // update the mine's reset config
                mine.resetConfig.broadcastResetMessages = !mine.resetConfig.broadcastResetMessages

                // save changes to file
                MineHandler.saveData()

                // send update message
                if (mine.resetConfig.broadcastResetMessages) {
                    player.sendMessage("${ChatColor.GREEN}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.GREEN}will now broadcast reset messages.")
                } else {
                    player.sendMessage("${ChatColor.RED}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.RED}will no longer broadcast reset messages.")
                }
            }
        }
    }

    private inner class ToggleIntervalMessagesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Interval Messages Enabled: ${ChatColor.GRAY}${mine.resetConfig.intervalMessagesEnabled}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}If messages counting down the time until",
                "${ChatColor.GRAY}the next reset occurs are sent.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to toggle"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                // update the mine's reset config
                mine.resetConfig.intervalMessagesEnabled = !mine.resetConfig.intervalMessagesEnabled

                // save changes to file
                MineHandler.saveData()

                // send update message
                if (mine.resetConfig.intervalMessagesEnabled) {
                    player.sendMessage("${ChatColor.GREEN}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.GREEN}will now send interval messages.")
                } else {
                    player.sendMessage("${ChatColor.RED}Mine ${ChatColor.WHITE}${mine.id} ${ChatColor.RED}will no longer send interval messages.")
                }
            }
        }
    }

}