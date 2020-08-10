/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.challenge.GangChallenge
import net.evilblock.prisonaio.module.gang.challenge.GangChallengeHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

class GangChallengesMenu(private val gang: Gang) : Menu() {

    companion object {
        private val CHALLENGE_SLOTS = arrayListOf(
            10, 12, 14, 16,
            28, 30, 32, 34
        )
    }

    init {
        updateAfterClick = true
        placeholder = true
    }

    override fun getTitle(player: Player): String {
        return "Gang Challenges"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        var i = 0
        for (challenge in GangChallengeHandler.getChallenges()) {
            buttons[CHALLENGE_SLOTS[i++]] = ChallengeButton(challenge)

            if (i >= 5) {
                break
            }
        }

        buttons[49] = ExitButton()

        return buttons
    }

    private inner class ChallengeButton(private val challenge: GangChallenge) : Button() {
        override fun getName(player: Player): String {
            return if (gang.challengesData.hasCompleted(challenge)) {
                "${ChatColor.GREEN}${ChatColor.BOLD}${challenge.getRenderedName()}"
            } else {
                "${ChatColor.RED}${ChatColor.BOLD}${challenge.getRenderedName()}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")

            for (line in challenge.renderGoal()) {
                description.add("${ChatColor.GRAY}$line")
            }

            description.add("")

            if (gang.challengesData.hasCompleted(challenge)) {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}COMPLETED")
            } else {
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}IN PROGRESS")
                description.add(challenge.renderProgress(gang))
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            return itemMeta
        }
    }

    private inner class ExitButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Exit"
        }

        override fun getDescription(player: Player): List<String> {
            return emptyList()
        }

        override fun getMaterial(player: Player): Material {
            return Material.BARRIER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            player.closeInventory()
        }
    }

}