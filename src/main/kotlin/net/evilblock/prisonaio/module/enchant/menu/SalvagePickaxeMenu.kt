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
import net.evilblock.cubed.util.bukkit.ColorUtil
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.enchant.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.enchant.type.Cubed
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*

class SalvagePickaxeMenu(private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Menu() {

    init {
        updateAfterClick = true
        placeholder = true
        keepBottomMechanics = false
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.RED}${ChatColor.BOLD}Salvage Pickaxe"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = HashMap()

        // header buttons
        buttons[0] = TokenShopButton()
        buttons[2] = TokenBalanceButton()
        buttons[4] = PickaxeButton(pickaxeItem.clone(), pickaxeData) { this.openMenu(player) }
        buttons[6] = PurchaseEnchantsButton(pickaxeItem, pickaxeData)
        buttons[8] = RefundEnchantsButton(pickaxeItem, pickaxeData)

        // footer buttons
        buttons[49] = ExitButton()

        // middle button
        buttons[22] = ConfirmSalvageButton()

        return buttons
    }

    private inner class ConfirmSalvageButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Salvage Returns"
        }

        override fun getDescription(player: Player): List<String> {
            val description: MutableList<String> = ArrayList()
            val enchants = SalvagePreventionHandler.getRefundableEnchants(pickaxeItem, pickaxeData)

            enchants.entries
                .filter { entry -> entry.key !is Cubed }
                .sortedWith(EnchantsManager.ENCHANT_COMPARATOR)
                .forEach { entry ->
                    description.add("${ColorUtil.toChatColor(entry.key.iconColor)}${ChatColor.BOLD}❙ ${ChatColor.GRAY}${entry.key.getStrippedEnchant()} ${entry.value} (${ChatColor.GOLD}${Formats.formatTokens(entry.key.getRefundTokens(entry.value))}${ChatColor.GRAY})")
                }

            description.add("")

            val totalReturns = enchants.entries.stream()
                .filter { entry -> entry.key !is Cubed }
                .mapToLong { entry -> entry.key.getRefundTokens(entry.value) }
                .sum()

            if (totalReturns > 0) {
                description.addAll(
                    TextSplitter.split(
                        text = "You will receive ${ChatColor.GOLD}${ChatColor.BOLD}${Formats.formatTokens(totalReturns)} ${ChatColor.GRAY}(1/4th original cost) from salvaging your pickaxe.",
                        linePrefix = ChatColor.GRAY.toString()
                    )
                )
            } else {
                description.addAll(
                    TextSplitter.split(
                        text = "You don't have any salvagable enchantments on your pickaxe.",
                        linePrefix = ChatColor.GRAY.toString()
                    )
                )
            }

            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}CLICK TO ACCEPT SALVAGE")

            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.INK_SACK
        }

        override fun getDamageValue(player: Player): Byte {
            return 10
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.LEFT) { // item has no enchants on it, so it cannot be salvaged
                val enchants = SalvagePreventionHandler.getRefundableEnchants(pickaxeItem, pickaxeData)
                if (enchants.isEmpty()) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe doesn't have any salvagable enchantments, therefore it cannot be salvaged.")
                    return
                }

                if (enchants.containsKey(Cubed)) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe has the Cubed enchantment, which makes the pickaxe un-salvagable.")
                    return
                }

                ConfirmMenu("Accept Salvage?") { confirmed: Boolean ->
                    if (confirmed) {
                        val totalReturns = enchants.entries.stream()
                            .filter { entry -> entry.key !is Cubed }
                            .mapToLong { entry -> entry.key.getRefundTokens(entry.value) }
                            .sum()

                        player.sendMessage("${EnchantsManager.CHAT_PREFIX}You have salvaged your pickaxe for ${Formats.formatTokens(totalReturns)}${ChatColor.GRAY}. It is now gone forever...")

                        val indexOfItem = player.inventory.first(pickaxeItem)
                        if (indexOfItem == -1) {
                            return@ConfirmMenu
                        }

                        player.inventory.setItem(indexOfItem, ItemStack(Material.AIR))
                        player.updateInventory()

                        val user = UserHandler.getUser(player.uniqueId)
                        user.addTokensBalance(totalReturns)

                        PickaxeHandler.forgetPickaxeData(pickaxeData)
                    } else {
                        player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Aborted salvage!")
                    }

                    this@SalvagePickaxeMenu.openMenu(player)
                }.openMenu(player)
            }
        }
    }

}