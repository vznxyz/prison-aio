/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.mine.MineHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlock
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockMine
import net.evilblock.prisonaio.module.mine.variant.luckyblock.reward.menu.LuckyBlockRewardsEditor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class EditLuckyBlockMenu(private val luckyBlock: LuckyBlock) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit LuckyBlock - ${luckyBlock.name}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[1] = EditNameButton()
            buttons[2] = EditBlockTypeButton()
            buttons[3] = EditRewardsButton()
            buttons[4] = EditMinRewardsButton()
            buttons[5] = EditMaxRewardsButton()
            buttons[6] = EditSpawnChanceButton()
            buttons[7] = EditSkinSourceButton()

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                LuckyBlockEditor().openMenu(player)
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
                desc.addAll(TextSplitter.split(text = "The name is how you want this LuckyBlock to be displayed in menu and chat text."))
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
                        luckyBlock.name = ChatColor.translateAlternateColorCodes('&', input)

                        Tasks.async {
                            LuckyBlockHandler.saveData()
                        }

                        this@EditLuckyBlockMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditBlockTypeButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Block Type"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The Minecraft block type that represents this LuckyBlock type."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit block type")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectItemStackMenu(title = "Select a Block") { selectedItem ->
                    if (!selectedItem.type.isBlock) {
                        player.sendMessage("${ChatColor.RED}The item you selected is not a block!")
                        return@SelectItemStackMenu
                    }

                    luckyBlock.blockType = selectedItem.clone()

                    Tasks.async {
                        LuckyBlockHandler.saveData()
                    }

                    this@EditLuckyBlockMenu.openMenu(player)
                }.openMenu(player)
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(luckyBlock.blockType)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }
    }

    private inner class EditRewardsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Rewards"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The possible rewards a player can win for mining this type of LuckyBlock."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit rewards")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.CHEST
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                LuckyBlockRewardsEditor(luckyBlock = luckyBlock).openMenu(player)
            }
        }
    }

    private inner class EditMinRewardsButton : TexturedHeadButton(texture = Constants.IB_WOOD_NUMBER_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Min Rewards ${ChatColor.GRAY}(${NumberUtils.format(luckyBlock.minRewards)})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = ""))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase min rewards by +1")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease min rewards by -1")
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase min rewards by +10")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease min rewards by -10")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = if (clickType.isShiftClick) {
                10
            } else {
                1
            }

            if (clickType.isLeftClick) {
                luckyBlock.minRewards += mod
            } else if (clickType.isRightClick) {
                luckyBlock.minRewards -= mod
            }

            Tasks.async {
                LuckyBlockHandler.saveData()
            }
        }
    }

    private inner class EditMaxRewardsButton : TexturedHeadButton(texture = Constants.IB_WOOD_NUMBER_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Max Rewards ${ChatColor.GRAY}(${NumberUtils.format(luckyBlock.maxRewards)})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.addAll(TextSplitter.split(text = ""))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to increase max rewards by +1")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to decrease max rewards by -1")
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to increase max rewards by +10")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}SHIFT RIGHT-CLICK ${ChatColor.RED}to decrease max rewards by -10")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val mod = if (clickType.isShiftClick) {
                10
            } else {
                1
            }

            if (clickType.isLeftClick) {
                luckyBlock.maxRewards += mod
            } else if (clickType.isRightClick) {
                luckyBlock.maxRewards -= mod
            }

            Tasks.async {
                LuckyBlockHandler.saveData()
            }
        }
    }

    private inner class EditSpawnChanceButton : TexturedHeadButton(PERCENT_HEAD_TEXTURE) {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Spawn Chance ${ChatColor.GRAY}(${NumberUtils.format(luckyBlock.spawnChance)})"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "The chance of this LuckyBlock type spawning in a LuckyBlock mine."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit chance")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                player.closeInventory()

                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a new spawn chance (percentage) for the block type. ${ChatColor.GRAY}(0.0-100.0)")
                    .withRegex(NumberPrompt.NUMBER_REGEX)
                    .acceptInput { number ->
                        try {
                            luckyBlock.spawnChance = number.replace("%", "").replace(",", "").toDouble()
                        } catch (e: NumberFormatException) {
                            player.sendMessage("${ChatColor.RED}Could not parse input to percentage.")
                            return@acceptInput
                        }

                        Tasks.async {
                            LuckyBlockHandler.saveData()
                        }

                        player.sendMessage("${ChatColor.GREEN}Successfully updated block type's spawn chance.")

                        openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    private inner class EditSkinSourceButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Skin Source"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Change the skin source, which is the username or texture ID to fetch texture data for."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit skin source")
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(ItemUtils.getPlayerHeadItem(player.name))
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                InputPrompt()
                    .withText("${ChatColor.GREEN}Please input a username or texture ID.")
                    .acceptInput { input ->
                        if (input.contains(" ")) {
                            player.sendMessage("${ChatColor.RED}Your input must not contain any white-space characters!")
                            return@acceptInput
                        }

                        luckyBlock.skinSource = input

                        Tasks.async {
                            LuckyBlockHandler.saveData()

                            for (mine in MineHandler.getMines()) {
                                if (mine is LuckyBlockMine) {
                                    for (spawn in mine.spawnedEntities) {
                                        if (spawn.luckyBlock == luckyBlock) {
                                            spawn.updateForCurrentWatchers()
                                        }
                                    }
                                }
                            }
                        }

                        this@EditLuckyBlockMenu.openMenu(player)
                    }
                    .start(player)
            }
        }
    }

    companion object {
        private const val PERCENT_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg1YmU3NmRlMjhkZGNiMzlkMjgzZTNkNzFmNmVkNjNkZTg1NGY4Mzk2MjNlYzE4YTUzODBjODRmMWMyNWY5In19fQ=="
    }

}