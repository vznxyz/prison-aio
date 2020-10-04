/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.config.formula

import net.evilblock.cubed.serialize.AbstractTypeSerializable
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

abstract class PriceFormulaType(val name: String) {

    abstract fun getDescription(): List<String>

    abstract fun createFormulaInstance(): PriceFormula

    abstract class PriceFormula : AbstractTypeSerializable {
        abstract fun getDescription(): List<String>

        abstract fun getEditActions(): List<EditAction>

        open fun getVariablesPreview(): List<String> {
            return emptyList()
        }

        abstract fun getCost(level: Int): Long
    }

    data class EditAction(val name: String, val color: ChatColor = ChatColor.GREEN, val clickType: ClickType, val clicked: (Player, PriceFormula) -> Unit)

}