/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.prisonaio.module.rank.Rank
import net.evilblock.prisonaio.module.rank.RankHandler
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class UserRankupsMenu(private val user: User) : Menu() {

    override fun getTitle(player: Player): String {
        return "Rankups"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (rank in RankHandler.getSortedRanks()) {
            buttons[buttons.size] = RankupButton(user, rank)
        }

        return buttons
    }

    private inner class RankupButton(private val user: User, private val rank: Rank) : Button() {
        override fun getName(player: Player): String {
            if (user.getRank().sortOrder >= rank.sortOrder) {
                return "${ChatColor.GREEN}${ChatColor.BOLD}Rank ${rank.displayName}"
            }

            val optionalNextRank = RankHandler.getNextRank(user.getRank())
            if (optionalNextRank.isPresent && optionalNextRank.get() == rank) {
                return "${ChatColor.YELLOW}${ChatColor.BOLD}Rank ${rank.displayName}"
            }

            return "${ChatColor.RED}${ChatColor.BOLD}Rank ${rank.displayName}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}This rankup costs ${ChatColor.GREEN}$${ChatColor.YELLOW}${NumberUtils.format(rank.getPrice(user.getPrestige()))}${ChatColor.GRAY}."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTED_BOOK
        }
    }

}