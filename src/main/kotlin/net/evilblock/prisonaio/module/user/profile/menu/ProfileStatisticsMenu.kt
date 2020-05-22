package net.evilblock.prisonaio.module.user.profile.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProfileStatisticsMenu(user: User) : ProfileMenuTemplate(user = user, tab = ProfileMenuTab.STATISTICS) {

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = super.getButtons(player) as MutableMap<Int, Button>

        buttons[20] = FirstSeenButton()
        buttons[21] = BlocksMinedButton()
        buttons[22] = TimePlayedButton()

        return buttons
    }

    private inner class FirstSeenButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}First Seen"
        }

        override fun getDescription(player: Player): List<String> {
            return TextSplitter.split(
                length = 36,
                text = "${user.getUsername()} was first seen on ${TimeUtil.formatIntoDateString(Date(user.firstSeen))}.",
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
                length = 36,
                text = "${user.getUsername()} has mined ${NumberFormat.getInstance().format(user.statistics.getBlocksMined())} blocks.",
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
            return TextSplitter.split(
                length = 36,
                text = "${user.getUsername()} has played on the server for ${TimeUtil.formatIntoDetailedString(TimeUnit.MILLISECONDS.toSeconds(user.statistics.getLivePlayTime()).toInt())}.",
                linePrefix = "${ChatColor.GRAY}"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }
    }



}