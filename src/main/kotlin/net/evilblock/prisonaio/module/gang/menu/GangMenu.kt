/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.HiddenLore
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.challenge.menu.GangChallengesMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class GangMenu(private val gang: Gang) : Menu() {

    init {
        updateAfterClick = true
        placeholder = true
    }

    override fun getTitle(player: Player): String {
        return "Jerry The Prison Guard"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[10] = AnnouncementButton()
        buttons[13] = ChallengesButton()
        buttons[16] = BoostersButton()

        buttons[28] = SettingsButton()
        buttons[31] = MembersButton()
        buttons[34] = MoveLocationButton()

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    private inner class AnnouncementButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Announcement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            for (line in TextSplitter.split(length = 40, text = gang.announcement, linePrefix = "${ChatColor.GRAY}")) {
                description.add(line)
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }
    }

    private inner class ChallengesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Challenges"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Complete challenges with your gang members to earn your gang trophies.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to view challenges")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                GangChallengesMenu(gang).openMenu(player)
            }
        }
    }

    private inner class BoostersButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Boosters"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Activate boosters to help your gang earn more money, tokens, and trophies."))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to manage boosters")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                BoostersMenu(gang).openMenu(player)
            }
        }
    }

    private inner class MoveLocationButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Move Location"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(text = "Move Jerry's location by using the Mover Tool.", linePrefix = ChatColor.GRAY.toString()))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to move Jerry")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.MONSTER_EGG
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemUtils.setMonsterEggType(super.getButtonItem(player), EntityType.VILLAGER)
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!gang.isLeader(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the leader of the gang to move Jerry!")
                return
            }

            if (player.inventory.firstEmpty() == -1) {
                player.sendMessage("${ChatColor.RED}You need a free inventory space to pickup Jerry!")
                return
            }

            gang.guideNpc.updateVisibility(hidden = true)

            val builder = ItemBuilder.of(Material.MONSTER_EGG)
                .name("${ChatColor.YELLOW}${ChatColor.BOLD}Move Jerry")
                .addToLore(
                    HiddenLore.encodeString("${gang.guideNpc.uuid}"),
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
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Settings & Permissions"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Manage your gang's settings",
                "${ChatColor.GRAY}and permissions.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to manage settings"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.REDSTONE_COMPARATOR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (!gang.isLeader(player.uniqueId)) {
                player.sendMessage("${ChatColor.RED}You must be the leader of the gang to manage its settings.")
                return
            }

            ManageSettingsMenu(gang).openMenu(player)
        }
    }

    private inner class MembersButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Members"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}View and manage your gang's members.",
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

        override fun getButtonItem(player: Player): ItemStack {
            val item = super.getButtonItem(player)
            val meta = item.itemMeta as SkullMeta
            meta.owner = player.name
            item.itemMeta = meta
            return item
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (gang.isLeader(player.uniqueId)) {
                ManageMembersMenu(gang).openMenu(player)
            } else {
                MembersMenu(gang).openMenu(player)
            }
        }
    }

}