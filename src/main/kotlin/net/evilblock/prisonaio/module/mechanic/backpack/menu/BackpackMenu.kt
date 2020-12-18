/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.StaticItemStackButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import net.evilblock.prisonaio.module.mechanic.backpack.upgrade.menu.BackpackUpgradesMenu
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

        buttons[0] = PageButton(-1)
//        buttons[2] = BlockFilterButton()
        buttons[4] = InfoButton()
        buttons[6] = ClearItemsButton()
        buttons[8] = PageButton(1)

        for (i in 0..8) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(7)
            }
        }

        val range = if (page == 1) {
            0 .. (44.coerceAtMost(backpack.contents.size))
        } else {
            val last = (page * 44).coerceAtMost(backpack.contents.size)
            (last - 44).coerceAtLeast(0) .. last
        }

        for (i in range) {
            if (i >= backpack.contents.size) {
                break
            }

            val item = backpack.contents[i]
            buttons[i - ((page - 1) * 45) + 9] = StaticItemStackButton(item)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    fun getMaxPages(): Int {
        return ceil((backpack.getMaxItemsSize() / 64.0) / 44.0).toInt().coerceAtLeast(1)
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

    private inner class PageButton(private val mod: Int) : Button() {
        override fun getName(player: Player): String {
            return if (mod > 0) {
                if (page < getMaxPages()) {
                    "${ChatColor.YELLOW}${ChatColor.BOLD}Next Page"
                } else {
                    "${ChatColor.GRAY}${ChatColor.BOLD}No Next Page"
                }
            } else {
                if (page > 1) {
                    "${ChatColor.RED}${ChatColor.BOLD}Previous Page"
                } else {
                    "${ChatColor.GRAY}${ChatColor.BOLD}No Previous Page"
                }
            }
        }

        override fun getDamageValue(player: Player): Byte {
            return 3.toByte()
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getButtonItem(player: Player): ItemStack {
            val texture = if (mod > 0) {
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"
            } else {
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="
            }

            return ItemUtils.applySkullTexture(super.getButtonItem(player), texture)
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (mod > 0) {
                if (clickType.isLeftClick && page < getMaxPages()) {
                    page += 1
                    openMenu(player)
                }
            } else {
                if (clickType.isLeftClick && page > 1) {
                    page -= 1
                    openMenu(player)
                }
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Your Backpack"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Items: ${ChatColor.RED}${NumberUtils.format(backpack.getItemsSize())}${ChatColor.GRAY}/${ChatColor.RED}${NumberUtils.format(backpack.getMaxItemsSize())}")
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}Upgrades")

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