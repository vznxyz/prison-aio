/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.TimeUtil.formatIntoDetailedString
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.mechanic.event.MultiBlockBreakEvent
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.util.nms.RayTrace
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object Laser : AbstractEnchant("laser", "Laser", 1) {

    private val cooldown: MutableMap<UUID, Long?> = HashMap()

    override val iconColor: Color
        get() = Color.RED

    override val textColor: ChatColor
        get() = ChatColor.RED

    override val menuDisplay: Material
        get() = Material.GOLD_INGOT

    override fun getCost(level: Int): Long {
        return (10000 + (level - 1) * 2500).toLong()
    }

    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (cooldown.containsKey(event.player.uniqueId)) {
                val remainingSeconds = ((System.currentTimeMillis() - cooldown[event.player.uniqueId]!!) / 1000).toInt()
                if (remainingSeconds < 30) {
                    event.isCancelled = true
                    sendMessage(event.player, "${ChatColor.RED}You can't use this ability for another " + formatIntoDetailedString(30 - remainingSeconds) + ".")
                    return
                }
            }

            // update cooldown map
            cooldown[event.player.uniqueId] = System.currentTimeMillis()

            // send notification
            sendMessage(event.player, "Activated ability for 10 seconds!")

            // start laser task
            LaserRunnable(event.player).runTaskTimerAsynchronously(PrisonAIO.instance, 2L, 2L)

            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        cooldown.remove(event.player.uniqueId)
    }

    private class LaserRunnable(private val player: Player) : BukkitRunnable() {
        private var executed = 0

        override fun run() {
            if (!player.isOnline) {
                cancel()
                return
            }

            if (executed >= 100) {
                cancel()
                return
            }

            executed++

            val showParticles = executed % 5 == 0 // show particles every 5 executions to prevent fps lag
            val eyeLocation = player.eyeLocation
            val lineOfSight = player.eyeLocation.direction.normalize()
            val rayTrace = RayTrace(eyeLocation.toVector(), lineOfSight)
            val needsDestroyed: MutableList<Block> = ArrayList() // the blocks that need to be destroyed

            for (vec in rayTrace.traverse(20.0, 0.05)) {
                val toLocation = vec.toLocation(player.world)
                // skip bedrock, air, and enderchest (MineCrate) blocks
                if (!toLocation.isChunkLoaded) {
                    continue
                }

                if (toLocation.block.type == Material.BEDROCK || toLocation.block.type == Material.AIR || toLocation.block.type == Material.ENDER_CHEST) {
                    continue
                }

                val regionAtBlock = RegionHandler.findRegion(toLocation)
                if (regionAtBlock.supportsAbilityEnchants() && regionAtBlock.getBreakableCuboid() != null) {
                    if (!regionAtBlock.getBreakableCuboid()!!.contains(toLocation)) {
                        continue
                    }

                    val toBlock = toLocation.block
                    if (!needsDestroyed.contains(toBlock)) {
                        needsDestroyed.add(toBlock)
                    }

                    if (showParticles) {
                        player.spawnParticle(Particle.SPELL_MOB, toLocation, 1)
                    }
                }
            }

            if (needsDestroyed.isNotEmpty()) {
                Tasks.sync {
                    val multiBlockBreakEvent = MultiBlockBreakEvent(player, needsDestroyed.iterator().next(), needsDestroyed, 100F)
                    Bukkit.getPluginManager().callEvent(multiBlockBreakEvent)
                }
            }
        }
    }

}