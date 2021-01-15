/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.listener

import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import net.evilblock.prisonaio.module.combat.event.PlayerKilledEvent
import net.evilblock.prisonaio.module.combat.logger.event.CombatLoggerDeathEvent
import net.evilblock.prisonaio.module.mine.event.MineBlockBreakEvent
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierHandler
import net.evilblock.prisonaio.module.reward.multiplier.GlobalMultiplierType
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerQuitEvent

object UserStatisticsListeners : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val user = UserHandler.getUser(event.player.uniqueId)

        val multiEvent = GlobalMultiplierHandler.getEvent(GlobalMultiplierType.BLOCKS_MINED)
        if (multiEvent != null) {
            user.statistics.addBlocksMined(multiEvent.multiplier.toInt())
        } else {
            user.statistics.addBlocksMined(1)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMineBlockBreakEvent(event: MineBlockBreakEvent) {
        UserHandler.getUser(event.player.uniqueId).statistics.addBlocksMinedAtMine(event.mine, 1)
    }

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    fun onMultiBlockBreakEvent(event: MultiBlockBreakEvent) {
//        UserHandler.getUser(event.player.uniqueId).statistics.addRawBlocksMined(event.blockList.size)
//    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        UserHandler.getUser(event.player.uniqueId).statistics.syncPlayTime()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerKilledEvent(event: PlayerKilledEvent) {
        val killer = event.killer
        val victim = event.victim

        // prevent kill boosting
        if (DamageTracker.lastKilled.containsKey(killer.uniqueId) && DamageTracker.lastKilled[killer.uniqueId]!!.first == victim.uniqueId) {
            val kills = if (DamageTracker.boosting.containsKey(killer.uniqueId)) {
                DamageTracker.boosting[killer.uniqueId]!!.first
            } else {
                1
            }

            DamageTracker.boosting[killer.uniqueId] = Pair(kills, System.currentTimeMillis())
        }

        val sameAddress = killer.address.address.hostAddress.equals(victim.address.address.hostAddress, ignoreCase = true)
        val sameVictim = DamageTracker.boosting.containsKey(killer.uniqueId) && DamageTracker.boosting[killer.uniqueId]!!.first > 1

        if (UserHandler.isUserLoaded(victim)) {
            UserHandler.getUser(victim).statistics.addDeath()
        }

        if (!sameAddress && !sameVictim) {
            if (UserHandler.isUserLoaded(killer)) {
                UserHandler.getUser(killer).statistics.addKill()
            }

            DamageTracker.lastKilled[killer.uniqueId] = Pair(victim.uniqueId, System.currentTimeMillis())
        }
    }

    @EventHandler
    fun onCombatLoggerDeathEvent(event: CombatLoggerDeathEvent) {
        if (event.killer is Player) {
            val victim = event.logger.owner
            val killer = event.killer

            // prevent kill boosting
            if (DamageTracker.lastKilled.containsKey(killer.uniqueId) && DamageTracker.lastKilled[killer.uniqueId]!!.first === victim.uniqueId) {
                val kills = if (DamageTracker.boosting.containsKey(killer.uniqueId)) {
                    DamageTracker.boosting[killer.uniqueId]!!.first
                } else {
                    1
                }

                DamageTracker.boosting[killer.uniqueId] = Pair(kills, System.currentTimeMillis())
            }

            val sameAddress = killer.address.address.hostAddress.equals(victim.address.address.hostAddress, ignoreCase = true)
            val sameVictim = DamageTracker.boosting.containsKey(killer.uniqueId) && DamageTracker.boosting[killer.uniqueId]!!.first > 1

            if (UserHandler.isUserLoaded(victim)) {
                UserHandler.getUser(victim).statistics.addDeath()
            }

            if (!sameAddress && !sameVictim) {
                if (UserHandler.isUserLoaded(killer)) {
                    UserHandler.getUser(killer).statistics.addKill()
                }

                DamageTracker.lastKilled[killer.uniqueId] = Pair(victim.uniqueId, System.currentTimeMillis())
            }
        }
    }

}