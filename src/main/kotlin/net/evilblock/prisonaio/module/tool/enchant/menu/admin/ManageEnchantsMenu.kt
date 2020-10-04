/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.menu.admin

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.config.menu.SelectPriceFormulaMenu
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta

class ManageEnchantsMenu : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Manage Enchants"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (enchant in EnchantsManager.getRegisteredEnchants().sortedWith(EnchantsManager.ENCHANT_COMPARATOR)) {
            buttons[buttons.size] = EnchantButton(enchant)
        }

        return buttons
    }

    private inner class EnchantButton(private val enchant: AbstractEnchant) : Button() {
        override fun getName(player: Player): String {
            return "${enchant.textColor}${ChatColor.BOLD}${enchant.enchant}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()

            if (EnchantsManager.config.isEnchantEnabled(enchant)) {
                description.add("${ChatColor.GRAY}Enabled: ${ChatColor.GREEN}yes")
            } else {
                description.add("${ChatColor.GRAY}Enabled: ${ChatColor.RED}no")
            }

            val formula = EnchantsManager.config.getEnchantPriceFormula(enchant)
            description.addAll(formula.getVariablesPreview())

            description.add("")

            for (editAction in formula.getEditActions()) {
                val click = when (editAction.clickType) {
                    ClickType.LEFT -> "LEFT-CLICK"
                    ClickType.RIGHT -> "RIGHT-CLICK"
                    ClickType.SHIFT_LEFT -> "SHIFT LEFT-CLICK"
                    ClickType.SHIFT_RIGHT -> "SHIFT RIGHT-CLICK"
                    ClickType.MIDDLE -> "MIDDLE CLICK"
                    else -> "UNKNOWN CLICK"
                }

                description.add("${editAction.color}${ChatColor.BOLD}$click ${editAction.color}to ${editAction.name}")
            }

            description.add("${ChatColor.AQUA}${ChatColor.BOLD}MIDDLE-CLICK ${ChatColor.AQUA}to change formula")

            if (EnchantsManager.config.isEnchantEnabled(enchant)) {
                description.add("${ChatColor.RED}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.RED}to disable enchant")
            } else {
                description.add("${ChatColor.GREEN}${ChatColor.BOLD}SHIFT LEFT-CLICK ${ChatColor.GREEN}to enable enchant")
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

            val fireworkEffectMeta = itemMeta as FireworkEffectMeta
            fireworkEffectMeta.effect = FireworkEffect.builder().withColor(enchant.iconColor).build()

            return fireworkEffectMeta
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val formula = EnchantsManager.config.getEnchantPriceFormula(enchant)

            for (editAction in EnchantsManager.config.getEnchantPriceFormula(enchant).getEditActions()) {
                if (editAction.clickType == clickType || ((editAction.clickType == ClickType.MIDDLE && clickType == ClickType.CREATIVE) || (editAction.clickType == ClickType.CREATIVE && clickType == ClickType.MIDDLE))) {
                    editAction.clicked.invoke(player, formula)
                    return
                }
            }

            // middle-click
            if (clickType == ClickType.MIDDLE || clickType == ClickType.CREATIVE) {
                SelectPriceFormulaMenu { selectedFormula ->
                    EnchantsManager.config.updateEnchantPriceFormula(enchant, selectedFormula)

                    Tasks.async {
                        EnchantsManager.saveConfig()
                    }
                    
                    this@ManageEnchantsMenu.openMenu(player)
                }.openMenu(player)
            }

            // shift left-click
            if (clickType.isShiftClick && clickType.isLeftClick) {
                if (EnchantsManager.config.isEnchantEnabled(enchant)) {
                    EnchantsManager.config.disableEnchant(enchant)
                } else {
                    EnchantsManager.config.enableEnchant(enchant)
                }

                Tasks.async {
                    EnchantsManager.saveConfig()
                }
            }
        }
    }

}