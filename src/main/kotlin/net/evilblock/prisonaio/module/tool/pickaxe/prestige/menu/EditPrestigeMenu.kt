/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.prestige.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.tool.enchant.menu.SelectEnchantMenu
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeInfo
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class EditPrestigeMenu(private val prestige: PickaxePrestigeInfo) : Menu() {

    init {
        autoUpdate = true
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Edit Prestige ${prestige.number}"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        buttons[0] = EditMoneyRequiredButton()
        buttons[1] = EditTokensRequiredButton()
        buttons[2] = EditPrestigeRequiredButton()
        buttons[3] = EditBlocksMinedRequiredButton()
        buttons[4] = EditEnchantLimitsButton()

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            Tasks.delayed(1L) {
                PrestigeEditorMenu().openMenu(player)
            }
        }
    }

    private inner class EditMoneyRequiredButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Money Requirement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "The amount of money a player must have in their balance to level their pickaxe to this prestige.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Current requirement: ${Formats.formatMoney(prestige.moneyRequired.toDouble())}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit money requirement")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EMERALD
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt().withText("${ChatColor.GREEN}Please input the amount of money required.").acceptInput { number ->
                    assert(number.toInt() > 0) { "Number must be above 0" }

                    prestige.moneyRequired = number.toLong()

                    Tasks.async {
                        PickaxePrestigeHandler.saveData()
                    }

                    this@EditPrestigeMenu.openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class EditTokensRequiredButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Tokens Requirement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "The amount of tokens a player must have in their balance to level their pickaxe to this prestige.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Current requirement: ${Formats.formatTokens(prestige.tokensRequired)}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit tokens requirement")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.GOLD_INGOT
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt().withText("${ChatColor.GREEN}Please input the amount of tokens required.").acceptInput { number ->
                    assert(number.toInt() > 0) { "Number must be above 0" }

                    prestige.tokensRequired = number.toLong()

                    Tasks.async {
                        PickaxePrestigeHandler.saveData()
                    }

                    this@EditPrestigeMenu.openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class EditPrestigeRequiredButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Prestige Requirement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "The prestige a player must be to level their pickaxe to this prestige.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Current requirement: ${NumberUtils.format(prestige.prestigeRequired)}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit prestige requirement")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt().withText("${ChatColor.GREEN}Please input the prestige required.").acceptInput { number ->
                    assert(number.toInt() > 0) { "Number must be above 0" }

                    prestige.prestigeRequired = number.toInt()

                    Tasks.async {
                        PickaxePrestigeHandler.saveData()
                    }

                    this@EditPrestigeMenu.openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class EditBlocksMinedRequiredButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Blocks Mined Requirement"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "The amount of blocks a player must have mined to level their pickaxe to this prestige.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Current requirement: ${NumberUtils.format(prestige.blocksMinedRequired)}")
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit blocks mined requirement")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIAMOND_PICKAXE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                NumberPrompt().withText("${ChatColor.GREEN}Please input the amount of blocks mined required.").acceptInput { number ->
                    assert(number.toInt() > 0) { "Number must be more than 0" }

                    prestige.blocksMinedRequired = number.toInt()

                    Tasks.async {
                        PickaxePrestigeHandler.saveData()
                    }

                    this@EditPrestigeMenu.openMenu(player)
                }.start(player)
            }
        }
    }

    private inner class EditEnchantLimitsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Enchant Limits"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            description.add("")

            description.addAll(TextSplitter.split(
                length = 40,
                text = "Limit the enchants a pickaxe can be upgraded to when currently equipped with this prestige.",
                linePrefix = ChatColor.GRAY.toString()
            ))

            description.add("")
            description.add("${ChatColor.YELLOW}${ChatColor.BOLD}ENCHANT LIMITS")

            if (prestige.enchantLimits.isEmpty()) {
                description.add("${ChatColor.GRAY}None")
            } else {
                for ((enchant, level) in prestige.enchantLimits) {
                    description.add("${enchant.lorified()} $level")
                }
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit enchant limits")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTMENT_TABLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                SelectEnchantMenu { enchant ->
                    NumberPrompt().withText("${ChatColor.GREEN}Please input a max level for the enchant.").acceptInput { number ->
                        if (number.toInt() == 0) {
                            prestige.enchantLimits.remove(enchant)
                        } else {
                            assert(number.toInt() > 0) { "Number must be more than 0" }
                            prestige.enchantLimits[enchant] = number.toInt()
                        }

                        Tasks.async {
                            PickaxePrestigeHandler.saveData()
                        }

                        Tasks.delayed(1L) {
                            this@EditPrestigeMenu.openMenu(player)
                        }
                    }.start(player)
                }.openMenu(player)
            }
        }
    }

}