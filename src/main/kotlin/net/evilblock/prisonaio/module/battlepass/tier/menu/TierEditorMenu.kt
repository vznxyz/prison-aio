/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.tier.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.battlepass.menu.BattlePassEditorMenu
import net.evilblock.prisonaio.module.battlepass.tier.Tier
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class TierEditorMenu : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "BattlePass Tier Editor"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (tier in TierHandler.getTiers()) {
            buttons[buttons.size] = TierButton(tier)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                BattlePassEditorMenu().openMenu(player)
            }
        }
    }

    private inner class TierButton(private val tier: Tier) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}Tier ${tier.number}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}Required Experience: ${ChatColor.GREEN}${tier.requiredExperience}")
            description.add("")

            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Free Reward")
            renderRewardDetails(description, tier.freeReward)

            description.add("")

            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Premium Reward")
            renderRewardDetails(description, tier.premiumReward)

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit tier")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditTierMenu(tier).openMenu(player)
            }
        }

        private fun renderRewardDetails(description: MutableList<String>, reward: Reward?) {
            if (reward == null) {
                description.add(" ${ChatColor.GRAY}None applied")
            } else {
                for (text in reward.textLines) {
                    description.add(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} $text")
                }
            }
        }
    }

}