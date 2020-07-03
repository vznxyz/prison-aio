/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.cell.CellsModule
import net.evilblock.prisonaio.module.cell.entity.JerryNpcEntity
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class JerryMenu(private val jerry: JerryNpcEntity) : Menu() {

    override fun getTitle(player: Player): String {
        return "Jerry The Prison Guard"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[10] = AnnouncementButton()
        buttons[13] = AchievementsButton()
        buttons[16] = MoveLocationButton()

        buttons[28] = SettingsButton()
        buttons[31] = MembersButton()

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    private inner class AnnouncementButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Cell Announcement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            for (line in TextSplitter.split(40, jerry.cell.announcement, "${ChatColor.GRAY}", "")) {
                description.add(line)
            }

            description.add("")
            description.add("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Server News")
            description.addAll(CellsModule.getJerryChangeLog())

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK_AND_QUILL
        }
    }

    private inner class AchievementsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Cell Achievements"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}Coming soon!")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOAT
        }
    }

    private inner class MoveLocationButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Location"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Move Jerry's location by",
                "${ChatColor.GRAY}using the Mover Tool.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to move Jerry"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.MONSTER_EGG
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemUtils.setMonsterEggType(super.getButtonItem(player), EntityType.VILLAGER)
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (player.inventory.firstEmpty() == -1) {
                player.sendMessage("${ChatColor.RED}You need a free inventory space to pickup Jerry!")
                return
            }

            jerry.updateVisibility(hidden = true)

            val builder = ItemBuilder.of(Material.MONSTER_EGG)
                .name("${ChatColor.YELLOW}${ChatColor.BOLD}Move Jerry")
                .addToLore(
                    HiddenLore.encodeString("${jerry.uuid}"),
                    "${ChatColor.GRAY}Place this mob egg wherever",
                    "${ChatColor.GRAY}you'd like to move Jerry to."
                )

            val itemStack = ItemUtils.setMonsterEggType(builder.build(), EntityType.VILLAGER)

            player.closeInventory()
            player.inventory.addItem(itemStack)
            player.updateInventory()
            player.sendMessage("${ChatColor.GREEN}I've given you an egg, place this wherever you'd like me to move to.")
        }
    }

    private inner class SettingsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Settings"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Manage your cell's settings",
                "${ChatColor.GRAY}and permissions.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to manage settings"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.ANVIL
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!jerry.cell.isOwner(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the owner of the cell to manage its settings.")
                return
            }

            ManageSettingsMenu(jerry.cell).openMenu(player)
        }
    }

    private inner class MembersButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Members"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}View and manage your cell's members.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to manage members"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!jerry.cell.isOwner(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the owner of the cell to manage its members.")
                return
            }

            ManageMembersMenu(jerry.cell).openMenu(player)
        }

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)
            val meta = item.itemMeta as SkullMeta
            meta.owner = player.name
            item.itemMeta = meta
            return item
        }
    }

}