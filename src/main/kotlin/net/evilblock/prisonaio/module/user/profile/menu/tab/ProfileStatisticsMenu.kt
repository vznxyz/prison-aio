/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.profile.menu.tab

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.profile.menu.ProfileLayout
import net.evilblock.prisonaio.module.user.profile.menu.ProfileLayoutMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProfileStatisticsMenu(user: User) : ProfileLayoutMenu(layout = ProfileLayout(user = user, activeTab = ProfileLayout.ProfileMenuTab.STATISTICS)) {

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = super.getButtons(player) as MutableMap<Int, Button>

        buttons[20] = FirstSeenButton()
        buttons[21] = BlocksMinedButton()
        buttons[22] = TimePlayedButton()
        buttons[23] = CoinFlipButton()
        buttons[24] = KillsButton()
        buttons[25] = DeathsButton()

        return buttons
    }

    private inner class FirstSeenButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}First Seen"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(
                text = "${layout.user.getUsername()} was first seen on ${TimeUtil.formatIntoDateString(Date(layout.user.firstSeen))}.",
                linePrefix = "${ChatColor.GRAY}"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.BED
        }
    }

    private inner class BlocksMinedButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Blocks Mined"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(
                text = "${layout.user.getUsername()} has progress ${NumberFormat.getInstance().format(layout.user.statistics.getBlocksMined())} blocks.",
                linePrefix = "${ChatColor.GRAY}"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_PICKAXE
        }
    }

    private inner class TimePlayedButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Time Played"
        }

        override fun getDescription(player: Player): List<String> {
            val formattedPlayTime = TimeUtil.formatIntoDetailedString(TimeUnit.MILLISECONDS.toSeconds(layout.user.statistics.getLivePlayTime()).toInt())

            return TextSplitter.split(
                text = "${layout.user.getUsername()} has played on the server for $formattedPlayTime.",
                linePrefix = "${ChatColor.GRAY}"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }
    }

    private inner class CoinFlipButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}COIN${ChatColor.YELLOW}${ChatColor.BOLD}FLIP"
        }

        override fun getDescription(player: Player): List<String> {
            return CoinFlipHandler.renderStatisticsDisplay(layout.user)
        }

        override fun getMaterial(player: Player): Material {
            return Material.DOUBLE_PLANT
        }
    }

    private inner class KillsButton : TexturedHeadButton(Constants.SKULL_GREEN_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Kills"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "${layout.user.getUsername()} has ${NumberUtils.format(layout.user.statistics.getKills())} kills.",
                linePrefix = "${ChatColor.GRAY}"
            )
        }
    }

    private inner class DeathsButton : TexturedHeadButton(Constants.SKULL_RED_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Deaths"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(text = "${layout.user.getUsername()} has ${NumberUtils.format(layout.user.statistics.getKills())} deaths.",
                linePrefix = "${ChatColor.GRAY}"
            )
        }
    }

}