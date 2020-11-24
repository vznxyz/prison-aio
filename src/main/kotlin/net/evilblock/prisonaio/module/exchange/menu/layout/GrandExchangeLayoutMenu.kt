/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.layout

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import org.bukkit.entity.Player

abstract class GrandExchangeLayoutMenu : PaginatedMenu() {

    init {
        updateAfterClick = true
        verticalView = true
    }

    override fun getGlobalButtons(player: Player): Map<Int, Button>? {
        return hashMapOf<Int, Button>().also {
            GrandExchangeLayout.renderLayout(it)
        }
    }

    override fun getAllPagesButtonSlots(): List<Int> {
        return ITEM_SLOTS
    }

    override fun getMaxItemsPerPage(player: Player): Int {
        return 40
    }

    override fun getPageButtonSlots(): Pair<Int, Int> {
        return Pair(9, 45)
    }

    companion object {
        private val ITEM_SLOTS = arrayListOf<Int>().also {
            it.addAll(10..17)
            it.addAll(19..26)
            it.addAll(28..35)
            it.addAll(37..44)
            it.addAll(46..53)
        }
    }

}