/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.BackButton
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeData
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta

class ToggleEnchantsMenu(private val pickaxeItem: ItemStack, private val pickaxeData: PickaxeData) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Toggle Enchants"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->
            for (i in 0..7) {
                buttons[i] = GlassButton(7)
            }

            buttons[0] = BackButton { PickaxeMenu(pickaxeItem, pickaxeData).openMenu(player) }

            buttons[8] = ToggleAllButton()

            val sortedEnchants = EnchantHandler.getRegisteredEnchants().sortedWith(EnchantHandler.ENCHANT_COMPARATOR)
            for ((index, enchant) in sortedEnchants.withIndex()) {
                buttons[9 + index] = ToggleButton(enchant)
            }
        }
    }

    private inner class ToggleButton(private val enchant: Enchant) : Button() {
        override fun getName(player: Player): String {
            return if (pickaxeData.isEnchantDisabled(enchant)) {
                "${ChatColor.RED}${ChatColor.BOLD}${enchant.enchant}"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}${enchant.enchant}"
            }
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also { desc ->
                if (pickaxeData.isEnchantDisabled(enchant)) {
                    desc.addAll(TextSplitter.split(text = "You have the ${enchant.enchant} enchant ${ChatColor.RED}${ChatColor.BOLD}disabled${ChatColor.GRAY}."))
                } else {
                    desc.addAll(TextSplitter.split(text = "You have the ${enchant.enchant} enchant ${ChatColor.GREEN}${ChatColor.BOLD}enabled${ChatColor.GRAY}."))
                }

                desc.add("")

                if (pickaxeData.isEnchantDisabled(enchant)) {
                    desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to enable ${enchant.enchant}")
                } else {
                    desc.add("${ChatColor.RED}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.RED}to disable ${enchant.enchant}")
                }
            }
        }

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder.of(Material.FIREWORK_CHARGE)
                .name(getName(player))
                .setLore(getDescription(player))
                .addFlags(
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_POTION_EFFECTS
                )
                .build()
                .also { item ->
                    val fireworkEffectMeta = item.itemMeta as FireworkEffectMeta
                    fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.getCategory().iconColor).build()
                    item.itemMeta = fireworkEffectMeta
                }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                pickaxeData.toggleEnchant(enchant)
            }
        }
    }

    private inner class ToggleAllButton : Button() {
        override fun getName(player: Player): String {
            val anyEnabled = EnchantHandler.getRegisteredEnchants().any { !pickaxeData.isEnchantDisabled(it) }
            return if (anyEnabled) {
                "${ChatColor.RED}${ChatColor.BOLD}Disable All"
            } else {
                "${ChatColor.GREEN}${ChatColor.BOLD}Enable All"
            }
        }

        override fun getDescription(player: Player): List<String> {
            val anyEnabled = EnchantHandler.getRegisteredEnchants().any { !pickaxeData.isEnchantDisabled(it) }
            return if (anyEnabled) {
                TextSplitter.split(text = "Click to disable all enchants.")
            } else {
                TextSplitter.split(text = "Click to enable all enchants.")
            }
        }

        override fun getMaterial(player: Player): Material {
            return Material.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val anyEnabled = EnchantHandler.getRegisteredEnchants().any { !pickaxeData.isEnchantDisabled(it) }
            if (anyEnabled) {
                for (enchant in EnchantHandler.getRegisteredEnchants()) {
                    if (!pickaxeData.isEnchantDisabled(enchant)) {
                        pickaxeData.toggleEnchant(enchant)
                    }
                }
            } else {
                for (enchant in EnchantHandler.getRegisteredEnchants()) {
                    if (pickaxeData.isEnchantDisabled(enchant)) {
                        pickaxeData.toggleEnchant(enchant)
                    }
                }
            }
        }
    }

}