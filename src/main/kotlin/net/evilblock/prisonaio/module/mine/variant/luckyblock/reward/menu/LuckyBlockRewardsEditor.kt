/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlock
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.menu.EditLuckyBlockMenu
import net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.LuckyBlockReward
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class LuckyBlockRewardsEditor(private val luckyBlock: LuckyBlock) : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "Edit Rewards"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {  buttons ->
            for (reward in luckyBlock.rewards) {
                buttons[buttons.size] = RewardButton(reward)
            }
        }
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = AddRewardButton()

        for (i in 9..17) {
            buttons[i] = GlassButton(0)
        }

        return buttons
    }

    override fun getPageButtonSlots(): Pair<Int, Int> {
        return Pair(0, 8)
    }

    override fun getButtonsStartOffset(): Int {
        return 9
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 36
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 45
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                EditLuckyBlockMenu(luckyBlock).openMenu(player)
            }
        }
    }

    private inner class AddRewardButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Add Reward"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Add a new reward by completing the setup procedure."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add reward")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                val reward = LuckyBlockReward()
                luckyBlock.rewards.add(reward)

                Tasks.async {
                    LuckyBlockHandler.saveData()
                }

                EditRewardMenu(reward).openMenu(player)
            }
        }
    }

    private inner class RewardButton(private val reward: LuckyBlockReward) : Button() {
        override fun getName(player: Player): String {
            return reward.name
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")

                desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Commands")
                for (command in reward.commands) {
                    desc.add("${ChatColor.WHITE} $command")
                }

                desc.add("")
                desc.add("${ChatColor.GRAY}Give Item: ${if (reward.giveItem) "${ChatColor.GREEN}${ChatColor.BOLD}yes" else "${ChatColor.RED}${ChatColor.BOLD}no" }")
                desc.add("${ChatColor.GRAY}Chance: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(reward.chance)}")
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(reward.itemStack)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }
    }

}