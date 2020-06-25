/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.rank.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.cubed.util.bukkit.prompt.PricePrompt
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditRankMenu(val rank: Rank) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Rank - ${rank.displayName}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        buttons[1] = EditNameButton()
        buttons[3] = EditOrderButton()
        buttons[5] = EditPriceButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                RankEditorMenu().openMenu(player)
            }, 1L)
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The name is how you want the rank",
                "${ChatColor.GRAY}to appear in chat and menu text.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit the name"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            player.closeInventory()

            EzPrompt.Builder()
                .promptText("${ChatColor.GREEN}Please input a new name. ${ChatColor.GRAY}(Colors supported)")
                .acceptInput { player, input ->
                    if (input.length >= 32) {
                        player.sendMessage("${ChatColor.RED}Rank name is too long! (${input.length} > 32 characters)")
                        return@acceptInput
                    }

                    rank.displayName = ChatColor.translateAlternateColorCodes('&', input)
                    RankHandler.saveData()

                    player.sendMessage("${ChatColor.GREEN}Successfully updated name of rank.")

                    openMenu(player)
                }
                .build()
                .start(player)
        }
    }

    private inner class EditOrderButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Sort Order ${ChatColor.GRAY}(${rank.sortOrder})"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Sort order is how the rank structure",
                "${ChatColor.GRAY}is determined.",
                "",
                "${ChatColor.GRAY}The starting rank is the rank with the",
                "${ChatColor.GRAY}lowest sort order. The last rank is the",
                "${ChatColor.GRAY}rank with the highest sort order.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase order by +1",
                "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease order by -1",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase order by +10",
                "${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease order by -10"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = if (clickType.isShiftClick) 10 else 1
            if (clickType.isLeftClick) {
                rank.sortOrder += mod
            } else if (clickType.isRightClick) {
                rank.sortOrder = 0.coerceAtLeast(rank.sortOrder - mod)
            }

            RankHandler.saveData()
        }
    }

    private inner class EditPriceButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Price"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The current amount of money it costs",
                "${ChatColor.GRAY}to rankup to this rank is ${ChatColor.AQUA}$${ChatColor.GREEN}${NumberUtils.format(rank.price)}${ChatColor.GRAY}.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update the price"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.GOLD_INGOT
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()

                PricePrompt { price ->
                    rank.price = price.toLong()
                    RankHandler.saveData()

                    player.sendMessage("${ChatColor.GREEN}Successfully updated price of rank.")

                    openMenu(player)
                }.start(player)
            }
        }
    }

}