/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.deliveryman.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.reward.deliveryman.DeliveryManHandler
import net.evilblock.prisonaio.module.reward.deliveryman.reward.DeliveryManReward
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirement
import net.evilblock.prisonaio.module.reward.deliveryman.reward.requirement.DeliveryManRewardRequirementType
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditDeliveryManRewardRequirementsMenu(private val reward: DeliveryManReward) : Menu() {

    override fun getTitle(player: Player): String {
        return "Edit Requirements - ${reward.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddRequirementButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        reward.requirements.forEachIndexed { index, requirement ->
            buttons[18 + index] = RequirementButton(requirement)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditDeliveryManRewardMenu(reward).openMenu(player)
            }
        }
    }

    private inner class AddRequirementButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Add Requirement"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Add a requirement to claim this",
                "${ChatColor.GRAY}reward by completing the setup",
                "${ChatColor.GRAY}procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add requirement"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                AddRequirementMenu().openMenu(player)
            }
        }
    }

    private inner class RequirementButton(private val requirement: DeliveryManRewardRequirement) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${requirement.getType().getName()} Requirement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = requirement.getText(), linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to remove requirement")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return requirement.getType().getIcon().type
        }

        override fun getDamageValue(player: Player): Byte {
            return requirement.getType().getIcon().durability.toByte()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                ConfirmMenu("Are you sure?") { confirmed ->
                    if (confirmed) {
                        reward.requirements.remove(requirement)
                        DeliveryManHandler.saveData()

                        player.sendMessage("${ChatColor.GREEN}Successfully removed requirement.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made.")
                    }

                    openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class AddRequirementMenu : Menu() {
        override fun getTitle(player: Player): String {
            return "Add Requirement"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            val buttons = hashMapOf<Int, Button>()

            for (reqType in DeliveryManHandler.REQUIREMENT_REGISTRY) {
                buttons[buttons.size] = RequirementTypeButton(reqType)
            }

            return buttons
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    EditDeliveryManRewardRequirementsMenu(reward).openMenu(player)
                }
            }
        }

        private inner class RequirementTypeButton(private val type: DeliveryManRewardRequirementType<*>) : Button() {
            init {
                updateAfterClick = true
            }

            override fun getName(player: Player): String {
                return "${ChatColor.YELLOW}${ChatColor.BOLD}${type.getName()} Requirement"
            }

            override fun getDescription(player: Player): List<String> {
                val description = arrayListOf<String>()

                description.add("")
                description.addAll(TextSplitter.split(length = 40, text = type.getDescription(), linePrefix = "${ChatColor.GRAY}"))
                description.add("")

                if (!type.isCompatibleWithReward(reward)) {
                    description.add("${ChatColor.RED}This requirement is already applied")
                    description.add("${ChatColor.RED}or does not support that type of reward.")
                } else {
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add requirement")
                }

                return description
            }

            override fun getMaterial(player: Player): Material {
                return type.getIcon().type
            }

            override fun getDamageValue(player: Player): Byte {
                return type.getIcon().durability.toByte()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick && type.isCompatibleWithReward(reward)) {
                    type.startSetupProcedure(player, reward)
                }
            }
        }
    }

}