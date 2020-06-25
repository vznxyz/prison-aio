/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.crate.Crate
import net.evilblock.prisonaio.module.crate.CrateHandler
import net.evilblock.prisonaio.module.crate.reward.CrateReward
import net.evilblock.prisonaio.module.crate.reward.impl.BasicCrateReward
import net.evilblock.prisonaio.module.crate.reward.impl.ItemCrateReward
import net.evilblock.prisonaio.module.crate.reward.menu.EditCrateRewardMenu
import net.evilblock.prisonaio.module.crate.reward.menu.button.CrateRewardButton
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditCrateMenu(internal val crate: Crate) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Crate - ${crate.name}"
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = EditNameButton()
        buttons[3] = EditHologramTextButton()
        buttons[4] = AddRewardButton()
        buttons[5] = EditRewardsRangeButton()
        buttons[6] = EditReRollButton()

        for (i in 9..17) {
            buttons[i] = Button.placeholder(Material.STAINED_GLASS_PANE, 0, " ")
        }

        return buttons
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        crate.rewards.sortedBy { it.sortOrder }.forEachIndexed { index, crateReward ->
            buttons[index] = RewardButton(crateReward)
        }

        for (i in 0 until 36) {
            if (!buttons.containsKey(i)) {
                buttons[i] = EmptySlotButton()
            }
        }

        return buttons
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun acceptsShiftClickedItem(player: Player, itemStack: ItemStack): Boolean {
        return itemStack.type != Material.AIR && crate.rewards.add(ItemCrateReward(itemStack.clone()))
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            PrisonAIO.instance.server.scheduler.runTaskLater(PrisonAIO.instance, {
                CrateEditorMenu().openMenu(player)
            }, 1L)
        }
    }

    private inner class AddRewardButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Add Reward"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}Adds a new command-based reward",
                "${ChatColor.GRAY}into the crate.",
                "",
                "${ChatColor.GRAY}If you'd like to add a physical",
                "${ChatColor.GRAY}item reward, all you need to do",
                "${ChatColor.GRAY}is drop an item onto an empty slot.",
                "",
                "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add reward"
            )
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val newReward = BasicCrateReward()
            crate.rewards.add(newReward)
            CrateHandler.saveData()

            EditCrateRewardMenu(crate, newReward).openMenu(player)
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf(
                "",
                "${ChatColor.GRAY}The name is how you want the crate",
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
                        return "${ChatColor.GREEN}Please input a new name for the crate. ${ChatColor.GRAY}(Colors supported, limited to 48 characters)"
                    }

                    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                        assert(input.length in 1..48) { "Text is too long! (${input.length} > 48)" }

                        crate.name = ChatColor.translateAlternateColorCodes('&', input)
                        CrateHandler.saveData()

                        context.forWhom.sendRawMessage("${ChatColor.GREEN}Successfully updated crate name.")
                        openMenu(player)

                        return Prompt.END_OF_CONVERSATION
                    }
                })
            }
        }
    }

    private inner class EditHologramTextButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Hologram Text"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")
            description.addAll(TextSplitter.split(length = 40, text = "Edit the hologram text that appears above each ${crate.name} ${ChatColor.GRAY}crate.", linePrefix = "${ChatColor.GRAY}"))
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit hologram text")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditCrateHologramTextMenu(crate).openMenu(player)
            }
        }
    }

    private inner class EditRewardsRangeButton : TexturedHeadButton(NUMBER_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Rewards Range"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}The rewards range controls how many")
            description.add("${ChatColor.GRAY}rewards can be given per crate key.")
            description.add("")
            description.add("${ChatColor.GRAY}Min Rewards: ${ChatColor.GREEN}${crate.rewardsRange.first}")
            description.add("${ChatColor.GRAY}Max Rewards: ${ChatColor.GREEN}${crate.rewardsRange.last}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit rewards range")
            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditCrateRewardsRangeMenu(this@EditCrateMenu).openMenu(player)
            }
        }
    }

    private inner class EditReRollButton : TexturedHeadButton(REFRESH_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Re-Roll"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.add("")
            description.add("${ChatColor.GRAY}If players that have available")
            description.add("${ChatColor.GRAY}re-rolls can re-roll this crate.")
            description.add("")

            if (crate.reroll) {
                description.add("${ChatColor.GRAY}Players with re-rolls currently")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}can ${ChatColor.GRAY}re-roll this crate.")
            } else {
                description.add("${ChatColor.GRAY}Players with re-rolls currently")
                description.add("${ChatColor.RED}${ChatColor.BOLD}can not ${ChatColor.GRAY}re-roll this crate.")
            }

            description.add("")

            if (crate.reroll) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to disable re-roll")
            } else {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enable re-roll")
            }

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                crate.reroll = !crate.reroll
                CrateHandler.saveData()
            }
        }
    }

    private inner class EmptySlotButton : Button() {
        override fun getMaterial(player: Player): Material {
            return Material.AIR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (view.cursor != null && view.cursor.type != Material.AIR) {
                val clonedCursor = view.cursor.clone()

                crate.rewards.add(ItemCrateReward(clonedCursor))
                CrateHandler.saveData()

                view.cursor = null
                player.updateInventory()
            }
        }
    }

    private inner class RewardButton(reward: CrateReward) : CrateRewardButton(reward) {
        override fun getName(player: Player): String {
            return reward.name
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (reward.getIcon().hasItemMeta() && reward.getIcon().itemMeta.hasDisplayName() && reward.getIcon().itemMeta.displayName != reward.name) {
                description.add(reward.getIcon().itemMeta.displayName)
            }

            if (reward.getIcon().hasItemMeta() && reward.getIcon().itemMeta.hasLore()) {
                description.addAll(reward.getIcon().lore!!.toList())
            }

            description.add("")
            description.add("${ChatColor.GRAY}Chance: ${ChatColor.YELLOW}${reward.chance}%")
            description.add("${ChatColor.GRAY}Commands: ${ChatColor.YELLOW}${reward.commands.size}")
            description.add("${ChatColor.GRAY}Sort Order: ${ChatColor.YELLOW}${reward.sortOrder}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit reward")
            description.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete reward")

            return description
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            // handle edit
            if (clickType.isLeftClick) {
                EditCrateRewardMenu(crate, reward).openMenu(player)
            }

            // handle delete
            if (clickType.isRightClick) {
                ConfirmMenu(title = "Are you sure?", callback = { confirmed ->
                    if (confirmed) {
                        crate.rewards.remove(reward)
                        player.sendMessage("${ChatColor.GREEN}Successfully deleted reward from crate.")
                    } else {
                        player.sendMessage("${ChatColor.YELLOW}No changes made to crate.")
                    }

                    openMenu(player)
                }).openMenu(player)
            }
        }
    }

    companion object {
        private const val NUMBER_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFhOTQ2M2ZkM2M0MzNkNWUxZDlmZWM2ZDVkNGIwOWE4M2E5NzBiMGI3NGRkNTQ2Y2U2N2E3MzM0OGNhYWIifX19"
        private const val REFRESH_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg4N2NjMzg4YzhkY2ZjZjFiYThhYTVjM2MxMDJkY2U5Y2Y3YjFiNjNlNzg2YjM0ZDRmMWMzNzk2ZDNlOWQ2MSJ9fX0="
    }

}