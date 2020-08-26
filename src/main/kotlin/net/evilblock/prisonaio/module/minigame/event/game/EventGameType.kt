/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.event.game

import net.evilblock.prisonaio.util.Permissions
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class EventGameType(
    val displayName: String,
    val description: String,
    val icon: ItemStack,
    val minPlayers: Int,
    val maxPlayers: Int
) {

    SUMO(
        "Sumo",
        "Try and knock your opponent(s) off of the platform!",
        ItemStack(Material.STICK),
        32,
        64
    );

    fun canHost(player: Player): Boolean {
        return player.hasPermission(Permissions.EVENTS_HOST + name.toLowerCase())
    }

}