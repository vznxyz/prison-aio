/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class EditCrateRewardIconMenu(private val crate: Crate, private val reward: CrateReward) : Menu() {

    override fun getTitle(player: Player): String {
        return "Select an Icon"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[4] = GuideButton()

        return buttons
    }

    open fun onSelectItemStack(player: Player, itemStack: ItemStack) {
        reward.icon = itemStack.clone()
        CrateHandler.saveData()

        Tasks.delayed(1L) {
            EditCrateRewardMenu(crate, reward).openMenu(player)
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditCrateRewardMenu(crate, reward).openMenu(player)
            }
        }
    }

    private inner class GuideButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Select an Icon"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Click on an item-stack in your",
                "${ChatColor.GRAY}inventory to select as your new icon."
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.EYE_OF_ENDER
        }
    }

}