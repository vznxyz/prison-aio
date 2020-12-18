/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.menu.buttons.StaticItemStackButton
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.tool.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.module.user.menu.ToggleEnchantMessagesMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

class PickaxeMenu(
    private val pickaxeItem: ItemStack,
    private val pickaxeData: PickaxeData
) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Pickaxe"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            buttons[4] = StaticItemStackButton(pickaxeItem.clone())
            buttons[20] = RenamePickaxeButton()
            buttons[22] = PrestigePickaxeButton()
            buttons[24] = EnchantPickaxeButton()
            buttons[38] = ToggleEnchantsButton()
            buttons[40] = ToggleEnchantMessagesButton()
//            buttons[42] = SalvagePickaxeButton()

            for (i in 0 until 54) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return 54
    }

    private inner class RenamePickaxeButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Rename Pickaxe"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Give your pickaxe its own name, with colors & styles."))
                desc.add("")
                desc.add("${ChatColor.RED}${ChatColor.BOLD}Warning")
                desc.addAll(TextSplitter.split(text = "You can be punished for applying an inappropriate name to your pickaxe."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to rename pickaxe")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NAME_TAG
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {

            }
        }
    }

    private inner class PrestigePickaxeButton : Button() {
        override fun getName(player: Player): String {
            val nextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)
            return if (nextPrestige == null) {
                "${ChatColor.AQUA}${ChatColor.BOLD}Max Prestige"
            } else {
                "${ChatColor.AQUA}${ChatColor.BOLD}Prestige Pickaxe"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Prestige your pickaxe to unlock new enchant limits."))
                desc.add("")

                val nextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)
                if (nextPrestige != null) {
                    desc.add("${ChatColor.RED}${ChatColor.BOLD}Prestige Requirements")
                    desc.addAll(nextPrestige.renderRequirements(player, pickaxeData))
                    desc.add("")

                    if (nextPrestige.meetsRequirements(player, pickaxeData)) {
                        desc.add("${ChatColor.YELLOW}Click to prestige pickaxe")
                    } else {
                        desc.addAll(TextSplitter.split(text = "This pickaxe doesn't meet the requirements to prestige!", linePrefix = ChatColor.RED.toString()))
                    }
                } else {
                    desc.add("${ChatColor.YELLOW}${ChatColor.BOLD}Enchant Limits")

                    val enchantLimits = PickaxePrestigeHandler.findEnchantLimits(pickaxeData.prestige)
                    if (enchantLimits.isEmpty()) {
                        desc.add("${ChatColor.GRAY}None")
                    } else {
                        for (enchant in EnchantHandler.getRegisteredEnchants()) {
                            val enchantLimit = pickaxeData.getEnchantLimit(enchant)
                            if (enchantLimit != -1) {
                                desc.add("${enchant.lorified()} ${NumberUtils.format(enchantLimit)}")
                            }
                        }
                    }

                    desc.add("")
                    desc.addAll(TextSplitter.split(text = "This pickaxe has reached the maximum prestige!", linePrefix = ChatColor.RED.toString()))
                }
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.NETHER_STAR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val nextPrestige = PickaxePrestigeHandler.getNextPrestige(pickaxeData.prestige)
            if (nextPrestige != null) {
                if (!nextPrestige.meetsRequirements(player, pickaxeData)) {
                    player.sendMessage("${ChatColor.RED}You don't meet the requirements to prestige your pickaxe!")
                    return
                }

                ConfirmMenu { confirmed ->
                    if (confirmed) {
                        nextPrestige.purchase(player, pickaxeItem, pickaxeData)

                        player.sendMessage("")
                        player.sendMessage(" ${ChatColor.GREEN}${ChatColor.BOLD}Acquired Pickaxe Prestige ${pickaxeData.prestige}")
                        player.sendMessage(" ${ChatColor.GRAY}Your pickaxe has reached the next prestige!")

                        val oldLimits = PickaxePrestigeHandler.findEnchantLimits(nextPrestige.number - 1)
                        val newLimits = PickaxePrestigeHandler.findEnchantLimits(nextPrestige.number)

                        val changedLimits = newLimits.filter { it.value != it.key.maxLevel && oldLimits.containsKey(it.key) && oldLimits.getValue(it.key) < it.value }
                        if (changedLimits.isNotEmpty()) {
                            player.sendMessage("")
                            player.sendMessage(" ${ChatColor.YELLOW}${ChatColor.BOLD}NEW ENCHANT LIMITS")

                            for ((enchant, limit) in changedLimits) {
                                player.sendMessage(" ${enchant.lorified()} ${ChatColor.GRAY}${NumberUtils.format(limit)}")
                            }
                        }

                        player.sendMessage("")
                    }
                }.openMenu(player)
            } else {
                player.sendMessage("${ChatColor.RED}This pickaxe has reached the maximum prestige!")
            }
        }
    }

    private inner class EnchantPickaxeButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Enchant Pickaxe"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Build your perfect pickaxe with enchantments that give various abilities."))
                desc.add("")

                for (category in EnchantCategory.values()) {
                    desc.add("${ChatColor.GRAY}${Constants.DOT_SYMBOL} ${category.getColoredName()}")
                }

                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enchant pickaxe")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.ENCHANTMENT_TABLE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                PurchaseEnchantsMenu(pickaxeItem, pickaxeData).openMenu(player)
            }
        }
    }

    private inner class ToggleEnchantsButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Toggle Enchants"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Toggle which enchants on this pickaxe will be processed while mining."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to toggle enchants")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.REDSTONE_COMPARATOR
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ToggleEnchantsMenu(pickaxeItem, pickaxeData).openMenu(player)
            }
        }
    }

    private inner class ToggleEnchantMessagesButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}Toggle Enchant Messages"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                desc.add("")
                desc.addAll(TextSplitter.split(text = "Toggle which enchants can send you messages in chat regarding its ability/cooldown."))
                desc.add("")
                desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to toggle enchant messages")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.SIGN
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                ToggleEnchantMessagesMenu(UserHandler.getUser(player.uniqueId), this@PickaxeMenu).openMenu(player)
            }
        }
    }

}