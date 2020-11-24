/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.jumppad

import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

object JumpPadListeners : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.player.location.block.getRelative(BlockFace.DOWN).type == Material.SPONGE) {
            val region = RegionHandler.findRegion(event.player)
            if (region is BitmaskRegion && region.hasBitmask(RegionBitmask.SAFE_ZONE)) {
                val x = event.player.location.direction.x
                val y = event.player.location.direction.y
                val z = event.player.location.direction.z

                event.player.velocity = Vector(x * MechanicsModule.getJumpPadVelocityX(), y + MechanicsModule.getJumpPadVelocityY(), z * MechanicsModule.getJumpPadVelocityX())
                event.player.playSound(event.player.location, Sound.BLOCK_LADDER_STEP, 10.0F, 5.0F)
                event.player.playEffect(event.player.location, Effect.STEP_SOUND, Material.SPONGE.id)
            }
        }
    }

}