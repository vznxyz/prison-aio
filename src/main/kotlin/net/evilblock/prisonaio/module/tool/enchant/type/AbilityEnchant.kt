/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.tool.enchant.AbstractEnchant
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbilityEnchant(id: String, enchant: String, maxLevel: Int) : AbstractEnchant(id, enchant, maxLevel) {

    internal val useCooldown: MutableMap<UUID, Long> = HashMap()

    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.gameMode != GameMode.CREATIVE) {
                val cooldown: Long = if (isCooldownBasedOnLevel()) {
                    val cooldownMap = readLevelToCooldownMap()

                    cooldownMap.getOrElse(level) {
                        cooldownMap.entries.maxBy { it.key }!!.value
                    }
                } else {
                    readCooldown()
                }

                if (useCooldown.containsKey(event.player.uniqueId)) {
                    val expiry = useCooldown[event.player.uniqueId]!!
                    if (System.currentTimeMillis() < expiry) {
                        val remainingSeconds = ((expiry - System.currentTimeMillis()) / 1000.0).toInt()
                        sendMessage(event.player, "${ChatColor.RED}You can't use this ability for another " + TimeUtil.formatIntoDetailedString(remainingSeconds) + ".")
                        return
                    }
                }

                if (isOnGlobalCooldown(event.player)) {
                    return
                }

                useCooldown[event.player.uniqueId] = System.currentTimeMillis() + cooldown
                resetGlobalCooldown(event.player)
            }
        }
    }

    companion object {
        private var globalCooldown: MutableMap<UUID, Long> = ConcurrentHashMap()

        @JvmStatic
        fun resetGlobalCooldown(player: Player) {
            globalCooldown[player.uniqueId] = System.currentTimeMillis() + 250L
        }

        @JvmStatic
        fun isOnGlobalCooldown(player: Player): Boolean {
            return globalCooldown.containsKey(player.uniqueId) && System.currentTimeMillis() < globalCooldown[player.uniqueId]!!
        }

        @EventHandler
        fun onPlayerQuitEvent(event: PlayerQuitEvent) {
            globalCooldown.remove(event.player.uniqueId)
        }
    }

}