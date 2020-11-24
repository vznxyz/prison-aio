package net.evilblock.prisonaio.module.minigame.event.game.listener

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.combat.timer.listener.CombatTimerListeners
import net.evilblock.prisonaio.module.minigame.event.game.EventGameHandler
import net.evilblock.prisonaio.module.minigame.event.EventItems
import net.evilblock.prisonaio.module.minigame.event.game.EventGameState
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*

object EventGameListeners : Listener {

    @EventHandler
    fun onPlayerToggleFlightEvent(event: PlayerToggleFlightEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val game = EventGameHandler.getOngoingGame()!!
            if (game.isPlaying(event.player.uniqueId)) {
                if (event.player.gameMode == GameMode.CREATIVE && RegionBypass.hasBypass(event.player)) {
                    RegionBypass.attemptNotify(event.player)
                    return
                }

                event.player.allowFlight = false
                event.player.isFlying = false
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val game = EventGameHandler.getOngoingGame()!!
            if (game.isPlayingOrSpectating(event.player.uniqueId)) {
                for (blockedCommand in CombatTimerListeners.BLOCKED_COMMANDS) {
                    if (event.message.startsWith(blockedCommand, ignoreCase = true)) {
                        event.isCancelled = true
                        event.player.sendMessage("${ChatColor.RED}You can't execute that command while in an event!")
                        return
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (EventGameHandler.isOngoingGame()) {
                val game = EventGameHandler.getOngoingGame()!!
                if (game.isPlayingOrSpectating(event.player.uniqueId)) {
                    if (event.hasItem() && event.item == EventItems.LEAVE_EVENT) {
                        if (game.isPlaying(event.player.uniqueId)) {
                            game.removePlayer(event.player)
                        } else if (game.isSpectating(event.player.uniqueId)) {
                            game.removeSpectator(event.player)
                        }

                        event.player.teleport(PrisonAIO.instance.getSpawnLocation())
                    }

                    if (event.action == Action.RIGHT_CLICK_BLOCK) {
                        if (CONTAINER_TYPES.contains(event.clickedBlock.type)) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.player.uniqueId)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.player.uniqueId)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBucketFillEvent(event: PlayerBucketFillEvent) {
        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.player.uniqueId)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBucketEmptyEvent(event: PlayerBucketFillEvent) {
        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.player.uniqueId)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        if (EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.player.uniqueId)) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onFoodLevelChangeEvent(event: FoodLevelChangeEvent) {
        if (event.entity is Player && EventGameHandler.isOngoingGame() && EventGameHandler.getOngoingGame()!!.isPlayingOrSpectating(event.entity.uniqueId)) {
            event.isCancelled = true
            (event.entity as Player).foodLevel = 20
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (EventGameHandler.isOngoingGame()) {
            if (event.entity is Player) {
                val victim = event.entity as Player
                val damager: Player = getDamageSource(event.damager) ?: return
                val game = EventGameHandler.getOngoingGame()!!
                val victimInGame: Boolean = game.isPlayingOrSpectating(victim.uniqueId)
                val damagerInGame: Boolean = game.isPlayingOrSpectating(damager.uniqueId)

                if (!victimInGame && !damagerInGame) {
                    return
                }

                if (!victimInGame || !damagerInGame) {
                    event.isCancelled = true
                } else {
                    game.handleDamage(victim, damager, event)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player

            if (EventGameHandler.isOngoingGame()) {
                val game = EventGameHandler.getOngoingGame()!!
                if (game.isPlaying(player.uniqueId)) {
                    if (event !is EntityDamageByEntityEvent) {
                        event.isCancelled = true
                    }
                } else if (game.isSpectating(player.uniqueId)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val ongoingGame = EventGameHandler.getOngoingGame()!!
            if (ongoingGame.isPlayingOrSpectating(event.whoClicked.uniqueId)) {
                if (ongoingGame.state != EventGameState.RUNNING) {
                    event.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val ongoingGame = EventGameHandler.getOngoingGame()!!
            if (ongoingGame.isPlayingOrSpectating(event.player.uniqueId)) {
                ongoingGame.handleRespawn(event.player)
            }
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        if (EventGameHandler.isOngoingGame()) {
            val ongoingGame = EventGameHandler.getOngoingGame()!!
            if (ongoingGame.isPlaying(event.player.uniqueId)) {
                ongoingGame.eliminatePlayer(event.player, null)
            } else if (ongoingGame.isSpectating(event.player.uniqueId)) {
                ongoingGame.removeSpectator(event.player)
            }
        }
    }

    private val CONTAINER_TYPES = listOf(
        Material.CHEST,
        Material.TRAPPED_CHEST,
        Material.ENDER_CHEST,
        Material.FURNACE,
        Material.BURNING_FURNACE,
        Material.DISPENSER,
        Material.HOPPER,
        Material.DROPPER,
        Material.BREWING_STAND
    )

    private fun getDamageSource(damager: Entity): Player? {
        var playerDamager: Player? = null
        if (damager is Player) {
            playerDamager = damager
        } else if (damager is Projectile) {
            if (damager.shooter is Player) {
                playerDamager = damager.shooter as Player
            }
        }
        return playerDamager
    }

}