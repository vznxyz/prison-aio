/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.luckyblock.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.buttons.AddButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.EzPrompt
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlockHandler
import net.evilblock.prisonaio.module.mine.variant.luckyblock.LuckyBlock
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class LuckyBlockEditor : PaginatedMenu() {

    init {
        updateAfterClick = true
    }

    override fun getPrePaginatedTitle(player: Player): String {
        return "LuckyBlock Editor"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (blockType in LuckyBlockHandler.getBlockTypes()) {
            buttons[buttons.size] = LuckyBlockButton(blockType)
        }

        return buttons
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        val buttons = hashMapOf<Int, Button>()

        buttons[2] = AddLuckyBlockButton()

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

    private inner class AddLuckyBlockButton : AddButton() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Add LuckyBlock Type"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Add a new LuckyBlock by completing the setup procedure."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to add block type")
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EzPrompt.Builder()
                    .charLimit(48)
                    .promptText("${ChatColor.GREEN}Please input an ID for the LuckyBlock.")
                    .acceptInput { input ->
                        if (LuckyBlockHandler.getBlockTypeById(input) != null) {
                            player.sendMessage("${ChatColor.RED}That ID is taken by another LuckyBlock type!")
                            return@acceptInput
                        }

                        val luckyBlock = LuckyBlock(input)

                        Tasks.async {
                            LuckyBlockHandler.trackBlockType(luckyBlock)
                            LuckyBlockHandler.saveData()
                        }

                        EditLuckyBlockMenu(luckyBlock).openMenu(player)
                    }
                    .build()
                    .start(player)
            }
        }
    }

    private inner class LuckyBlockButton(private val luckyBlock: LuckyBlock) : Button() {
        override fun getName(player: Player): String {
            return luckyBlock.name
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("${ChatColor.GRAY}(ID: ${luckyBlock.id})")
                desc.add("")
                desc.add("${ChatColor.GRAY}Rewards: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(luckyBlock.rewards.size)}")
                desc.add("${ChatColor.GRAY}Chance: ${ChatColor.YELLOW}${ChatColor.BOLD}${NumberUtils.format(luckyBlock.spawnChance)}")
                desc.add("${ChatColor.GRAY}Skin Source: ${ChatColor.YELLOW}${ChatColor.BOLD}${luckyBlock.skinSource ?: "None"}")
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit block type")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to delete block type")
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.copyOf(luckyBlock.blockType)
                .name(getName(player))
                .setLore(getDescription(player))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                EditLuckyBlockMenu(luckyBlock).openMenu(player)
            } else if (clickType.isRightClick) {
                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        LuckyBlockHandler.forgetBlockType(luckyBlock)

                        Tasks.async {
                            LuckyBlockHandler.saveData()
                        }
                    }

                    this@LuckyBlockEditor.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}