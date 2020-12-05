package net.evilblock.prisonaio.module.robot.cosmetic

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.robot.RobotsModule
import net.evilblock.prisonaio.module.robot.cosmetic.impl.SkinCosmetic
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object CosmeticHandler : Listener {

    private const val REDIS_KEY = "Robots:CosmeticGrants"

    private val registeredCosmetics: MutableSet<Cosmetic> = hashSetOf()
    private val grantedCosmetics: MutableMap<UUID, MutableSet<Cosmetic>> = hashMapOf()

    fun load() {
        registeredCosmetics.clear()

        for (key in RobotsModule.config.getConfigurationSection("cosmetics.skins").getKeys(false)) {
            registeredCosmetics.add(SkinCosmetic(key))
        }
    }

    /**
     * If the given [cosmetic] is granted to the given [uuid] player.
     */
    fun hasBeenGrantedCosmetic(uuid: UUID, cosmetic: Cosmetic): Boolean {
        if (!grantedCosmetics.containsKey(uuid)) {
            grantedCosmetics[uuid] = fetchGrantedCosmetics(uuid)
            startCacheExpiration(uuid)
        }

        return grantedCosmetics[uuid]!!.contains(cosmetic)
    }

    /**
     * Fetches the given [uuid] player's granted cosmetics from redis.
     */
    private fun fetchGrantedCosmetics(uuid: UUID) : MutableSet<Cosmetic> {
        return Cubed.instance.redis.runRedisCommand { redis ->
            val playerKey = "$REDIS_KEY:player.$uuid"
            if (!redis.exists(playerKey)) {
                return@runRedisCommand hashSetOf()
            }

            val grantedCosmetics = hashSetOf<Cosmetic>()
            for (cosmeticId in redis.lrange(playerKey, 0, -1)) {
                try {
                    grantedCosmetics.add(registeredCosmetics.first { it.getUniqueId() == cosmeticId })
                } catch (e: Exception) {
                    continue
                }
            }

            return@runRedisCommand grantedCosmetics
        }
    }

    /**
     * Grant the given [cosmetic] to the given [uuid] player and write changes to database.
     */
    fun grantCosmetic(uuid: UUID, cosmetic: Cosmetic) {
        if (grantedCosmetics.containsKey(uuid)) {
            grantedCosmetics[uuid]!!.add(cosmetic)
        }

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.lpush("$REDIS_KEY:player.$uuid", cosmetic.getUniqueId())
        }
    }

    /**
     * Wipes all grants and writes changes to database.
     */
    fun wipeGrants() {
        grantedCosmetics.clear()

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.del(REDIS_KEY)
        }
    }

    /**
     * Wipes grants for the given [uuid] player and write changes to database.
     */
    fun wipeGrants(uuid: UUID) {
        grantedCosmetics.remove(uuid)

        Cubed.instance.redis.runRedisCommand { redis ->
            redis.del("$REDIS_KEY:player.$uuid")
        }
    }

    /**
     * Gets a copy of the registered upgrades.
     */
    fun getRegisteredCosmetics(): List<Cosmetic> {
        return registeredCosmetics.toList()
    }

    /**
     * Expires the given [uuid] from the [grantedCosmetics] after 30 seconds if the player isn't logged in.
     */
    private fun startCacheExpiration(uuid: UUID) {
        Tasks.asyncDelayed(20L * 30) {
            if (Bukkit.getPlayer(uuid) == null) {
                grantedCosmetics.remove(uuid)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            grantedCosmetics[event.uniqueId] = fetchGrantedCosmetics(event.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        grantedCosmetics.remove(event.player.uniqueId)
    }

}