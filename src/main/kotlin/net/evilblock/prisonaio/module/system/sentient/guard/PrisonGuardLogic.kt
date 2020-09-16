/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient.guard

import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.npc.animation.MoveToLocation
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.system.sentient.SentientHandler
import net.evilblock.prisonaio.module.system.sentient.guard.entity.PrisonGuard
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor
import kotlin.random.Random

object PrisonGuardLogic : Runnable {

    override fun run() {
        for (guard in EntityManager.getEntities().filterIsInstance<PrisonGuard>()) {
            try {
                if (System.currentTimeMillis() >= guard.nextPhrase) {
                    guard.nextPhrase = guard.randomTime()
                    guard.updateLines(SentientHandler.getRandomPrisonGuardPhrase())

                    Tasks.asyncDelayed(20L * 5L) {
                        guard.updateLines(listOf(SentientHandler.getPrisonGuardName()))
                    }
                }
            } catch (e: Exception) {
                PrisonAIO.instance.logger.severe("Failed to process phrase logic")
                e.printStackTrace()
            }

            try {
                if (System.currentTimeMillis() >= guard.nextWalk) {
                    if (guard.walkRegion == null) {
                        continue
                    }

                    val region = guard.walkRegion!!
                    val randomX = ThreadLocalRandom.current().nextInt(region.lowerX, region.upperX)
                    val randomZ = ThreadLocalRandom.current().nextInt(region.lowerZ, region.upperZ)

                    val location = findFloor(Location(region.world, randomX.toDouble(), guard.location.y, randomZ.toDouble()).block)
                    if (location == null) {
                        PrisonAIO.instance.logger.info("Failed to find floor location $randomX, ${guard.location.y}, $randomZ")
                        continue
                    }

                    location.x = floor(location.x) + 0.5
                    location.z = floor(location.z) + 0.5

                    guard.animation = MoveToLocation(npc = guard, target = location)

                    if (guard.animation!!.finished) { // failed to find path, try again next tick
                        guard.animation = null
                        PrisonAIO.instance.logger.info("Failed to find path for $randomX, ${guard.location.y}, $randomZ")
                        continue
                    }

                    guard.nextWalk = guard.randomTime()
                }
            } catch (e: Exception) {
                PrisonAIO.instance.logger.severe("Failed to process phrase logic:")
                e.printStackTrace()
            }
        }
    }

    private fun findFloor(block: Block, checks: Int = 10, descend: Boolean = true): Location? {
        var direction = descend
        var checks = checks

        if (direction && checks == 0) {
            direction = false
            checks = 10
        }

        if (!direction && checks <= 0) {
            return null
        }

        return if (block.getRelative(BlockFace.DOWN).type != Material.AIR && isEmpty(block, 2)) {
            block.location
        } else {
            findFloor(block.getRelative(if (descend) { BlockFace.DOWN } else { BlockFace.UP }), checks - 1, direction)
        }
    }

    private fun isEmpty(block: Block, height: Int): Boolean {
        if (height == 0) {
            return true
        }

        return if (block.type == Material.AIR) {
            return isEmpty(block.getRelative(BlockFace.UP), height - 1)
        } else {
            false
        }
    }

}