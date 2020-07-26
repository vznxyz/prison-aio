/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassMenu
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class BrowseChallengesMenu(private val user: User, private val daily: Boolean) : Menu() {

    private var page: Int = 1

    override fun getTitle(player: Player): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}${if (daily) "Daily" else "Premium"} Challenges"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()
        val offset = (page - 1) * SLOTS.size

        val challengeSet = if (daily) {
            DailyChallengeHandler.getSession().getChallenges()
        } else {
            ChallengeHandler.getChallenges()
        }

        for ((index, challenge) in challengeSet.sortedBy { it.rewardXp }.drop(offset).withIndex()) {
            if (index >= SLOTS.size) {
                break
            }

            buttons[SLOTS[index]] = ChallengeButton(challenge)
        }

        buttons[45] = PreviousPageButton()
        buttons[53] = NextPageButton()

        buttons[49] = BackButton { BattlePassMenu(user).openMenu(player) }

        for (i in 0 until 54) {
            if (!buttons.containsKey(i)) {
                buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 15, " ")
            }
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                BattlePassMenu(user).openMenu(player)
            }
        }
    }

    private inner class ChallengeButton(private val challenge: Challenge) : Button() {
        override fun getName(player: Player): String {
            return "${challenge.name} ${ChatColor.GRAY}(${challenge.rewardXp} ${ChatColor.GOLD}${Constants.EXP_SYMBOL}${ChatColor.GRAY})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.addAll(TextSplitter.split(length = 40, text = challenge.getText(), linePrefix = "${ChatColor.GRAY}"))

            if (challenge.isProgressive()) {
                description.add("")
                description.add(challenge.getProgressText(player, user))
            }

            if ((challenge.daily && DailyChallengeHandler.getSession().getProgress(player.uniqueId).hasCompletedChallenge(challenge)) || user.battlePassProgress.hasCompletedChallenge(challenge)) {
                description.add("")
                description.add("${ChatColor.GREEN}You've completed this challenge!")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.STAINED_GLASS_PANE
        }

        override fun getDamageValue(player: Player): Byte {
            return if ((challenge.daily && DailyChallengeHandler.getSession().getProgress(player.uniqueId).hasCompletedChallenge(challenge)) || user.battlePassProgress.hasCompletedChallenge(challenge)) {
                13.toByte()
            } else {
                1.toByte()
            }
        }
    }

    private inner class PreviousPageButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==") {
        override fun getName(player: Player): String {
            return if (page > 1) {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Previous page"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}No previous page"
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && page > 1) {
                page -= 1
                openMenu(player)
            }
        }
    }

    private inner class NextPageButton : TexturedHeadButton(texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19") {
        override fun getName(player: Player): String {
            return if (page < 6) {
                "${ChatColor.YELLOW}${ChatColor.BOLD}Next page"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}No next page"
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick && page < 6) {
                page += 1
                openMenu(player)
            }
        }
    }

    companion object {
        private val SLOTS = arrayListOf(
            10, 12, 14, 16,
            28, 30, 32, 34
        )
    }

}