/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.shop.item.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.shop.Shop
import net.evilblock.prisonaio.module.shop.ShopHandler
import net.evilblock.prisonaio.module.shop.item.ShopItem
import net.evilblock.prisonaio.module.shop.menu.EditShopMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditShopItemMenu(private val shop: Shop, private val item: ShopItem) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Shop Item"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[1] = EditItemButton()
        buttons[3] = ToggleGiveItemButton()
        buttons[5] = EditCommandsButton()

        for (i in 0 until 9) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(15)
            }
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditShopMenu(shop).openMenu(player)
            }
        }
    }

    private inner class EditItemButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Item"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Update the item that is given to players when they purchase this shop item.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit item")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu { selected ->
                    item.itemStack = selected

                    Tasks.async {
                        ShopHandler.saveData()
                    }

                    this@EditShopItemMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class ToggleGiveItemButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Toggle Give Item"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "If the item should be given to a player when they purchase this shop item.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")

            if (item.giveItem) {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}Currently giving item")
            } else {
                description.add("${ChatColor.RED}${ChatColor.BOLD}Currently not giving item")
            }

            description.add("")

            if (item.giveItem) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to disable give item")
            } else {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enable give item")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            item.giveItem = !item.giveItem

            Tasks.async {
                ShopHandler.saveData()
            }
        }
    }

    private inner class EditCommandsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Commands"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "The commands that are executed by console when a player purchases this shop item.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit commands")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.COMMAND_REPEATING
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditShopItemCommandsMenu(shop, item).openMenu(player)
            }
        }
    }

}