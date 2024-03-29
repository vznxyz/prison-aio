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
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.tool.pickaxe.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.tool.enchant.impl.*
import net.evilblock.prisonaio.module.tool.pickaxe.menu.button.GoToEnchantMenuButton
import net.evilblock.prisonaio.module.tool.pickaxe.menu.button.SalvagePickaxeButton
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.mechanic.economy.Currency
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
        return "Refund Enchantments"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

//        buttons[0] = TokenBalanceButton()
        buttons[2] = GoToEnchantMenuButton(pickaxeItem, pickaxeData)
        buttons[4] = StaticItemStackButton(pickaxeItem.clone())
        buttons[6] = SalvagePickaxeButton(this, pickaxeItem, pickaxeData)
//        buttons[8] = ExitButton()

        buttons[10] = RefundEnchantmentButton(Efficiency)
        buttons[19] = RefundEnchantmentButton(Unbreaking)
        buttons[28] = RefundEnchantmentButton(Speed)
        buttons[37] = RefundEnchantmentButton(Jump)
        buttons[46] = RefundEnchantmentButton(Haste)

        buttons[11] = RefundEnchantmentButton(Exporter)
        buttons[20] = RefundEnchantmentButton(Greed)
        buttons[29] = RefundEnchantmentButton(Luck)
        buttons[38] = RefundEnchantmentButton(LuckyMoney)
        buttons[47] = RefundEnchantmentButton(TokenPouch)


        buttons[12] = RefundEnchantmentButton(Fortune)
        buttons[21] = RefundEnchantmentButton(Tokenator)
        buttons[30] = RefundEnchantmentButton(Locksmith)
        buttons[39] = RefundEnchantmentButton(Scavenger)

        buttons[13] = RefundEnchantmentButton(Nuke)
        buttons[22] = RefundEnchantmentButton(JackHammer)
        buttons[31] = RefundEnchantmentButton(Explosive)
        buttons[40] = RefundEnchantmentButton(MineBomb)
        buttons[49] = RefundEnchantmentButton(Cubed)

        buttons[14] = RefundEnchantmentButton(Zeus)
        buttons[23] = RefundEnchantmentButton(Laser)

        for (i in BLACK_SLOTS) {
            if (!buttons.containsKey(i)) {
                buttons[i] = GlassButton(7)
            }
        }

        return buttons
    }

    private inner class RefundEnchantmentButton(private val enchant: Enchant) : Button() {
        override fun getName(player: Player): String {
            return if (pickaxeData.enchants.containsKey(enchant)) {
                val enchantLevels = pickaxeData.enchants.getValue(enchant)
                "${enchant.getCategory().textColor}${ChatColor.BOLD}${enchant.enchant} ${ChatColor.GRAY}(Lvl ${NumberUtils.format(enchantLevels)})"
            } else {
                "${ChatColor.GRAY}${ChatColor.BOLD}${enchant.enchant}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (enchant == Cubed) {
                description.addAll(TextSplitter.split(
                    text = "Cubed cannot be refunded, but it can be discarded. If you chose to discard it, you will not be compensated in any way.",
                    linePrefix = ChatColor.GRAY.toString()
                ))

                if (pickaxeData.enchants.containsKey(enchant)) {
                    description.add("")
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO DISCARD ENCHANT")
                }
            } else if (pickaxeData.enchants.containsKey(enchant)) {
                val refundableEnchants = SalvagePreventionHandler.getRefundableEnchants(pickaxeItem, pickaxeData)
                if (refundableEnchants.containsKey(enchant) && refundableEnchants.getValue(enchant) > 0) {
                    val enchantLevels = pickaxeData.enchants.getValue(enchant)
                    val refundableLevels = refundableEnchants.getValue(enchant)

                    description.addAll(TextSplitter.split(
                        text = "You will receive ${Formats.formatTokens(enchant.getRefundTokens(refundableLevels))} ${ChatColor.GRAY}(1/4th cost) for refunding this enchantment.",
                        linePrefix = ChatColor.GRAY.toString()
                    ))

                    if (enchantLevels != refundableLevels) {
                        description.add("")
                        description.addAll(TextSplitter.split(
                            text = "You can only refund ${ChatColor.BOLD}${NumberUtils.format(refundableLevels)} ${ChatColor.RED}of the ${ChatColor.BOLD}${NumberUtils.format(enchantLevels)} ${ChatColor.RED}${enchant.enchant} levels, because some are soulbound to the pickaxe.",
                            linePrefix = ChatColor.RED.toString()
                        ))
                    }

                    description.add("")
                    description.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO ACCEPT REFUND")
                } else {
                    description.addAll(TextSplitter.split(
                        text = "You can't refund any of your ${enchant.enchant} levels because they are all soulbound to the pickaxe.",
                        linePrefix = ChatColor.RED.toString()
                    ))
                }
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
                fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.getCategory().iconColor).build()
                return fireworkEffectMeta
            }

            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                if (enchant == Cubed) {
                    if (pickaxeData.enchants.containsKey(Cubed)) {
                        ConfirmMenu("Discard Cubed?") { confirmed ->
                            if (confirmed) {
                                val level = pickaxeData.enchants[enchant]!!

                                pickaxeData.removeEnchant(enchant)
                                pickaxeData.applyMeta(pickaxeItem)

                                player.updateInventory()
                                player.sendMessage("${EnchantHandler.CHAT_PREFIX}You have discarded your pickaxe's ${enchant.getCategory().textColor}${ChatColor.BOLD}Level $level ${enchant.enchant} ${ChatColor.GRAY}enchant.")
                            } else {
                                player.sendMessage("${EnchantHandler.CHAT_PREFIX}${ChatColor.RED}Aborted discarding of Cubed enchantment!")
                            }

                            this@RefundEnchantsMenu.openMenu(player)
                        }.openMenu(player)
                    }

                    return
                }

                if (pickaxeData.enchants.containsKey(enchant)) {
                    val refundableEnchants = SalvagePreventionHandler.getRefundableEnchants(pickaxeItem, pickaxeData)
                    if (refundableEnchants.containsKey(enchant) && refundableEnchants.getValue(enchant) > 0) {
                        ConfirmMenu("Accept Refund?") { confirmed ->
                            if (confirmed) {
                                val refundableLevels = refundableEnchants.getValue(enchant)
                                val refundedTokens = enchant.getRefundTokens(refundableLevels)

                                Currency.Type.TOKENS.give(player.uniqueId, refundedTokens)

                                pickaxeData.removeEnchant(enchant)
                                pickaxeData.applyMeta(pickaxeItem)

                                player.sendMessage("${EnchantHandler.CHAT_PREFIX}You have refunded your pickaxe's ${enchant.getCategory().textColor}${ChatColor.BOLD}Level $refundableLevels ${enchant.enchant} ${ChatColor.GRAY}enchant for ${Formats.formatTokens(refundedTokens)}${ChatColor.GRAY}.")
                                player.updateInventory()
                            } else {
                                player.sendMessage("${EnchantHandler.CHAT_PREFIX}${ChatColor.RED}Aborted refund!")
                            }

                            this@RefundEnchantsMenu.openMenu(player)
                        }.openMenu(player)
                    } else {
                        player.sendMessage("${EnchantHandler.CHAT_PREFIX}${ChatColor.RED}Your pickaxe doesn't have any refundable ${enchant.getCategory().textColor}${ChatColor.BOLD}${enchant.enchant} ${ChatColor.RED}levels.")
                    }
                }
            }
        }
    }

}