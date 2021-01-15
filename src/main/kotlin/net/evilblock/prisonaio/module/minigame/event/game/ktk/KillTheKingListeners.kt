package net.evilblock.prisonaio.module.minigame.event.game.ktk

import net.evilblock.cubed.util.bukkit.EventUtils
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.combat.damage.DamageTracker
import net.evilblock.prisonaio.module.combat.damage.objects.Damage
import net.evilblock.prisonaio.module.combat.damage.objects.PlayerDamage
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.minigame.event.game.EventGameState
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.util.concurrent.TimeUnit

object KillTheKingListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val ongoingGame = EventGameHandler.getOngoingGame()
            if (ongoingGame is KillTheKingEvent && ongoingGame.isPlaying(event.player.uniqueId)) {
                if (event.itemDrop.itemStack.type == Material.GLASS_BOTTLE) {
                    event.itemDrop.remove()
                } else {
                    event.player.sendMessage("${ChatColor.RED}You can't drop items here!")
                }

                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val ongoingGame = EventGameHandler.getOngoingGame()
            if (ongoingGame is KillTheKingEvent && ongoingGame.isPlaying(event.whoClicked.uniqueId)) {
                if (event.whoClicked.uniqueId != ongoingGame.king.uniqueId && event.slotType == InventoryType.SlotType.ARMOR) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (EventUtils.hasPlayerMoved(event)) {
            if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame() is KillTheKingEvent) {
                val ongoingGame = EventGameHandler.getOngoingGame() as KillTheKingEvent
                if (ongoingGame.state == EventGameState.RUNNING && ongoingGame.isPlaying(event.player.uniqueId)) {
                    if (System.currentTimeMillis() < ongoingGame.startedAt!! + 6000L) {
                        val teleportTo = event.from.clone()
                        teleportTo.yaw = event.to.yaw
                        teleportTo.pitch = event.to.pitch

                        event.isCancelled = true
                        event.player.teleport(teleportTo)
                        return
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val ongoingGame = EventGameHandler.getOngoingGame()
        if (EventGameHandler.isOngoingGame() && ongoingGame is KillTheKingEvent) {
            if (ongoingGame.isPlayingOrSpectating(event.entity.uniqueId)) {
                event.entity.spigot().respawn()
                event.drops.clear()

                if (ongoingGame.isPlaying(event.entity.uniqueId) && ongoingGame.state == EventGameState.RUNNING) {
                    if (event.entity.killer != null) {
                        ongoingGame.eliminatePlayer(event.entity, event.entity.killer)
                    } else {
                        val record: List<Damage> = DamageTracker.getDamageList(event.entity)
                        if (record.isNotEmpty()) {
                            val deathCause = record[record.size - 1]
                            if (deathCause is PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                                ongoingGame.eliminatePlayer(event.entity, Bukkit.getPlayer(deathCause.damager))
                            } else {
                                ongoingGame.eliminatePlayer(event.entity, null)
                            }
                        } else {
                            ongoingGame.eliminatePlayer(event.entity, null)
                        }
                    }
                } else {
                    Tasks.delayed(1L) {
                        if (ongoingGame.votedArena != null) {
                            event.entity.teleport(ongoingGame.votedArena!!.pointA!!)
                        } else {
                            event.entity.teleport(ongoingGame.votedArena!!.spectatorLocation)
                        }
                    }
                }
            }
        }
    }

//    @EventHandler(ignoreCancelled = true)
//    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
//        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame() is SumoEventGame) {
//            val ongoingGame = EventGameHandler.getOngoingGame() as SumoEventGame
//            if (ongoingGame.isCurrentlyFighting(event.player)) {
//                if (System.currentTimeMillis() < ongoingGame.startedAt!! + 6000L) {
//                    event.isCancelled = true
//                    event.player.teleport(event.from)
//                    return
//                }
//
//                if (event.player.location.y <= ongoingGame.getDeathHeight()) {
//                    ongoingGame.eliminatePlayer(event.player, ongoingGame.getOpponent(event.player))
//                }
//            }
//        }
//    }

}