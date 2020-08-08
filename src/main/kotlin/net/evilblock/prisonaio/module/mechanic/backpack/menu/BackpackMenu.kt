/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.InventoryUtils
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.backpack.enchant.menu.BackpackEnchantsMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

class BackpackMenu(private val backpack: Backpack) : Menu() {

    private var page: Int = 1
    private var cursor: ItemStack? = null

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Backpack"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = PreviousPageButton()
        buttons[4] = InfoButton()
        buttons[8] = NextPageButton()

        for (i in 0..8) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
            }
        }

        val range = if (page == 1) {
            0..44
        } else {
            (((page - 1) * 44) + 1)..(page * 44)
        }

        for (i in range) {
            if (backpack.contents.containsKey(i)) {
                buttons[i - ((page - 1) * 45) + 9] = ItemSlotButton(i)
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    fun getMaxPages(): Int {
        return ceil(backpack.getMaxSlots() / 44.0).toInt().coerceAtLeast(1)
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
        if (slot < 9) {
            return false
        }

        if (backpack.contents.containsKey(slot - 9)) {
            return false
        }

        if (BackpackHandler.isBackpackItem(itemStack)) {
            return false
        }

        backpack.contents[slot - 9] = itemStack

        return true
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        if (backpack.contents.size >= backpack.getMaxSlots()) {
            return false
        }

        if (BackpackHandler.isBackpackItem(itemStack)) {
            return false
        }

        val notInserted = backpack.addItem(itemStack)
        if (notInserted != null) {
            InventoryUtils.addAmountToInventory(player.inventory, itemStack, itemStack.amount)
        }

        return true
    }

    override fun acceptsDraggedItems(player: Player, items: Map<Int, ItemStack>): Boolean {
        for (inserted in items) {
            if (inserted.key < 9) {
                return false
            }

            if (backpack.contents.containsKey(inserted.key - 9)) {
                return false
            }

            if (BackpackHandler.isBackpackItem(inserted.value)) {
                return false
            }
        }

        for (inserted in items) {
            backpack.contents[inserted.key - 9] = inserted.value
        }

        return true
    }

    private inner class PreviousPageButton : Button() {
        override fun getName(player: Player): String {
            return if (page > 1) {
                "${ChatColor.RED}${ChatColor.BOLD}Previous Page"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}No Previous Page"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && page > 1) {
                page -= 1
                openMenu(player)
            }
        }
    }

    private inner class NextPageButton : Button() {
        override fun getName(player: Player): String {
            return if (page < getMaxPages()) {
                "${ChatColor.RED}${ChatColor.BOLD}Next Page"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}No Next Page"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && page < getMaxPages()) {
                page += 1
                openMenu(player)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Your Backpack"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}(ID: #${backpack.id})")
            description.add("")
            description.add("${ChatColor.GRAY}Slots: ${ChatColor.RED}${ChatColor.BOLD}${NumberUtils.format(backpack.contents.size)}${ChatColor.GRAY}/${ChatColor.RED}${NumberUtils.format(backpack.getMaxSlots())}")
            description.add("${ChatColor.GRAY}Items: ${ChatColor.RED}${NumberUtils.format(backpack.getItemsSize())}")
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}Enchants")

            if (backpack.enchants.isEmpty()) {
                description.add("${ChatColor.GRAY}None")
            } else {
                description.add("")
            }

            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}CLICK TO BUY ENCHANTS")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                BackpackEnchantsMenu(backpack).openMenu(player)
            }
        }
    }

    private inner class ItemSlotButton(private val slot: Int) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return if (backpack.contents.containsKey(slot)) {
                backpack.contents[slot]!!.clone()
            } else {
                ItemStack(Material.AIR)
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (view.cursor != null && view.cursor.type != Material.AIR) {
                return
            }

            val adjustedSlot = ((page - 1) * 45) + slot - 9

            if (!backpack.contents.containsKey(adjustedSlot)) {
                return
            }

            when (clickType) {
                ClickType.LEFT -> {
                    val pickup = backpack.contents.remove(adjustedSlot)!!
                    view.cursor = pickup.clone()
                    player.updateInventory()
                }
                ClickType.RIGHT -> {
                    val originalItem = backpack.contents[adjustedSlot]!!
                    if (originalItem.amount == 1) {
                        view.cursor = originalItem
                        backpack.contents.remove(adjustedSlot)
                    } else {
                        val halved = (originalItem.amount / 2.0).toInt()
                        originalItem.amount = originalItem.amount - halved

                        val halvedStack = originalItem.clone()
                        halvedStack.amount = halved

                        view.cursor = halvedStack
                    }

                    player.updateInventory()
                }
                ClickType.SHIFT_LEFT,
                ClickType.SHIFT_RIGHT -> {
                    if (player.inventory.firstEmpty() == -1) {
                        return
                    }

                    val item = backpack.contents.remove(adjustedSlot)
                    if (item != null) {
                        player.inventory.addItem(item.clone())
                        player.updateInventory()
                    }
                }
                else -> {}
            }
        }
    }

}