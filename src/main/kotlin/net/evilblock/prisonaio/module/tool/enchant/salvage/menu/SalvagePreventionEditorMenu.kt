/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.salvage.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.HelpButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.tool.enchant.salvage.SalvagePreventionHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class SalvagePreventionEditorMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Salvage Prevention"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = GuideButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (pickaxe in SalvagePreventionHandler.getPickaxes()) {
            buttons[buttons.size] = PickaxeButton(pickaxe)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        if (!itemStack.type.name.contains("_PICKAXE")) {
            return false
        }

        SalvagePreventionHandler.trackPickaxe(itemStack)
        SalvagePreventionHandler.saveData()

        return true
    }

    private inner class GuideButton : HelpButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Salvage Prevention"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Insert enchanted pickaxes that",
                "${ChatColor.GRAY}are given to players that",
                "${ChatColor.GRAY}shouldn't be able to be salvaged",
                "${ChatColor.GRAY}without purchasing enchant levels.",
                "",
                "${ChatColor.GRAY}To ${ChatColor.BOLD}insert ${ChatColor.GRAY}a new pickaxe, simply",
                "${ChatColor.YELLOW}${ChatColor.BOLD}shift-click ${ChatColor.GRAY}the item."
            )
        }
    }

    private inner class PickaxeButton(private val itemStack: ItemStack) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder
                .copyOf(itemStack)
                .addToLore(
                    "",
                    "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete pickaxe"
                )
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        SalvagePreventionHandler.forgetPickaxe(itemStack)
                        player.sendMessage("${ChatColor.GREEN}Successfully deleted pickaxe.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

}