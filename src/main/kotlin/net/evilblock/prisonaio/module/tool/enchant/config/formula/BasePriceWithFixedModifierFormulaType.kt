/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config.formula

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.menu.admin.ManageEnchantsMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import java.lang.reflect.Type

object BasePriceWithFixedModifierFormulaType : PriceFormulaType("Base Price + Fixed Modifier") {

    override fun getDescription(): List<String> {
        return listOf("${ChatColor.GRAY}base price + (level * fixed modifier)")
    }

    override fun createFormulaInstance(): PriceFormula {
        return BasePriceWithFixedModifierFormula()
    }

    class BasePriceWithFixedModifierFormula : PriceFormulaType.PriceFormula() {
        var basePrice: Int = 0
        var fixedRate: Int = 0

        override fun getDescription(): List<String> {
            return BasePriceWithFixedModifierFormulaType.getDescription()
        }

        override fun getEditActions(): List<EditAction> {
            return listOf(EditAction(name = "edit price variables", clickType = ClickType.LEFT, clicked = { player, formula ->
                EditVariablesMenu(formula as BasePriceWithFixedModifierFormula).openMenu(player)
            }))
        }

        override fun getVariablesPreview(): List<String> {
            return listOf(
                "${ChatColor.GRAY}Base Price: $basePrice",
                "${ChatColor.GRAY}Fixed Rate: $fixedRate"
            )
        }

        override fun getCost(level: Int): Long {
            return (basePrice + (level - 1) * fixedRate).toLong()
        }

        override fun getAbstractType(): Type {
            return BasePriceWithFixedModifierFormula::class.java
        }
    }

    private class EditVariablesMenu(private val formula: BasePriceWithFixedModifierFormula) : Menu() {
        init {
            updateAfterClick = true
        }

        override fun getTitle(player: Player): String {
            return "Edit Variables"
        }

        override fun getButtons(player: Player): Map<Int, Button> {
            return mapOf(
                0 to EditBaseCostButton(),
                1 to EditFixedRateButton()
            )
        }

        override fun onClose(player: Player, manualClose: Boolean) {
            if (manualClose) {
                Tasks.delayed(1L) {
                    ManageEnchantsMenu().openMenu(player)
                }
            }
        }

        private inner class EditBaseCostButton : Button() {
            override fun getName(player: Player): String {
                return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Base Cost"
            }

            override fun getDescription(player: Player): List<String> {
                return listOf(
                    "${ChatColor.GRAY}Current Value: ${formula.basePrice}",
                    "",
                    "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit base cost"
                )
            }

            override fun getMaterial(player: Player): Material {
                return Material.NAME_TAG
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    NumberPrompt().acceptInput { number ->
                        formula.basePrice = number.toInt()

                        Tasks.async {
                            EnchantsManager.saveConfig()
                        }

                        this@EditVariablesMenu.openMenu(player)
                    }.start(player)
                }
            }
        }

        private inner class EditFixedRateButton : Button() {
            override fun getName(player: Player): String {
                return "${ChatColor.AQUA}${ChatColor.BOLD}Edit Fixed Rate"
            }

            override fun getDescription(player: Player): List<String> {
                return listOf(
                    "${ChatColor.GRAY}Current Value: ${formula.fixedRate}",
                    "",
                    "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to edit fixed rate"
                )
            }

            override fun getMaterial(player: Player): Material {
                return Material.NAME_TAG
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                if (clickType.isLeftClick) {
                    NumberPrompt().acceptInput { number ->
                        formula.fixedRate = number.toInt()

                        Tasks.async {
                            EnchantsManager.saveConfig()
                        }

                        this@EditVariablesMenu.openMenu(player)
                    }.start(player)
                }
            }
        }
    }

}