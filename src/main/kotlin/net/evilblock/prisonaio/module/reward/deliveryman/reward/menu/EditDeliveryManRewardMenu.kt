/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.NumberButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.menu.DeliveryManEditorMenu
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditDeliveryManRewardMenu(private val reward: DeliveryManReward) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Reward - ${reward.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (i in 0..8) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 7, " ")
        }

        buttons[0] = EditNameButton()
        buttons[1] = EditOrderButton()
        buttons[2] = EditRewardsTextButton()
        buttons[3] = EditCommandsButton()
        buttons[4] = EditCooldownButton()
        buttons[5] = EditRequirementsButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                DeliveryManEditorMenu().openMenu(player)
            }, 1L)
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The name is how you want the reward",
                "${ChatColor.GRAY}to appear in chat and menu text.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit name"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .promptText(EzPrompt.NAME_PROMPT)
                    .charLimit(48)
                    .acceptInput { player, input ->
                        reward.name = ChatColor.translateAlternateColorCodes('&', input)

                        DeliveryManHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully updated name of reward.")

                        openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class EditOrderButton : NumberButton(number = reward.order) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Order ${ChatColor.GRAY}(${reward.order})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.addAll(TextSplitter.split(length = 36, text = "The order value controls how rewards are sorted (ascending).", linePrefix = "${ChatColor.GRAY}"))
            description.addAll(super.getDescription(player))
            return description
        }

        override fun onChange(number: Int) {
            reward.order = number
            DeliveryManHandler.saveData()
        }
    }

    private inner class EditRewardsTextButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Rewards Text"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "The text that appears in the `rewards` section of the button representing this reward in the Delivery Man menu.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit rewards text")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditDeliveryManRewardTextMenu(reward).openMenu(player)
            }
        }
    }

    private inner class EditCommandsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Commands"
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
                EditDeliveryManRewardCommandsMenu(reward).openMenu(player)
            }
        }
    }

    private inner class EditCooldownButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Cooldown"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The cooldown the player must",
                "${ChatColor.GRAY}wait before being able to",
                "${ChatColor.GRAY}claim this reward again.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit cooldown"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.WATCH
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            EditDeliveryManRewardCooldownMenu(reward).openMenu(player)
        }
    }

    private inner class EditRequirementsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Requirements"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The requirements that the player",
                "${ChatColor.GRAY}must meet before being able to",
                "${ChatColor.GRAY}claim this reward.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit requirements"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.REDSTONE_COMPARATOR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditDeliveryManRewardRequirementsMenu(reward).openMenu(player)
            }
        }
    }

}