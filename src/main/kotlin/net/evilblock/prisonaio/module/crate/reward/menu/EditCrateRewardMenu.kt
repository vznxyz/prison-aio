package net.evilblock.prisonaio.module.crate.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.menu.EditCrateMenu
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import net.evilblock.prisonaio.module.crate.reward.impl.BasicCrateReward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import kotlin.math.max

class EditCrateRewardMenu(private val crate: Crate, val reward: CrateReward) : Menu() {

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

        val collectedButtons = arrayListOf(
            EditNameButton(),
            EditChanceButton(),
            EditCommandsButton(),
            EditSortOrderButton()
        )

        if (reward is BasicCrateReward) {
            collectedButtons.add(0, EditIconButton())
        }

        val startAt = if (collectedButtons.size >= 5) 0 else 1
        collectedButtons.forEachIndexed { index, button ->
            buttons[startAt + (index * 2)] = button
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                EditCrateMenu(crate).openMenu(player)
            }, 1L)
        }
    }

    private inner class EditIconButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Icon"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Select an item to represent",
                "${ChatColor.GRAY}this reward.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit icon"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.EYE_OF_ENDER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditCrateRewardIconMenu(crate, reward).openMenu(player)
            }
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
                player.closeInventory()

                ConversationUtil.startConversation(player, object : StringPrompt() {
                    override fun getPromptText(context: ConversationContext): String {
                        return "${ChatColor.GREEN}Please input a new name for the reward. ${ChatColor.GRAY}(Colors supported, limited to 48 characters)"
                    }

                    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                        assert(input.length in 1..48) { "Text is too long! (${input.length} > 48)" }

                        reward.name = ChatColor.translateAlternateColorCodes('&', input)
                        CrateHandler.saveData()

                        context.forWhom.sendRawMessage("${ChatColor.GREEN}Successfully updated reward name.")

                        openMenu(player)

                        return Prompt.END_OF_CONVERSATION
                    }
                })
            }
        }
    }

    private inner class EditChanceButton : TexturedHeadButton(PERCENT_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Chance ${ChatColor.GRAY}(${reward.chance}%)"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The chance of winning this reward",
                "${ChatColor.GRAY}from a roll.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit chance"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()

                ConversationUtil.startConversation(player, object : StringPrompt() {
                    override fun getPromptText(context: ConversationContext): String {
                        return "${ChatColor.GREEN}Please input a new chance (percentage) for the reward. ${ChatColor.GRAY}(0-100)"
                    }

                    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                        try {
                            reward.chance = input.replace("%", "").replace(",", "").toDouble()
                        } catch (e: NumberFormatException) {
                            context.forWhom.sendRawMessage("${ChatColor.RED}Could not parse input to percentage.")
                            return Prompt.END_OF_CONVERSATION
                        }

                        CrateHandler.saveData()

                        context.forWhom.sendRawMessage("${ChatColor.GREEN}Successfully updated reward chance.")

                        openMenu(player)

                        return Prompt.END_OF_CONVERSATION
                    }
                })
            }
        }
    }

    private inner class EditCommandsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Commands"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The commands that are executed",
                "${ChatColor.GRAY}by console when a player wins",
                "${ChatColor.GRAY}this reward.",
                "",
                "${ChatColor.GRAY}Available variables:",
                "${ChatColor.GRAY} {playerName} - The name of the player",
                "${ChatColor.GRAY} {playerDisplayName} - The display name of the player",
                "${ChatColor.GRAY} {playerUuid} - The UUID of the player",
                "${ChatColor.GRAY} {rewardName} - The name of this reward",
                "${ChatColor.GRAY} {chance} - The chance of winning this reward",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit commands"
            )
        }

        override fun getMaterial(player: Player): Material {
            return Material.COMMAND_REPEATING
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditCrateRewardCommandsMenu(this@EditCrateRewardMenu, reward).openMenu(player)
            }
        }
    }

    private inner class EditSortOrderButton : TexturedHeadButton(STONE_ARROW_UP_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Sort Order ${ChatColor.GRAY}(${reward.sortOrder})"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}This setting controls the order")
            description.add("${ChatColor.GRAY}that rewards are displayed in.")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase sort order by +1")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease sort order by -1")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase sort order by +10")
            description.add("${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease sort order by -10")
            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            try {
                val mod = if (clickType.isShiftClick) 10 else 1
                when {
                    clickType.isLeftClick -> {
                        reward.sortOrder = max(0, reward.sortOrder + mod)
                    }
                    clickType.isRightClick -> {
                        reward.sortOrder = max(0, reward.sortOrder - mod)
                    }
                }
            } catch (e: IllegalStateException) {
                player.sendMessage("${ChatColor.RED}${e.message}!")
            }
        }
    }

    companion object {
        private const val PERCENT_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg1YmU3NmRlMjhkZGNiMzlkMjgzZTNkNzFmNmVkNjNkZTg1NGY4Mzk2MjNlYzE4YTUzODBjODRmMWMyNWY5In19fQ=="
        private const val STONE_ARROW_UP_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThmZTI1MWE0MGU0MTY3ZDM1ZDA4MWMyNzg2OWFjMTUxYWY5NmI2YmQxNmRkMjgzNGQ1ZGM3MjM1ZjQ3NzkxZCJ9fX0="
    }

}