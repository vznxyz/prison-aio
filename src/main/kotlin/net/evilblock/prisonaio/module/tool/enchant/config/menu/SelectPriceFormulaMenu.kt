/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.config.formula.PriceFormulaType
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectPriceFormulaMenu(private val selected: (PriceFormulaType.PriceFormula) -> Unit) : Menu() {

    override fun getTitle(player: Player): String {
        return "Select Price Formula"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        for (formulaType in EnchantsManager.getRegistedPriceFormulaTypes()) {
            buttons[buttons.size] = PriceFormulaTypeButton(formulaType)
        }

        return buttons
    }

    private inner class PriceFormulaTypeButton(private val formulaType: PriceFormulaType) : Button() {
        override fun getName(player: Player): String {
            return "${ChatColor.YELLOW}${ChatColor.BOLD}${formulaType.name}"
        }

        override fun getDescription(player: Player): List<String> {
            val description = arrayListOf<String>()
            description.addAll(formulaType.getDescription())
            description.add("")
            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to select formula")
            return description
        }

        override fun getMaterial(player: Player): Material {
            return Material.DIODE
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                selected.invoke(formulaType.createFormulaInstance())
            }
        }
    }

}