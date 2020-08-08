/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.type.*
import net.evilblock.prisonaio.module.user.UserHandler
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
import java.text.NumberFormat
import java.util.*

class PurchaseEnchantsMenu(private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Menu() {

    init {
        updateAfterClick = true
        placeholder = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Purchase Enchants"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        // header buttons
        buttons[0] = TokenShopButton()
        buttons[2] = TokenBalanceButton()
        buttons[4] = PickaxeButton(pickaxeItem.clone(), pickaxeData) { this.openMenu(player) }
        buttons[6] = SalvagePickaxeButton(pickaxeItem, pickaxeData)
        buttons[8] = RefundEnchantsButton(pickaxeItem, pickaxeData)

        // footer buttons
        buttons[49] = ExitButton()

        // left column
        buttons[10] = PurchaseEnchantmentButton(MineBomb)
        buttons[11] = PurchaseEnchantmentButton(Explosive)
        buttons[19] = PurchaseEnchantmentButton(Efficiency)
        buttons[20] = PurchaseEnchantmentButton(Unbreaking)
        buttons[28] = PurchaseEnchantmentButton(Speed)
        buttons[29] = PurchaseEnchantmentButton(Luck)
        buttons[37] = PurchaseEnchantmentButton(Jump)
        buttons[38] = PurchaseEnchantmentButton(Haste)

        // middle column
        buttons[13] = PurchaseEnchantmentButton(JackHammer)
        buttons[22] = PurchaseEnchantmentButton(Exporter)
        buttons[31] = PurchaseEnchantmentButton(Fortune)
        buttons[40] = PurchaseEnchantmentButton(Nuke)

        // right column
        buttons[15] = PurchaseEnchantmentButton(Tokenator)
        buttons[16] = PurchaseEnchantmentButton(Locksmith)
        buttons[24] = PurchaseEnchantmentButton(TokenPouch)
        buttons[25] = PurchaseEnchantmentButton(LuckyMoney)
        buttons[33] = PurchaseEnchantmentButton(Greed)
        buttons[34] = PurchaseEnchantmentButton(Scavenger)
        buttons[42] = PurchaseEnchantmentButton(Laser)

        return buttons
    }

    private inner class PurchaseEnchantmentButton(private val enchant: AbstractEnchant) : Button() {
        override fun getName(player: Player): String {
            val currentLevel = pickaxeData.enchants[enchant] ?: 0
            return if (currentLevel >= enchant.maxLevel) {
                "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant}"
            } else {
                val nextLevel = currentLevel + 1
                "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant} ${ChatColor.GRAY}(Lvl $currentLevel -> $nextLevel)"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val user = UserHandler.getUser(player.uniqueId)
            val currentLevel = pickaxeData.enchants[enchant] ?: 0
            val nextLevel = currentLevel + 1

            val description: MutableList<String> = ArrayList()
            description.add("")

            description.addAll(
                TextSplitter.split(
                length = 40,
                text = enchant.readDescription(),
                linePrefix = "${ChatColor.GRAY}"
            ))

            description.add("")
            description.add("${ChatColor.GRAY}Price: ${ChatColor.GREEN}${ChatColor.BOLD}${NumberFormat.getInstance().format(enchant.getCost(nextLevel))} Tokens")
            description.add("${ChatColor.GRAY}Max Level: ${ChatColor.GOLD}${ChatColor.BOLD}${NumberFormat.getInstance().format(enchant.maxLevel.toLong())}")

            val isMaxed = currentLevel >= enchant.maxLevel
            if (isMaxed) {
                description.add("${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}Maxed")
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
            val user = UserHandler.getUser(player.uniqueId)

            val currentLevel = pickaxeData.enchants[enchant] ?: 0
            val nextLevel = currentLevel + 1

            if (currentLevel >= enchant.maxLevel) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe already has the max level enchant for the ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}enchantment.")
                return
            }

            val enchantLimit = pickaxeData.getEnchantLimit(enchant)
            if (enchantLimit != -1 && currentLevel >= enchantLimit) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You must prestige your pickaxe to upgrade the ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}enchantment any further.")
                return
            }

            // left click and drop click are purchase actions, so we can check a few conditions collectively
            if (clickType == ClickType.LEFT || clickType == ClickType.DROP) {
                if (user.getTokenBalance() < enchant.getCost(nextLevel)) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You don't have enough tokens to purchase the ${ChatColor.BOLD}${enchant.getStrippedEnchant()} ${ChatColor.RED}enchantment.")
                    return
                }
            }

            if (clickType == ClickType.LEFT) { // purchase level
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}Upgraded ${ChatColor.RED}${enchant.getStrippedEnchant()} ${ChatColor.GRAY}by ${ChatColor.RED}1 ${ChatColor.GRAY}level.")
                user.subtractTokensBalance(enchant.getCost(nextLevel))

                EnchantsManager.upgradeEnchant(player, pickaxeData, pickaxeItem, enchant, 1, false)
            } else if (clickType == ClickType.DROP) { // purchase max levels
                var levelsPurchased = 0
                var levelsCost = 0.0

                val maxLevel = if (enchantLimit == -1) {
                    enchant.maxLevel
                } else {
                    enchantLimit
                }

                for (level in currentLevel + 1 until maxLevel + 1) {
                    val cost = enchant.getCost(level)
                    if (user.getTokenBalance() < levelsCost + cost) {
                        break
                    }

                    levelsPurchased++
                    levelsCost += cost
                }

                if (levelsPurchased == 0) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You couldn't afford to purchase any levels.")
                    return
                }

                player.sendMessage("${EnchantsManager.CHAT_PREFIX}Upgraded ${ChatColor.RED}${enchant.getStrippedEnchant()} ${ChatColor.GRAY}by ${ChatColor.RED}$levelsPurchased ${ChatColor.GRAY}level${pluralize(levelsPurchased)}.")

                user.subtractTokensBalance(levelsCost.toLong())

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