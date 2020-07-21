/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.enchant.menu.BackpackEnchantsMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

class BackpackMenu(private val backpack: Backpack) : PaginatedMenu() {

    private var page: Int = 1

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Backpack"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (slot in backpack.contents) {
            buttons[slot.key] = ItemSlotButton(slot.key)
        }

        return buttons
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = PreviousPageButton()
        buttons[4] = InfoButton()
        buttons[8] = NextPageButton()

        for (i in 0..8) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
            }
        }

        return buttons
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    fun getMaxPages(): Int {
        return ceil(backpack.contents.maxBy { it.key }!!.key / 9.0).toInt()
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
            return "${ChatColor.RED}${ChatColor.BOLD}Backpack Info"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}(ID: #${backpack.id})")
            description.add("")
            description.add("${ChatColor.GRAY}Slots: ${ChatColor.RED}${ChatColor.BOLD}${backpack.contents.size}${ChatColor.GRAY}/${ChatColor.RED}${backpack.getMaxSlots()}")
            description.add("${ChatColor.GRAY}Items: ${ChatColor.RED}${backpack.getItemsSize()}")
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}Enchants")

            if (backpack.enchants.isEmpty()) {
                description.add("${ChatColor.GRAY}None")
            } else {
                description.add("")
            }

            description.add("")
            description.add("${ChatColor.RED}Click to enchant")

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
            if (view.cursor != null) {
                return
            }

            if (!backpack.contents.containsKey(slot)) {
                return
            }

            when (clickType) {
                ClickType.LEFT -> {
                    backpack.contents.remove(slot)
                    view.cursor = backpack.contents[slot]!!.clone()
                    player.updateInventory()
                }
                ClickType.RIGHT -> {
                    val originalItem = backpack.contents[slot]!!
                    if (originalItem.amount == 1) {
                        view.cursor = originalItem
                        backpack.contents.remove(slot)
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

                    val item = backpack.contents.remove(slot)
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