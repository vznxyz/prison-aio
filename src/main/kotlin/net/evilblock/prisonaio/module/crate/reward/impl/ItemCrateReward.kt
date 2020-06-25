/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.crate.reward.impl

import net.evilblock.prisonaio.module.crate.reward.CrateReward
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemCrateReward(private val itemStack: ItemStack) : CrateReward() {

    override fun getIcon(): ItemStack {
        return itemStack.clone()
    }

    override fun execute(player: Player) {
        super.execute(player)

        if (player.inventory.firstEmpty() == -1) {
            player.enderChest.addItem(itemStack.clone())
        } else {
            player.inventory.addItem(itemStack.clone())
        }
    }

}