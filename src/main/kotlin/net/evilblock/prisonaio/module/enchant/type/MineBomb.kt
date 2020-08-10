/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.event.MineBombExplodeEvent
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.RegionsModule
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import kotlin.random.Random

object MineBomb : AbstractEnchant("mine-bomb", "Mine Bomb", 3), Listener {

    private const val COOLDOWN = 20 * 1000.toLong()

    private val lastFired = HashMap<UUID, Long>()

    init {
        PrisonAIO.instance.server.pluginManager.registerEvents(this, PrisonAIO.instance)
    }

    override val iconColor: Color
        get() = Color.RED

    override val textColor: ChatColor
        get() = ChatColor.RED

    override val menuDisplay: Material
        get() = Material.FIREBALL

    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        if (event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) {
            return
        }

        if (!event.player.isOp && System.currentTimeMillis() - lastFired.getOrDefault(event.player.uniqueId, 0L) <= COOLDOWN) {
            val remainingSeconds = (((lastFired[event.player.uniqueId]!! + COOLDOWN) - System.currentTimeMillis()) / 1000.0).toInt()
            if (remainingSeconds < 30) {
                event.isCancelled = true
                sendMessage(event.player, "${ChatColor.RED}You can't use this ability for another " + TimeUtil.formatIntoDetailedString(remainingSeconds) + ".")
                return
            }
        }

        val region = RegionsModule.findRegion(event.player.location)
        if (!region.supportsAbilityEnchants() || region.getBreakableCuboid() == null) {
            return
        }

        lastFired[event.player.uniqueId] = System.currentTimeMillis()

        val fireball = event.player.world.spawnEntity(event.player.eyeLocation, EntityType.FIREBALL) as Fireball
        fireball.shooter = event.player
        fireball.velocity = event.player.location.direction
        fireball.setMetadata("minebomb", FixedMetadataValue(PrisonAIO.instance, level))

        event.isCancelled = true
    }

    @EventHandler
    fun onFireBallHit(event: ProjectileHitEvent) {
        if (event.entity !is Fireball || !event.entity.hasMetadata("minebomb")) {
            return
        }

        val level: Int = try {
            event.entity.getMetadata("minebomb")[0].asInt()
        } catch (ignored: Exception) {
            return
        }

        if (level <= 0) {
            return
        }

        val location: Location = when {
            event.hitBlock != null -> {
                event.hitBlock.location
            }
            event.hitEntity != null -> {
                event.entity.location
            }
            else -> {
                return
            }
        }

        val fireball = event.entity as Fireball
        if (fireball.shooter !is Player) {
            return
        }

        val player =  fireball.shooter as Player

        val region = RegionsModule.findRegion(location)
        if (!region.supportsAbilityEnchants() || region.getBreakableCuboid() == null) {
            return
        }

        val blocks: MutableList<Block> = ArrayList()
        val largeRadius = level + 1

        for (x in -largeRadius..largeRadius) {
            for (y in -largeRadius..largeRadius) {
                for (z in -largeRadius..largeRadius) {
                    val block = location.world.getBlockAt(location.blockX + x, location.blockY + y, location.blockZ + z)
                    if (block.type == Material.ENDER_CHEST || block.type == Material.AIR || block.type == Material.BEDROCK) {
                        continue
                    }

                    val dist = block.location.distance(location)
                    if (dist <= level || dist <= largeRadius && 85.0 > Random.nextDouble() * 100.0) {
                        if (block.location.distance(location) <= largeRadius && block.type != Material.ENDER_CHEST) {
                            val regionCriteria = region.supportsAbilityEnchants() && region.getBreakableCuboid() != null && region.getBreakableCuboid()!!.contains(block)
                            if (regionCriteria) {
                                blocks.add(block)
                            }
                        }
                    }
                }
            }
        }

        if (blocks.isEmpty()) {
            return
        }

        // broadcast mine bomb event
        val mineBombEvent = MineBombExplodeEvent(player, blocks, location.block, level)
        Bukkit.getPluginManager().callEvent(mineBombEvent)

        // handle cancelled event
        if (mineBombEvent.isCancelled) {
            return
        }

        // broadcast multi block break event
        val multiBlockBreakEvent = MultiBlockBreakEvent(player, mineBombEvent.origin, blocks, 100F)
        multiBlockBreakEvent.call()

        if (!multiBlockBreakEvent.isCancelled) {
            val particle = when {
                level >= 3 -> Particle.EXPLOSION_HUGE
                level >= 2 -> Particle.EXPLOSION_LARGE
                else -> Particle.EXPLOSION_NORMAL
            }

            // spawn the particle
            location.world.spawnParticle(particle, location, 1)
        }
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        if (event.entity is Fireball && event.entity.hasMetadata("minebomb")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFireBallHit(event: EntityDamageByEntityEvent) {
        if (event.damager.type != EntityType.PLAYER || event.entity.type != EntityType.FIREBALL) {
            return
        }

        if (!event.entity.hasMetadata("minebomb")) {
            return
        }

        val fireball = event.entity as Fireball
        if (fireball.shooter !is Player) {
            return
        }

        val shooter = fireball.shooter as Player
        event.damager.sendMessage("${EnchantsManager.CHAT_PREFIX}You have stolen ${ChatColor.RED}${shooter.name}'s ${ChatColor.GRAY}mine bomb!")
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        lastFired.remove(event.player.uniqueId)
    }

    override fun getCost(level: Int): Long {
        return when (level) {
            1 -> 35000
            2 -> 50000
            else -> 70000
        }
    }

}