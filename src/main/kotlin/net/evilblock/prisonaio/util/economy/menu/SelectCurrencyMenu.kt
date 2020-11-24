/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.util.economy.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.buttons.GlassButton
import net.evilblock.prisonaio.util.economy.Currency
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class SelectCurrencyMenu(
    private val title: String = "Select a currency...",
    private val simple: Boolean = true,
    private val select: (Currency.Type) -> Unit
) : Menu() {

    override fun getTitle(player: Player): String {
        return title
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = hashMapOf<Int, Button>()

        if (simple) {
            buttons[1] = GlassButton(5)
            buttons[2] = CurrencyButton(Currency.Type.MONEY)
            buttons[3] = GlassButton(5)

            buttons[5] = GlassButton(1)
            buttons[6] = CurrencyButton(Currency.Type.TOKENS)
            buttons[7] = GlassButton(1)

            for (i in 0 until 9) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        } else {
            buttons[9] = GlassButton(5)
            buttons[10] = CurrencyButton(Currency.Type.MONEY)
            buttons[11] = GlassButton(5)

            buttons[12] = GlassButton(1)
            buttons[13] = CurrencyButton(Currency.Type.TOKENS)
            buttons[14] = GlassButton(1)

            buttons[15] = GlassButton(14)
            buttons[16] = CurrencyButton(Currency.Type.PRESTIGE_TOKENS)
            buttons[17] = GlassButton(14)

            for (i in 0 until 27) {
                if (!buttons.containsKey(i)) {
                    buttons[i] = GlassButton(7)
                }
            }
        }

        return buttons
    }

    private inner class CurrencyButton(private val currency: Currency.Type) : Button() {
        override fun getName(player: Player): String {
            return currency.displayName
        }

        override fun getMaterial(player: Player): Material {
            return currency.icon
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isLeftClick) {
                select.invoke(currency)
            }
        }
    }

}