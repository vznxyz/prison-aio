/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config.formula

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.enchant.menu.admin.ManageEnchantsMenu
import org.bukkit.ChatColor
import org.bukkit.event.inventory.ClickType
import java.lang.reflect.Type

object FixedPriceFormulaType : PriceFormulaType("Fixed Price") {

    override fun getDescription(): List<String> {
        return listOf("${ChatColor.GRAY}price")
    }

    override fun createFormulaInstance(): PriceFormula {
        return FixedPriceFormula()
    }

    class FixedPriceFormula : PriceFormula() {
        var price: Long = 0

        override fun getDescription(): List<String> {
            return FixedPriceFormulaType.getDescription()
        }

        override fun getEditActions(): List<EditAction> {
            return listOf(
                EditAction(name = "edit price", clickType = ClickType.LEFT, clicked = { player, formula ->
                    NumberPrompt().acceptInput { number ->
                        (formula as FixedPriceFormula).price = number.toLong()

                        Tasks.async {
                            EnchantHandler.saveConfig()
                        }

                        ManageEnchantsMenu().openMenu(player)
                    }.start(player)
                })
            )
        }

        override fun getVariablesPreview(): List<String> {
            return listOf("${ChatColor.GRAY}Fixed Price: $price")
        }

        override fun getCost(level: Int): Long {
            return price
        }

        override fun getAbstractType(): Type {
            return FixedPriceFormula::class.java
        }
    }

}