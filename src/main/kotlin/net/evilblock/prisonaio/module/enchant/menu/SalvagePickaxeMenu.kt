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
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.button.*
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.enchant.type.Cubed
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.text.NumberFormat
import java.util.*

class SalvagePickaxeMenu(internal val pickaxeItem: ItemStack, internal val pickaxeData: PickaxeData) : Menu() {

    init {
        this.updateAfterClick = true
        this.placeholder = true
        this.keepBottomMechanics = false
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
        buttons[6] = PurchaseEnchantsMenuButton()
        buttons[8] = RefundsButton()

        // footer buttons
        buttons[49] = ExitButton()

        // middle button
        buttons[22] = ConfirmSalvageButton()

        return buttons
    }

    private inner class PurchaseEnchantsMenuButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${ChatColor.RED}${ChatColor.BOLD}Purchase Enchants ${ChatColor.GRAY}${Constants.DOUBLE_ARROW_LEFT}"
        }

        override fun getDescription(player: Player): List<String> {
            return listOf("${ChatColor.GRAY}Click here to purchase enchants")
        }

        override fun getMaterial(player: Player): Material {
            return Material.EXP_BOTTLE
        }

        override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            return itemMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType == ClickType.LEFT) {
                PurchaseEnchantMenu(pickaxeItem, pickaxeData).openMenu(player)
            }
        }
    }

    private inner class ConfirmSalvageButton : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.RED}${ChatColor.BOLD}Salvage Returns Preview"
        }

        override fun getDescription(player: Player): List<String> {
            val description: MutableList<String> = ArrayList()
            val enchants = SalvagePreventionHandler.getSalvageableLevels(pickaxeItem, pickaxeData)

            enchants.entries
                .filter { entry -> entry.key !is Cubed }
                .sortedWith(EnchantsManager.ENCHANT_COMPARATOR)
                .forEach { entry ->
                    val formattedReturns = NumberFormat.getInstance().format(entry.key.getSalvageReturns(entry.value))
                    description.add("${ColorUtil.toChatColor(entry.key.iconColor)}${ChatColor.BOLD}❙ ${ChatColor.GRAY}${entry.key.getStrippedEnchant()} ${entry.value} (${ChatColor.GOLD}$formattedReturns${ChatColor.GRAY})")
                }

            description.add("")

            val totalReturns = enchants.entries.stream()
                .filter { entry -> entry.key !is Cubed }
                .mapToLong { entry -> entry.key.getSalvageReturns(entry.value) }
                .sum()

            val formattedTotalReturns = NumberFormat.getInstance().format(totalReturns)

            val returnsText = "You will receive ${ChatColor.GOLD}${ChatColor.BOLD}$formattedTotalReturns ${ChatColor.GRAY}tokens from salvaging your pickaxe."
            description.addAll(TextSplitter.split(34, returnsText, ChatColor.GRAY.toString(), " "))

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
                val enchants = SalvagePreventionHandler.getSalvageableLevels(pickaxeItem, pickaxeData)
                if (enchants.isEmpty()) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe doesn't have any enchantments, therefore it cannot be salvaged.")
                    return
                }

                if (enchants.containsKey(Cubed)) {
                    player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe has the Cubed enchantment, which makes the pickaxe un-salvagable.")
                    return
                }

                player.closeInventory()

                ConfirmMenu("Accept salvage?") { confirmed: Boolean ->
                    if (confirmed) {
                        val totalReturns = enchants.entries.stream()
                            .filter { entry -> entry.key !is Cubed }
                            .mapToLong { entry -> entry.key.getSalvageReturns(entry.value) }
                            .sum()

                        val formattedTotalReturns = NumberFormat.getInstance().format(totalReturns)
                        player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.GRAY}You have salvaged your pickaxe for ${ChatColor.GOLD}$formattedTotalReturns ${ChatColor.GRAY}tokens. It is now gone forever.")

                        val indexOfItem = player.inventory.first(pickaxeItem)
                        if (indexOfItem == -1) {
                            return@ConfirmMenu
                        }

                        player.inventory.setItem(indexOfItem, ItemStack(Material.AIR))
                        player.updateInventory()

                        val user = UserHandler.getUser(player.uniqueId)
                        user.addTokensBalance(totalReturns)
                    } else {
                        openMenu(player)
                        player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Aborted salvage!")
                    }
                }.openMenu(player)
            }
        }
    }

}