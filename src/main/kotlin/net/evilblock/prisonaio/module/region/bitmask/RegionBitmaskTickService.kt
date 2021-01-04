package net.evilblock.prisonaio.module.region.bitmask

import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.service.Service
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object RegionBitmaskTickService : Service {

    private val appliedSpeed: MutableMap<UUID, Long> = ConcurrentHashMap()

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                tickPlayer(player)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        cleanup()
    }

    private fun tickPlayer(player: Player) {
        val region = RegionHandler.findRegion(player)
        if (region is BitmaskRegion) {
            if (region.hasBitmask(RegionBitmask.DENY_FLY)) {
                if (player.gameMode != GameMode.CREATIVE) {
                    if (player.isFlying) {
                        player.isFlying = false
                    }

                    if (player.allowFlight) {
                        player.allowFlight = false
                    }
                }
            }

            if (region.hasBitmask(RegionBitmask.DENY_SPEED)) {
                if (player.gameMode != GameMode.CREATIVE) {
                    if (player.walkSpeed != 0.2F) {
                        player.walkSpeed = 0.2F
                    }
                }
            }

            if (region.hasBitmask(RegionBitmask.SPEED)) {
                if (!appliedSpeed.containsKey(player.uniqueId) || System.currentTimeMillis() >= appliedSpeed[player.uniqueId]!! + 3000L) {
                    if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                        player.removePotionEffect(PotionEffectType.SPEED)
                    }

                    player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 8, 1))

                    appliedSpeed[player.uniqueId] = System.currentTimeMillis()
                }
            }
        }
    }

    private fun cleanup() {
        val toRemove = arrayListOf<UUID>()
        for (uuid in appliedSpeed.keys) {
            if (Bukkit.getPlayer(uuid) == null) {
                toRemove.add(uuid)
            }
        }

        for (uuid in toRemove) {
            appliedSpeed.remove(uuid)
        }
    }

}