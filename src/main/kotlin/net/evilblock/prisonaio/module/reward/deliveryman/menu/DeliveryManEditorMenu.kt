/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.TextUtil
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.reward.deliveryman.reward.menu.EditDeliveryManRewardMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class DeliveryManEditorMenu : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Delivery Man Editor"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddRewardButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        DeliveryManHandler.getRewards()
            .sortedBy { it.order }
            .forEachIndexed { index, reward ->
                buttons[18 + index] = RewardButton(reward)
            }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class AddRewardButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Create Reward Button"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Create a new reward by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add reward"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (DeliveryManHandler.getRewards().size >= 20) {
                    player.sendMessage("${ChatColor.RED}Can't add a new reward because The Delivery Man can only have 20 rewards at one time.")
                    return
                }

                EzPrompt.Builder()
                    .promptText(EzPrompt.IDENTIFIER_PROMPT)
                    .charLimit(16)
                    .regex(EzPrompt.IDENTIFIER_REGEX)
                    .acceptInput { input ->
                        if (DeliveryManHandler.getRewardById(input) != null) {
                            player.sendMessage("${ChatColor.RED}A reward's ID must be unique, and a reward with the ID `$input` already exists.")
                            return@acceptInput
                        }

                        val reward = DeliveryManReward(
                            id = input,
                            name = input,
                            order = (DeliveryManHandler.getRewards().maxBy { it.order }?.order ?: 0) + 1
                        )

                        DeliveryManHandler.trackReward(reward)
                        DeliveryManHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully created a new reward.")

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class RewardButton(private val reward: DeliveryManReward) : Button() {
        override fun getName(player: Player): String {
            return reward.name
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("${ChatColor.GRAY}(ID: ${reward.id})")
            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Rewards Text")

            if (reward.rewardsText.isEmpty()) {
                description.add("${ChatColor.GRAY}Nothing")
            } else {
                for (line in reward.rewardsText) {
                    description.add(" ${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${ChatColor.RESET}$line")
                }
            }

            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Commands")

            if (reward.commands.isEmpty()) {
                description.add("${ChatColor.GRAY}None")
            } else {
                for (cmd in reward.commands) {
                    description.add(" ${ChatColor.WHITE}${TextUtil.trimIfLonger(cmd, 36)}")
                }
            }

            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}Requirements")

            if (reward.requirements.isEmpty()) {
                description.add("${ChatColor.GRAY}None")
            } else {
                for (requirement in reward.requirements) {
                    description.add("${ChatColor.GRAY}${requirement.getText()}")
                }
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit reward")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete reward")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.STORAGE_MINECART
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditDeliveryManRewardMenu(reward).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        DeliveryManHandler.forgetReward(reward)
                        DeliveryManHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully deleted reward.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to reward.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

}