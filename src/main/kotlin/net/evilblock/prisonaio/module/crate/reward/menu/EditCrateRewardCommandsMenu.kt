/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditCrateRewardCommandsMenu(private val parent: EditCrateRewardMenu, private val reward: CrateReward) : Menu() {

    override fun getTitle(player: Player): String {
        return "Edit Reward Commands"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = AddCommandButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        reward.commands.forEachIndexed { index, command ->
            buttons[18 + index] = CommandButton(command)
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                parent.openMenu(player)
            }, 1L)
        }
    }

    private inner class AddCommandButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Add Command"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Add a new command by completing",
                "${ChatColor.GRAY}the setup procedure.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add command"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ConversationUtil.startConversation(player, object : StringPrompt() {
                    override fun getPromptText(context: ConversationContext): String {
                        return "${ChatColor.GREEN}Please input the command to add."
                    }

                    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                        if (input.equals("cancel", ignoreCase = true)) {
                            context.forWhom.sendRawMessage("${ChatColor.YELLOW}Cancelled setup procedure.")
                            return Prompt.END_OF_CONVERSATION
                        }

                        reward.commands.add(input)

                        CrateHandler.saveData()

                        context.forWhom.sendRawMessage("${ChatColor.GREEN}Successfully added command to reward.")

                        openMenu(context.forWhom as Player)

                        return Prompt.END_OF_CONVERSATION
                    }
                })
            }
        }
    }

    private inner class CommandButton(private val command: String) : Button() {
        private val lines = TextSplitter.split(48, command, "${ChatColor.WHITE}", " ")

        override fun getName(player: Player): String {
            return lines.first()
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.addAll(lines.drop(1))
            description.add("")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete command")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.PAPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isRightClick) {
                ConfirmMenu(title = "Are you sure?", callback = { confirmed ->
                    if (confirmed) {
                        reward.commands.remove(command)
                        player.sendMessage("${ChatColor.GREEN}Successfully deleted command from reward.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to reward commands.")
                    }

                    openMenu(player)
                }).openMenu(player)
            }
        }
    }

}