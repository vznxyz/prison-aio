/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.leaderboard.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.leaderboard.Leaderboard
import net.evilblock.prisonaio.module.leaderboard.LeaderboardsModule
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

class LeaderboardsMenu(private var redirectOnClose: Boolean = false) : Menu() {

    companion object {
        private val BUTTON_SLOTS = arrayListOf<Int>().also {
            it.addAll(19..26)
            it.addAll(28..35)
        }
    }

    init {
        updateAfterClick = true
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}Leaderboards"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0 until 9) {
                buttons[i] = GlassButton(0)
            }

            buttons[0] = BackButton { MainMenu(UserHandler.getUser(player)).openMenu(player) }
            buttons[4] = InfoButton()

            for ((index, leaderboard) in LeaderboardsModule.getLeaderboards().withIndex()) {
                buttons[BUTTON_SLOTS[index]] = LeaderboardButton(leaderboard)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return if (LeaderboardsModule.getLeaderboards().size > 7) {
            45
        } else {
            36
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (redirectOnClose && manualClose) {
            Tasks.delayed(1L) {
                MainMenu(UserHandler.getUser(player)).openMenu(player)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Leaderboards"
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }
    }

    private inner class LeaderboardButton(private val leaderboard: Leaderboard) : Button() {
        override fun getName(player: Player): String {
            return leaderboard.name
        }

        override fun getDescription(player: Player): List<String> {
            return leaderboard.getDisplayLines(includeTitle = false, fullView = false)
        }

        override fun getMaterial(player: Player): Material {
            return Material.SKULL_ITEM
        }

        override fun getDamageValue(player: Player): Byte {
            return 3
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            return (itemMeta as SkullMeta).also {
                if (leaderboard.entries.isNotEmpty()) {
                    it.owner = leaderboard.entries.first().skinSource
                }
            }
        }
    }

}