/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.RangeEditorMenu
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

class EditCrateRewardsRangeMenu(private val parent: EditCrateMenu) : RangeEditorMenu(parent.crate.rewardsRange.first, parent.crate.rewardsRange.last) {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Rewards Range - ${parent.crate.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        buttons[2] = EditMinButton()
        buttons[6] = EditMaxButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                parent.openMenu(player)
            }, 1L)
        }
    }

    override fun onChange(min: Int, max: Int) {
        parent.crate.updateMinRewards(min)
        parent.crate.updateMaxRewards(max)
    }

    private inner class EditMinButton : RangeEditorMenu.EditMinButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Min Rewards ${ChatColor.GRAY}(${parent.crate.rewardsRange.first})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}This setting controls the")
            description.add("${ChatColor.GRAY}minimum amount of rewards")
            description.add("${ChatColor.GRAY}a player receives per roll.")
            description.addAll(super.getDescription(player))
            return description
        }
    }

    private inner class EditMaxButton : RangeEditorMenu.EditMaxButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Max Rewards ${ChatColor.GRAY}(${parent.crate.rewardsRange.last})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}This setting controls the")
            description.add("${ChatColor.GRAY}maximum amount of rewards")
            description.add("${ChatColor.GRAY}a player receives per roll.")
            description.addAll(super.getDescription(player))
            return description
        }
    }

}