/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config.formula

import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.prisonaio.module.tool.enchant.EnchantsManager
import net.evilblock.prisonaio.module.tool.enchant.menu.admin.ManageEnchantsMenu
import org.bukkit.ChatColor
import org.bukkit.event.inventory.ClickType
import java.lang.reflect.Type

object FixedRateFormulaType : PriceFormulaType("Fixed Rate") {

    override fun getDescription(): List<String> {
        return listOf("${ChatColor.GRAY}level * price")
    }

    override fun createFormulaInstance(): PriceFormula {
        return FixedRateFormula()
    }

    class FixedRateFormula : PriceFormula() {
        var rate: Long = 0

        override fun getDescription(): List<String> {
            return FixedRateFormulaType.getDescription()
        }

        override fun getEditActions(): List<EditAction> {
            return listOf(
                EditAction(name = "edit rate", clickType = ClickType.LEFT, clicked = { player, formula ->
                    NumberPrompt().acceptInput { number ->
                        (formula as FixedRateFormula).rate = number.toLong()

                        Tasks.async {
                            EnchantsManager.saveConfig()
                        }

                        ManageEnchantsMenu().openMenu(player)
                    }.start(player)
                })
            )
        }

        override fun getVariablesPreview(): List<String> {
            return listOf("${ChatColor.GRAY}Fixed Rate: $rate")
        }

        override fun getCost(level: Int): Long {
            return level * rate
        }

        override fun getAbstractType(): Type {
            return FixedRateFormula::class.java
        }
    }

}