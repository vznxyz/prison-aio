/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.booster.GangBooster
import net.evilblock.prisonaio.module.gang.permission.GangPermission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class GangBoostersMenu(private val gang: Gang) : Menu() {

    companion object {
        private val ORANGE_GLASS = arrayListOf(
            0, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 44
        )

        private val BOOSTER_SLOTS = mapOf(
            GangBooster.BoosterType.INCREASED_TROPHIES to 20,
            GangBooster.BoosterType.INCREASED_MINE_CRATES to 22,
            GangBooster.BoosterType.SALES_MULTIPLIER to 24
        )
    }

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.GOLD}Gang Boosters"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[4] = TrophyCountButton()

        for (boosterType in GangBooster.BoosterType.values()) {
            val slot = BOOSTER_SLOTS.getValue(boosterType)
            buttons[slot] = BoosterButton(boosterType)

            if (gang.hasBooster(boosterType)) {
                buttons[slot + 9] = BoosterActiveButton(gang.getBooster(boosterType)!!)
            } else {
                buttons[slot + 9] = PurchaseBoosterButton(boosterType)
            }
        }

        for (i in ORANGE_GLASS) {
            buttons[i] = GlassButton(1)
        }

        for (i in 0..44) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(15)
            }
        }

        return buttons
    }

    private inner class TrophyCountButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.GOLD}${ChatColor.BOLD}Trophies ${ChatColor.GRAY}${Constants.DOUBLE_ARROW_LEFT}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(
                text = "Your gang has ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.format(gang.getTrophies())} ${ChatColor.GOLD}trophies ${ChatColor.GRAY}to spend.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.GOLD_INGOT
        }
    }

    private inner class BoosterButton(private val boosterType: GangBooster.BoosterType) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}${boosterType.rendered}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                text = boosterType.description,
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Price: ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.format(boosterType.price)} ${ChatColor.GOLD}Trophies")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return boosterType.icon
        }
    }

    private inner class BoosterActiveButton(private val booster: GangBooster) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Booster Active"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(
                text = "This booster expires in ${ChatColor.YELLOW}${TimeUtil.formatIntoDetailedString((booster.getRemainingTime() / 1000.0).toInt()).split(" ").joinToString(separator = " ${ChatColor.YELLOW}")}${ChatColor.GRAY}.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 5
        }
    }

    private inner class PurchaseBoosterButton(private val boosterType: GangBooster.BoosterType) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Purchase Booster"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(
                text = "Click to purchase this booster for ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.format(boosterType.price)} ${ChatColor.GOLD}Trophies${ChatColor.GRAY}.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return 4
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (!gang.testPermission(player, GangPermission.SPEND_TROPHIES, sendMessage = true)) {
                    return
                }

                if (gang.hasTrophies(boosterType.price)) {
                    ConfirmMenu { confirmed ->
                        if (confirmed) {
                            if (!gang.testPermission(player, GangPermission.SPEND_TROPHIES, sendMessage = true)) {
                                return@ConfirmMenu
                            }

                            val booster = GangBooster(boosterType = boosterType, purchasedBy = player.uniqueId)

                            gang.takeTrophies(boosterType.price)
                            gang.grantBooster(booster)
                        }

                        this@GangBoostersMenu.openMenu(player)
                    }.openMenu(player)
                } else {
                    player.sendMessage("${ChatColor.RED}Your gang doesn't have enough trophies to purchase the ${boosterType.rendered} booster.")
                }
            }
        }
    }

}