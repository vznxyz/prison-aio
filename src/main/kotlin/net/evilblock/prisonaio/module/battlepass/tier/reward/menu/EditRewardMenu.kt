package net.evilblock.prisonaio.module.battlepass.tier.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.battlepass.tier.menu.TierEditorMenu
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditRewardMenu(private val reward: Reward) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Reward - Tier ${reward.tier.number} (${if (reward.isFreeReward()) "Free" else "Premium"})"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[1] = EditTextButton()
        buttons[3] = EditCommandsButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                TierEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditTextButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Rewards Text"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The text that appears in the `rewards` section of the button representing this challenge.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit rewards text")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditRewardTextMenu(reward).openMenu(player)
            }
        }
    }

    private inner class EditCommandsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Rewards Commands"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The commands that are executed when a player claims this reward.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit commands")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.COMMAND
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditRewardCommandsMenu(reward).openMenu(player)
            }
        }
    }

}