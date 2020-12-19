/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.StaticItemStackButton
import net.evilblock.cubed.menu.pagination.PageButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.menu.BackpackUpgradesMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class BackpackMenu(private val backpack: Backpack) : PaginatedMenu() {

    private var cursor: ItemStack? = null

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "${ChatColor.BLUE}${ChatColor.BOLD}Backpack"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = PageButton(-1, this)
            buttons[8] = PageButton(1, this)

            buttons[4] = InfoButton()
            buttons[6] = ClearItemsButton()
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (item in backpack.contents) {
                buttons[buttons.size] = StaticItemStackButton(item)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun onOpen(player: Player) {
        if (cursor != null) {
            player.openInventory.cursor = cursor
            player.updateInventory()
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (!manualClose) {
            cursor = player.openInventory.cursor
        }
    }

    override fun acceptsInsertedItem(player: Player, itemStack: ItemStack, slot: Int): Boolean {
        return false
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        return false
    }

    override fun acceptsDraggedItems(player: Player, items: Map<Int, ItemStack>): Boolean {
        return false
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.BLUE}${ChatColor.BOLD}Your Backpack"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Items: ${ChatColor.BLUE}${NumberUtils.format(backpack.getItemsSize())}${ChatColor.GRAY}/${ChatColor.BLUE}${NumberUtils.format(backpack.getMaxItemsSize())}")
            description.add("")
            description.add("${ChatColor.BLUE}${ChatColor.BOLD}Upgrades")

            if (backpack.upgrades.isEmpty()) {
                description.add("${ChatColor.GRAY}None")
            } else {
                description.add("")
            }

            description.add("")
            description.add("${ChatColor.YELLOW}Click to purchase upgrades")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                BackpackUpgradesMenu(backpack).openMenu(player)
            }
        }
    }

    private inner class ClearItemsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Clear Items"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Clears all of the items stored in your backpack.", linePrefix = ChatColor.GRAY.toString()))
                desc.add("")
                desc.add("${ChatColor.RED}Click to clear items")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.BARRIER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val backpacks = BackpackHandler.findBackpacksInInventory(player)
            if (!backpacks.containsValue(backpack)) {
                player.sendMessage("${ChatColor.RED}You don't have that backpack in your inventory!")
                return
            }

            if (clickType.isLeftClick) {
                backpack.clearItems()
            }
        }
    }

}