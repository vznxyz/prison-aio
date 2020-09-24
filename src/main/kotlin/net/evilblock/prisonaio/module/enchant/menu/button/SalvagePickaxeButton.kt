/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ColorUtil
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeData
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeHandler
import net.evilblock.prisonaio.module.enchant.salvage.SalvagePreventionHandler
import net.evilblock.prisonaio.module.enchant.type.Cubed
import net.evilblock.prisonaio.module.user.UserHandler
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.ArrayList

class SalvagePickaxeButton(private val origin: Menu, private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.DARK_RED}${ChatColor.BOLD}Salvage Pickaxe"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf("${ChatColor.GRAY}Click here to salvage your pickaxe")
    }

    override fun getMaterial(player: Player): Material {
        return Material.HOPPER
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        return itemMeta
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType == ClickType.LEFT) {
            val enchants = SalvagePreventionHandler.getRefundableEnchants(pickaxeItem, pickaxeData)
            if (enchants.isEmpty()) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe doesn't have any salvagable enchantments, therefore it cannot be salvaged.")
                return
            }

            if (enchants.containsKey(Cubed)) {
                player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}Your pickaxe has the Cubed enchantment, which makes the pickaxe un-salvagable.")
                return
            }

            val description: MutableList<String> = ArrayList()

            enchants.entries
                .filter { entry -> entry.key !is Cubed }
                .sortedWith(EnchantsManager.MAPPED_ENCHANT_COMPARATOR)
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
                        text = "You will receive ${ChatColor.GOLD}${ChatColor.BOLD}${Formats.formatTokens(totalReturns)} ${ChatColor.GRAY}(1/4th cost) from salvaging your pickaxe.",
                        linePrefix = ChatColor.GRAY.toString()
                    )
                )

                ConfirmMenu(title = "Salvage Pickaxe?", extraInfo = description) { confirmed ->
                    if (confirmed) {
                        player.closeInventory()
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
                        origin.openMenu(player)
                    }
                }.openMenu(player)
            }
        }
    }

}