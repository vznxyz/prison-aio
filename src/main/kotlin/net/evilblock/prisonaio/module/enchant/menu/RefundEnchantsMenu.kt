/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.type.*
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class RefundEnchantsMenu(private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Menu() {

    init {
        updateAfterClick = true
        placeholder = true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Refund Enchants"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        // header buttons
        buttons[0] = TokenShopButton()
        buttons[2] = TokenBalanceButton()
        buttons[4] = PickaxeButton(pickaxeItem.clone(), pickaxeData) { this.openMenu(player) }
        buttons[6] = SalvagePickaxeButton(pickaxeItem, pickaxeData)
        buttons[8] = PurchaseEnchantsButton(pickaxeItem, pickaxeData)

        // footer buttons
        buttons[49] = ExitButton()

        // left column
        buttons[10] = RefundEnchantmentButton(MineBomb)
        buttons[11] = RefundEnchantmentButton(Explosive)
        buttons[19] = RefundEnchantmentButton(Efficiency)
        buttons[20] = RefundEnchantmentButton(Unbreaking)
        buttons[28] = RefundEnchantmentButton(Speed)
        buttons[29] = RefundEnchantmentButton(Luck)
        buttons[37] = RefundEnchantmentButton(Jump)
        buttons[38] = RefundEnchantmentButton(Haste)

        // middle column
        buttons[13] = RefundEnchantmentButton(JackHammer)
        buttons[22] = RefundEnchantmentButton(Exporter)
        buttons[31] = RefundEnchantmentButton(Fortune)
        buttons[40] = RefundEnchantmentButton(Nuke)

        // right column
        buttons[15] = RefundEnchantmentButton(Tokenator)
        buttons[16] = RefundEnchantmentButton(Locksmith)
        buttons[24] = RefundEnchantmentButton(TokenPouch)
        buttons[25] = RefundEnchantmentButton(LuckyMoney)
        buttons[33] = RefundEnchantmentButton(Greed)
        buttons[34] = RefundEnchantmentButton(Scavenger)
        buttons[42] = RefundEnchantmentButton(Laser)

        return buttons
    }

    private inner class RefundEnchantmentButton(private val enchant: AbstractEnchant) : Button() {
        override fun getName(player: Player): String {
            return if (pickaxeData.enchants.containsKey(enchant)) {
                "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant} ${ChatColor.GRAY}(Lvl ${pickaxeData.enchants[enchant]!!})"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}${enchant.enchant}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description: MutableList<String> = ArrayList()

            if (enchant == Cubed) {
                description.add("")

                description.addAll(TextSplitter.split(
                    text = "Cubed cannot be refunded, but it can be discarded. If you chose to discard it, you will not be compensated in any way.",
                    linePrefix = ChatColor.GRAY.toString()
                ))

                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO DISCARD ENCHANT")
            } else if (pickaxeData.enchants.containsKey(enchant)) {
                description.add("")

                description.addAll(TextSplitter.split(
                    text = "You will receive ${Formats.formatTokens(enchant.getRefundTokens(pickaxeData.enchants[enchant]!!))} ${ChatColor.GRAY}for refunding this enchantment.",
                    linePrefix = ChatColor.GRAY.toString()
                ))

                description.add("")
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO ACCEPT REFUND")
            }

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.FIREWORK_CHARGE
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS)
            itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON)
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

            if (pickaxeData.enchants.containsKey(enchant)) {
                val fireworkEffectMeta = itemMeta as FireworkEffectMeta
                fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.iconColor).build()
                return fireworkEffectMeta
            }

            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (enchant == Cubed) {
                    ConfirmMenu("Discard Cubed?") { confirmed ->
                        if (confirmed) {
                            val level = pickaxeData.enchants[enchant]!!

                            player.sendMessage("${EnchantsManager.CHAT_PREFIX}You have discard your pickaxe's ${enchant.textColor}${ChatColor.BOLD}Level $level ${enchant.enchant} ${ChatColor.GRAY}enchant.")
                            player.updateInventory()

                            pickaxeData.removeEnchant(enchant)
                            pickaxeData.applyLore(pickaxeItem)
                        } else {
                            player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Aborted discarding of Cubed enchantment!")
                        }

                        this@RefundEnchantsMenu.openMenu(player)
                    }.openMenu(player)

                    return
                }

                if (pickaxeData.enchants.containsKey(enchant)) {
                    ConfirmMenu("Accept Refund?") { confirmed ->
                        if (confirmed) {
                            val level = pickaxeData.enchants[enchant]!!
                            val returns = enchant.getRefundTokens(level)

                            player.sendMessage("${EnchantsManager.CHAT_PREFIX}You have refunded your pickaxe's ${enchant.textColor}${ChatColor.BOLD}Level $level ${enchant.enchant} ${ChatColor.GRAY}enchant for ${Formats.formatTokens(returns)}${ChatColor.GRAY}.")
                            player.updateInventory()

                            Currency.Type.TOKENS.give(player, returns)

                            pickaxeData.removeEnchant(enchant)
                            pickaxeData.applyLore(pickaxeItem)
                        } else {
                            player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Aborted refund!")
                        }

                        this@RefundEnchantsMenu.openMenu(player)
                    }.openMenu(player)
                }
            }
        }
    }

}