/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.menu.button

import net.evilblock.cubed.menu.Button
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PickaxeInHandButton(private val pickaxeInHand: ItemStack) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return pickaxeInHand
    }

}