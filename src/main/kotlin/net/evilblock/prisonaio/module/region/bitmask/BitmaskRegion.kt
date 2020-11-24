/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.bitmask

import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.combat.timer.CombatTimer
import net.evilblock.prisonaio.module.combat.timer.CombatTimerHandler
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.ChatColor
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.projectiles.ProjectileSource
import java.lang.reflect.Type

open class BitmaskRegion(id: String, cuboid: Cuboid? = null) : Region(id, cuboid) {

    private var bitmask: Int = 0

    override fun getRegionName(): String {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            return "${ChatColor.GREEN}${ChatColor.BOLD}Safe-Zone"
        }

        return "${ChatColor.RED}${ChatColor.BOLD}Dangerous"
    }

    override fun getPriority(): Int {
        return when {
            hasBitmask(RegionBitmask.SAFE_ZONE) -> {
                999
            }
            hasBitmask(RegionBitmask.DANGER_ZONE) -> {
                50
            }
            else -> {
                0
            }
        }
    }

    override fun getAbstractType(): Type {
        return BitmaskRegion::class.java
    }

    fun getRawBitmask(): Int {
        return bitmask
    }

    fun setBitmask(bitmaskValue: Int) {
        bitmask = bitmaskValue
    }

    fun addBitmask(bitmaskType: RegionBitmask) {
        if (!hasBitmask(bitmaskType)) {
            setBitmask(getRawBitmask() + bitmaskType.bitmaskValue)
        }
    }

    fun removeBitmask(bitmaskType: RegionBitmask) {
        if (hasBitmask(bitmaskType)) {
            setBitmask(getRawBitmask() - bitmaskType.bitmaskValue)
        }
    }

    fun hasBitmask(bitmaskType: RegionBitmask): Boolean {
        return (bitmask and bitmaskType.bitmaskValue) == bitmaskType.bitmaskValue
    }

    override fun onRightClickBlock(player: Player, clickedBlock: Block, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            if (Constants.CONTAINER_TYPES.contains(clickedBlock.type)) {
                cancellable.isCancelled = true
            }

            if (Constants.INTERACTIVE_TYPES.contains(clickedBlock.type)) {
                cancellable.isCancelled = true
            }
        }
    }

    override fun onBlockBreak(player: Player, block: Block, cancellable: Cancellable) {
        if (!hasBitmask(RegionBitmask.ALLOW_BUILD)) {
            cancellable.isCancelled = true
        }
    }

    override fun onBlockPlace(player: Player, block: Block, cancellable: Cancellable) {
        if (!hasBitmask(RegionBitmask.ALLOW_BUILD)) {
            cancellable.isCancelled = true
        }
    }

    override fun onEntityDamage(entity: Entity, cause: EntityDamageEvent.DamageCause, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            cancellable.isCancelled = true
            return
        }
    }

    override fun onEntityDamageEntity(attacker: Entity, victim: Entity, cause: EntityDamageEvent.DamageCause, damage: Double, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            cancellable.isCancelled = true
            victim.fireTicks = 0
            return
        }

        if (hasBitmask(RegionBitmask.DANGER_ZONE)) {
            if (attacker is Player && victim is Player) {
                if (attacker == victim) {
                    cancellable.isCancelled = false
                    return
                }

                if (getCuboid()?.contains(victim.location) == false) {
                    cancellable.isCancelled = true
                    return
                }

                cancellable.isCancelled = false

                val attackerTimer = CombatTimerHandler.getTimer(attacker.uniqueId)
                if (attackerTimer == null) {
                    CombatTimerHandler.trackTimer(CombatTimer(attacker.uniqueId))
                } else {
                    attackerTimer.reset()
                }

                val victimTimer = CombatTimerHandler.getTimer(victim.uniqueId)
                if (victimTimer == null) {
                    CombatTimerHandler.trackTimer(CombatTimer(victim.uniqueId))
                } else {
                    victimTimer.reset()
                }
            }

            return
        }
    }

    override fun onProjectileLaunch(projectile: Projectile, source: ProjectileSource, cancellable: Cancellable) {
        if (hasBitmask(RegionBitmask.SAFE_ZONE)) {
            if (source is Player) {
                cancellable.isCancelled = true
                source.sendMessage("${ChatColor.RED}You can't launch projectiles in a safe-zone.")
            }
        }
    }

    override fun onFoodLevelChange(cancellable: Cancellable) {
        cancellable.isCancelled = hasBitmask(RegionBitmask.SAFE_ZONE)
    }

    override fun onPlayerDeath(player: Player, event: PlayerDeathEvent) {
        // force the respawn
        player.spigot().respawn()

        // remove fire and add kept items back to inventory
        Tasks.delayed(1L) {
            player.fireTicks = 0
            player.inventory.clear()

            player.updateInventory()
        }
    }

    override fun onLeaveRegion(player: Player) {

    }

    override fun onEnterRegion(player: Player) {
//        if (player.gameMode != GameMode.CREATIVE) {
//            player.allowFlight = false
//            player.isFlying = false
//            player.flySpeed = 0.2F
//            player.walkSpeed = 0.2F
//        }
    }

    /*
            if (victim is Player) {
            cancellable.isCancelled = true
        }

        if (victim is ItemFrame) {
            if (attacker is Player) {
                if (RegionListeners.bypassCheck(attacker, cancellable)) {
                    return
                }

                cancellable.isCancelled = true
            }
        }
     */

}