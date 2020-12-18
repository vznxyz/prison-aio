/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class UserRankupsMenu(private val user: User) : Menu() {

    override fun getTitle(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Rankups"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { MainMenu(user).openMenu(player) }
            buttons[4] = InfoButton()

            for ((index, rank) in RankHandler.getSortedRanks().withIndex()) {
                buttons[9 + index] = RankupButton(user, rank)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                MainMenu(user).openMenu(player)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Rankups"
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }
    }

    private inner class RankupButton(private val user: User, private val rank: Rank) : Button() {
        override fun getName(player: Player): String {
            val nextRank = RankHandler.getNextRank(user.getRank())

            val color = if (user.getRank().sortOrder >= rank.sortOrder) {
                ChatColor.GREEN
            } else if (nextRank != null && nextRank == rank) {
                ChatColor.YELLOW
            } else {
                ChatColor.RED
            }

            return "$color${ChatColor.BOLD}Rank ${rank.displayName} ${Formats.formatMoney(rank.getPrice(user.getPrestige()))}"
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTED_BOOK
        }
    }

}