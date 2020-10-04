/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object AbilityArmorListeners : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        AbilityArmorHandler.pendingCheck.add(event.player)
    }

    @EventHandler
    fun onPlayerArmorChangeEvent(event: PlayerArmorChangeEvent) {
        AbilityArmorHandler.pendingCheck.add(event.player)
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        AbilityArmorHandler.equippedSet.remove(event.entity.uniqueId)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        AbilityArmorHandler.equippedSet.remove(event.player.uniqueId)
        AbilityArmorHandler.pendingCheck.remove(event.player)
    }

}