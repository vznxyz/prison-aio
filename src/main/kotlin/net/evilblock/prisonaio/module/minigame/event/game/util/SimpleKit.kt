/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SimpleKit(var inventory: Array<ItemStack>, var armor: Array<ItemStack>) {

    fun giveToPlayer(player: Player) {
        player.inventory.storageContents = inventory.clone()
        player.inventory.armorContents = armor.clone()
        player.updateInventory()
    }

}