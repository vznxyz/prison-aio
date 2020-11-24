/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.LuckyBlockReward
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditRewardMenu(private val reward: LuckyBlockReward) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[0] = EditNameButton()
            buttons[2] = EditItemButton()
            buttons[4] = ToggleGiveItemButton()
            buttons[6] = EditChanceButton()
            buttons[8] = EditCommandsButton()

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    private inner class EditNameButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Name"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The name is how you want this reward to be displayed in menu and chat text."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit name")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new name for the reward. ${ChatColor.GRAY}(Colors supported)")
                    .acceptInput { input ->
                        reward.name = ChatColor.translateAlternateColorCodes('&', input)

                        Tasks.async {
                            LuckyBlockHandler.saveData()
                        }

                        this@EditRewardMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditItemButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Item"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The item that can act as the literal reward or as just an icon."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit item")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ITEM_FRAME
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu { selectedItem ->
                    reward.itemStack = selectedItem.clone()

                    Tasks.async {
                        LuckyBlockHandler.saveData()
                    }

                    this@EditRewardMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

    private inner class ToggleGiveItemButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Toggle Give Item"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "If the item should be given to a player when they win this reward."))
                desc.add("")

                if (reward.giveItem) {
                    desc.add("${ChatColor.GREEN}${ChatColor.BOLD}Give Item is currently enabled")
                } else {
                    desc.add("${ChatColor.RED}${ChatColor.BOLD}Give Item is currently disabled")
                }

                desc.add("")

                if (reward.giveItem) {
                    desc.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to disable give item")
                } else {
                    desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enable give item")
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.LEVER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                reward.giveItem = !reward.giveItem

                Tasks.async {
                    LuckyBlockHandler.saveData()
                }
            }
        }
    }

    private inner class EditChanceButton : TexturedHeadButton(PERCENT_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Chance ${ChatColor.GRAY}(${NumberUtils.format(reward.chance)})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The chance of a player winning this reward."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit chance")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()

                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new chance (percentage) for the reward. ${ChatColor.GRAY}(0.0-100.0)")
                    .withRegex(NumberPrompt.NUMBER_REGEX)
                    .acceptInput { number ->
                        try {
                            reward.chance = number.replace("%", "").replace(",", "").toDouble()
                        } catch (e: NumberFormatException) {
                            player.sendMessage("${ChatColor.RED}Could not parse input to percentage.")
                            return@acceptInput
                        }

                        Tasks.async {
                            LuckyBlockHandler.saveData()
                        }

                        player.sendMessage("${ChatColor.GREEN}Successfully updated reward chance.")

                        openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditCommandsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Commands"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The commands that are executed by console when a player wins this reward."))
                desc.add("")
                desc.add("${ChatColor.GRAY}Available variables:")
                desc.add("${ChatColor.GRAY} {playerName} - The name of the player")
                desc.add("${ChatColor.GRAY} {playerDisplayName} - The display name of the player")
                desc.add("${ChatColor.GRAY} {playerUuid} - The UUID of the player")
                desc.add("${ChatColor.GRAY} {rewardName} - The name of this reward")
                desc.add("${ChatColor.GRAY} {chance} - The chance of winning this reward")
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit commands")
            }
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

    companion object {
        private const val PERCENT_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg1YmU3NmRlMjhkZGNiMzlkMjgzZTNkNzFmNmVkNjNkZTg1NGY4Mzk2MjNlYzE4YTUzODBjODRmMWMyNWY5In19fQ=="
    }

}