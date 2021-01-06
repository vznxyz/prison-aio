/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.rules.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.gang.rules.GangRule
import net.evilblock.prisonaio.module.gang.rules.GangRulesHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.MainMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class RulesMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Rules"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[0] = BackButton {
                MainMenu(UserHandler.getUser(it)).openMenu(player)
            }

            buttons[4] = InfoButton()

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for ((index, rule) in GangRulesHandler.rules.withIndex()) {
                buttons[buttons.size] = RuleButton(index + 1, rule)
            }
        }
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 45
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                MainMenu(UserHandler.getUser(player)).openMenu(player)
            }
        }
    }

    private inner class InfoButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}Gang Rules"
        }

        override fun getMaterial(player: Player): Material {
            return Material.BOOK
        }
    }

    private inner class RuleButton(private val number: Int, private val rule: GangRule) : Button() {
        override fun getName(player: Player): String {
            return rule.title.replace("{num}", number.toString())
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                for (line in rule.description) {
                    desc.add(line.replace("{num}", number.toString()))
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }
    }

}