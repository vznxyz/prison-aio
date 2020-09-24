/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.prestige.PickaxePrestigeHandler
import net.evilblock.prisonaio.module.enchant.type.*
import net.evilblock.prisonaio.module.quest.impl.tutorial.TutorialQuest
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class PurchaseEnchantmentsMenu(private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Menu() {

    companion object {
        private val BLACK_SLOTS = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 44,
            45, 53
        )
    }

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Purchase Enchantments"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        buttons[0] = TokenBalanceButton()
        buttons[2] = RefundEnchantmentsButton(pickaxeItem, pickaxeData)
        buttons[4] = PickaxeButton(pickaxeItem.clone(), pickaxeData) { this.openMenu(player) }
        buttons[6] = SalvagePickaxeButton(this, pickaxeItem, pickaxeData)
        buttons[8] = ExitButton()

        buttons[10] = PurchaseEnchantmentButton(Nuke)
        buttons[19] = PurchaseEnchantmentButton(JackHammer)
        buttons[28] = PurchaseEnchantmentButton(Explosive)
        buttons[37] = PurchaseEnchantmentButton(MineBomb)
        buttons[46] = PurchaseEnchantmentButton(Laser)

        buttons[11] = PurchaseEnchantmentButton(Exporter)
        buttons[20] = PurchaseEnchantmentButton(Greed)
        buttons[29] = PurchaseEnchantmentButton(Luck)
        buttons[38] = PurchaseEnchantmentButton(LuckyMoney)
        buttons[47] = PurchaseEnchantmentButton(TokenPouch)

        buttons[12] = PurchaseEnchantmentButton(Efficiency)
        buttons[21] = PurchaseEnchantmentButton(Unbreaking)
        buttons[30] = PurchaseEnchantmentButton(Speed)
        buttons[39] = PurchaseEnchantmentButton(Jump)
        buttons[48] = PurchaseEnchantmentButton(Haste)

        buttons[13] = PurchaseEnchantmentButton(Fortune)
        buttons[22] = PurchaseEnchantmentButton(Tokenator)
        buttons[31] = PurchaseEnchantmentButton(Locksmith)
        buttons[40] = PurchaseEnchantmentButton(Scavenger)

        buttons[16] = PurchaseEnchantmentButton(Cubed)

        for (i in BLACK_SLOTS) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(15)
            }
        }

        return buttons
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        TutorialQuest.getProgress(player).openedEnchantsMenu = true
    }

    private inner class PurchaseEnchantmentButton(private val enchant: AbstractEnchant) : Button() {
        override fun getName(player: Player): String {
            val currentLevel = pickaxeData.enchants[enchant] ?: 0

            if (enchant == Cubed) {
                return "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant} ${ChatColor.GRAY}(Lvl ${NumberUtils.format(currentLevel)})"
            }

            return if (currentLevel >= enchant.maxLevel) {
                "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant}"
            } else {
                val nextLevel = currentLevel + 1
                "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant} ${ChatColor.GRAY}(Lvl ${NumberUtils.format(currentLevel)} -> ${NumberUtils.format(nextLevel)})"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val user = UserHandler.getUser(player.uniqueId)
            val currentLevel = pickaxeData.enchants[enchant] ?: 0
            val nextLevel = currentLevel + 1

            val description: MutableList<String> = ArrayList()

            description.addAll(
                TextSplitter.split(
                length = 40,
                text = enchant.readDescription(),
                linePrefix = "${ChatColor.GRAY}"
            ))

            description.add("")

            if (enchant != Cubed) {
                description.add("${ChatColor.GRAY}Price: ${ChatColor.GREEN}${ChatColor.BOLD}${Formats.formatTokens(enchant.getCost(nextLevel))}")
            }

            description.add("${ChatColor.GRAY}Max Level: ${ChatColor.GOLD}${ChatColor.BOLD}${NumberUtils.format(enchant.maxLevel.toLong())}")

            val isMaxed = currentLevel >= enchant.maxLevel
            if (isMaxed) {
                description.add("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Maxed")
            }

            if (enchant == Cubed) {
                description.add("")
                description.addAll(TextSplitter.split(text = "Cubed can't be purchased and can only be applied through Enchanted Books.", linePrefix = ChatColor.RED.toString()))
                return description
            }

            if (user.hasTokenBalance(enchant.getCost(nextLevel))) {
                description.add("")
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Click to purchase")
                description.add("${ChatColor.YELLOW}${ChatColor.BOLD}Press ${ChatColor.AQUA}${ChatColor.BOLD}Q ${ChatColor.YELLOW}${ChatColor.BOLD}to Buy Max")
            } else {
                if (!isMaxed) {
                    description.add("")
                    description.add("${ChatColor.RED}${ChatColor.BOLD}Not Enough Tokens")
                }
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.FIREWORK_CHARGE
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            // add glow to item if the player has maxed out this enchantment
            val currentLevel = pickaxeData.enchants[enchant] ?: 0
            if (currentLevel >= enchant.maxLevel) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            }

            // hide useless info
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS)
            itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON)
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

            // set color of firework charge
            val fireworkEffectMeta = itemMeta as FireworkEffectMeta
            fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.iconColor).build()

            return fireworkEffectMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (enchant == Cubed) {
                return
            }

            val currentLevel = pickaxeData.enchants[enchant] ?: 0
            val nextLevel = currentLevel + 1

            if (currentLevel >= enchant.maxLevel) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe already has the max level enchant for the ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}enchantment.")
                return
            }

            val enchantLimit = pickaxeData.getEnchantLimit(enchant)
            if (enchantLimit != -1 && currentLevel >= enchantLimit) {
                val maxPrestige = PickaxePrestigeHandler.getMaxPrestige()
                if (maxPrestige != null && pickaxeData.prestige >= maxPrestige.number) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You've completely maxed your pickaxe prestige and can't purchase anymore ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}levels.")
                } else {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You must prestige your pickaxe to purchase anymore ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}levels.")
                }

                return
            }

            val user = UserHandler.getUser(player.uniqueId)
            // left click and drop click are purchase actions, so we can check a few conditions collectively
            if (clickType == ClickType.LEFT || clickType == ClickType.DROP) {
                if (user.hasTokenBalance(enchant.getCost(nextLevel))) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You don't have enough tokens to purchase the ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}enchantment.")
                    return
                }
            }

            if (clickType == ClickType.LEFT) {
                val cost = enchant.getCost(nextLevel)

                player.sendMessage("${EnchantsManager.CHAT_PREFIX}You purchased ${enchant.textColor}${ChatColor.BOLD}1 ${enchant.enchant} ${ChatColor.GRAY}level for ${Formats.formatTokens(cost)}${ChatColor.GRAY}.")

                user.subtractTokensBalance(cost)

                EnchantsManager.upgradeEnchant(player, pickaxeData, pickaxeItem, enchant, 1, false)
            } else if (clickType == ClickType.DROP) { // purchase max levels
                var levelsPurchased = 0
                var levelsCost = 0L

                val maxLevel = if (enchantLimit == -1) {
                    enchant.maxLevel
                } else {
                    enchantLimit
                }

                for (level in currentLevel + 1 until maxLevel + 1) {
                    val cost = enchant.getCost(level)
                    if (user.hasTokenBalance(levelsCost + cost)) {
                        break
                    }

                    levelsPurchased++
                    levelsCost += cost
                }

                if (levelsPurchased == 0) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You couldn't afford to purchase any levels.")
                    return
                }

                player.sendMessage("${EnchantsManager.CHAT_PREFIX}You purchased ${enchant.textColor}${ChatColor.BOLD}${NumberUtils.format(levelsPurchased)} ${enchant.enchant} ${ChatColor.GRAY}level${pluralize(levelsPurchased)} for ${Formats.formatTokens(levelsCost)}${ChatColor.GRAY}.")

                user.subtractTokensBalance(levelsCost)

                EnchantsManager.upgradeEnchant(player, pickaxeData, pickaxeItem, enchant, levelsPurchased, false)
            }
        }

        private fun pluralize(num: Int): String {
            return if (num == 1) {
                ""
            } else {
                "s"
            }
        }

    }

}