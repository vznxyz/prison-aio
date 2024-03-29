/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorHandler
import net.evilblock.prisonaio.module.mechanic.armor.impl.InmateArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.impl.WardenArmorSet
import net.evilblock.prisonaio.module.tool.enchant.Enchant
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

abstract class AbilityEnchant(id: String, enchant: String, maxLevel: Int) : Enchant(id, enchant, maxLevel) {

    internal val useCooldown: MutableMap<UUID, Long> = HashMap()

    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.gameMode == GameMode.CREATIVE) {
                return
            }

            var cooldown: Long = if (isCooldownBasedOnLevel()) {
                val cooldownMap = readLevelToCooldownMap()

                cooldownMap.getOrElse(level) {
                    cooldownMap.entries.maxBy { it.key }!!.value
                }
            } else {
                readCooldown()
            }

            val equippedSet = AbilityArmorHandler.getEquippedSet(event.player)
            if (equippedSet != null && equippedSet.hasAbility(InmateArmorSet)) {
                cooldown = (cooldown * 0.75).toLong()
            }

            if (useCooldown.containsKey(event.player.uniqueId)) {
                val expiresAt = useCooldown[event.player.uniqueId]!!
                if (System.currentTimeMillis() < expiresAt) {
                    val remainingSeconds = ((expiresAt - System.currentTimeMillis()) / 1000.0).toInt()
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