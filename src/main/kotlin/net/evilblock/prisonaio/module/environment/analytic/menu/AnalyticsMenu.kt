/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.environment.analytic.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.environment.analytic.Analytic
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class AnalyticsMenu : Menu() {

    init {
        autoUpdate = true
    }

    override fun getTitle(player: Player): String {
        return "Analytics"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (analytic in Analytic.values()) {
            buttons[buttons.size] = AnalyticButton(analytic)
        }

        return buttons
    }

    private inner class AnalyticButton(private val analytic: Analytic) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${analytic.displayName}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf("${ChatColor.GRAY}${analytic.getFormattedValue()}")
        }

        override fun getMaterial(player: Player): Material {
            return analytic.icon.type
        }

        override fun getDamageValue(player: Player): Byte {
            return analytic.icon.durability.toByte()
        }
    }

}